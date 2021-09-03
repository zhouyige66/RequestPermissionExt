package cn.roy.zlib.permission_ext.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.gradle.api.Project

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/08/10
 * @Version: v1.0
 */
class RequestPermissionPluginTransform extends Transform {
    Project project
    ClassPool classPool = ClassPool.getDefault()

    RequestPermissionPluginTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "RequestPermissionPluginTransform"
    }

    // 字节码
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 整个项目的处理
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 是否增量编译
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)

        println("transform old")
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        println("开始处理class")
        // 得到Transform的输入:jar、目录
        Collection<TransformInput> inputs = transformInvocation.getInputs()
        println("inputs长度：" + inputs.size())
        // 遍历我们的输入
        inputs.each { TransformInput input ->
            // 得到jar的input，要修改jar里面的字节码需要先解压，然后修改子字节码最后压缩放回原来的位置
            Collection<JarInput> jarInputs = input.getJarInputs()
            println("jarInputs长度：" + jarInputs.size())
            jarInputs.each { jarInput ->
                def jarPath = jarInput.getFile().absolutePath
                println("jar包路径：" + jarPath)
                classPool.appendClassPath(jarPath)
                // 得到输出，必须使用transformInvocation.getOutputProvider()来获取文件的输出供下一个transform使用，不能破坏transform的输入
                File dst = transformInvocation.getOutputProvider().getContentLocation(
                        jarInput.getName(), jarInput.getContentTypes(),
                        jarInput.getScopes(), Format.JAR)
                // 这个不能忘记，插桩完成之后需要将你写的文件重新拷贝到dst中供下一个transform使用
                FileUtils.copyFile(jarInput.getFile(), dst)
            }

            // 得到目录输入
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs()
            println("directoryInputs长度：" + directoryInputs.size())
            // 遍历目录输入
            directoryInputs.each { DirectoryInput directoryInput ->
                // 遍历输入文件
                File src = directoryInput.getFile()
                println("class包路径：" + src.getAbsolutePath())
                // 过滤当前目录中以.class结尾的文件，递归调用文件夹
                def filter = FileFilterUtils.suffixFileFilter(".class")
                Collection<File> files = FileUtils.listFiles(src, filter, TrueFileFilter.INSTANCE)
                println("需要处理的class共有：" + files.size())
                classPool.importPackage("cn.roy.zlib.permission_ext")
                classPool.appendClassPath(src.absolutePath)
                def dirLength = src.absolutePath.length()
                for (File file : files) {
                    def classPath = file.absolutePath
                    println("类路径：" + classPath)
                    // src的path直接定位到编译之后形成的class文件所在的根目录/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes
                    // 除去src根目录之后，后面就是具体的类的目录是以'/'结尾的，我们需要将'/'换成'.'就是全类名了/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes/androidx/activity/R.class
                    def className = classPath.substring(dirLength + 1)
                            .replace(File.separator, ".")
                            .replace(".class", "")
                    println("类名：" + className)
                    try {
                        classPool.appendClassPath(classPath)
                        FileInputStream fis = new FileInputStream(file)
                        // 具体的插桩逻辑
                        byte[] byteCode = referHackWhenInit(fis)
                        fis.close()
                        FileOutputStream fos = new FileOutputStream(classPath)
                        fos.write(byteCode)
                        fos.close()
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
                // 得到输出，必须使用transformInvocation.getOutputProvider()来获取文件的输出供下一个transform使用，不能破坏transform的输入
                File dst = transformInvocation.getOutputProvider().getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(), Format.DIRECTORY)
                // 这个不能忘记，插桩完成之后需要将你写的文件重新拷贝到dst中供下一个transform使用
                FileUtils.copyDirectory(src, dst)
            }
        }
    }

    private byte[] referHackWhenInit(InputStream fis) throws IOException {
        CtClass ctClass = classPool.makeClass(fis)
        def classSimpleName = ctClass.getSimpleName()
        def packageName = ctClass.getPackageName()
        println("正在处理类：" + classSimpleName)
        CtMethod[] declaredMethods = ctClass.getDeclaredMethods()// 获取本类中所有方法
        for (CtMethod method : declaredMethods) {
            def hasAnnotation = method.hasAnnotation("cn.roy.zlib.permission_ext.RequestPermission")
            if (!hasAnnotation) {
                continue
            }

            def methodName = method.getName()
            def returnType = method.returnType
            def modifiers = method.getModifiers()
            def parameterTypes = method.getParameterTypes()
            // 修改方法名
            method.setName(methodName + "_real")
            // 新增一个方法
            CtMethod newMethod = new CtMethod(returnType, methodName, parameterTypes, ctClass)
//            newMethod.setModifiers(modifiers)
            ctClass.addMethod(newMethod)
            try {
                def insertClassName = packageName + "." + classSimpleName + "_RequestPermissionExt"
                println("需要插入类：" + insertClassName)
                def returnStr
                if (returnType == CtClass.voidType) {
                    returnType = String.format("proxy.%s(\$\$);", methodName)
                } else {
                    returnType = String.format("return proxy.%s(\$\$);", methodName)
                }
                println("return语句：" + returnType)
                newMethod.setBody("{" +
                        String.format("%s proxy = (%s)PermissionHelper.get(this);", insertClassName, insertClassName) +
                        returnType +
                        "}")
                ctClass.setModifiers(ctClass.getModifiers() & ~Modifier.ABSTRACT)
            } catch (CannotCompileException e) {
                e.printStackTrace()
            }
        }
        try {
            return ctClass.toBytecode()
        } catch (IOException e) {
            e.printStackTrace()
        } catch (CannotCompileException e) {
            e.printStackTrace()
        } finally {
            if (ctClass != null) {
                ctClass.detach()
            }
        }
    }

}
