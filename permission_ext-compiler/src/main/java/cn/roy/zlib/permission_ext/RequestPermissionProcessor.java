package cn.roy.zlib.permission_ext;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/08/04
 * @Version: v1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RequestPermissionProcessor extends AbstractProcessor {
    private Messager messager;
    private boolean hasProcess = false;
    private Map<String, RequestPermissionClassParams> processMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        processMap = new HashMap<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(RequestPermission.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (hasProcess) {
            log("********************Annotation has processed********************");
            return false;
        }
        log("********************Annotation process start********************");
        Iterator<? extends TypeElement> iterator = annotations.iterator();
        while (iterator.hasNext()) {
            TypeElement typeElement = iterator.next();
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(typeElement);
            log(String.format("需要处理的方法数目为：%d", elements.size()));
            for (Element element : elements) {
                String className = element.getEnclosingElement().toString();
                log("待处理类名：" + className);
                RequestPermissionClassParams requestPermissionClassParams = processMap.get(className);
                if (requestPermissionClassParams == null) {
                    requestPermissionClassParams = new RequestPermissionClassParams();
                    requestPermissionClassParams.setClassName(className);
                    processMap.put(className, requestPermissionClassParams);
                }
                List<RequestPermissionMethodParams> methodParamsList =
                        requestPermissionClassParams.getMethodParamsList();
                if (methodParamsList == null) {
                    methodParamsList = new ArrayList<>();
                    requestPermissionClassParams.setMethodParamsList(methodParamsList);
                }
                RequestPermissionMethodParams requestPermissionMethodParams =
                        new RequestPermissionMethodParams();
                methodParamsList.add(requestPermissionMethodParams);
                String methodName = element.getSimpleName().toString();
                log("待处理方法名：" + methodName);
                requestPermissionMethodParams.setMethodName(methodName);
                // 方法是可执行的，进行类型转换
                ExecutableElement executableElement = (ExecutableElement) element;
                TypeMirror returnType = executableElement.getReturnType();
                log("方法返回类型：" + returnType);
                requestPermissionMethodParams.setReturnType(returnType);
                List<? extends VariableElement> parameters = executableElement.getParameters();
                List<RequestPermissionMethodParams.MethodParameter> methodParameterList = new ArrayList<>();
                requestPermissionMethodParams.setParameterList(methodParameterList);
                if (!parameters.isEmpty()) {
                    for (VariableElement item : parameters) {
                        log("方法参数类型：" + item.asType());
                        log("方法参数名：" + item.getSimpleName());
                        RequestPermissionMethodParams.MethodParameter parameter =
                                new RequestPermissionMethodParams.MethodParameter();
                        parameter.setName(item.getSimpleName().toString());
                        parameter.setType(item.asType());
                        methodParameterList.add(parameter);
                    }
                }
                RequestPermission annotation = element.getAnnotation(RequestPermission.class);
                String[] permissions = annotation.permissions();
                boolean autoApply = annotation.autoApply();
                String applyPermissionTip = annotation.applyPermissionTip();
                int applyPermissionCode = annotation.applyPermissionCode();
                String lackPermissionTip = annotation.lackPermissionTip();
                RequestPermissionParams requestPermissionParams = new RequestPermissionParams();
                requestPermissionParams.setPermissions(permissions);
                requestPermissionParams.setAutoApply(autoApply);
                requestPermissionParams.setApplyPermissionTip(applyPermissionTip);
                requestPermissionParams.setApplyPermissionCode(applyPermissionCode);
                requestPermissionParams.setLackPermissionTip(lackPermissionTip);
                requestPermissionMethodParams.setAnnotationParams(requestPermissionParams);
            }
        }
        productClass();
        log("********************Annotation process end********************");
        hasProcess = true;
        return true;
    }

    private void log(String text) {
        messager.printMessage(Diagnostic.Kind.NOTE, text + "\n");
    }

    private void productClass() {
        log("开始生成扩展类");
        ClassName Activity = ClassName.get("android.app", "Activity");
        ClassName Context = ClassName.get("android.content", "Context");
        ClassName TextUtils = ClassName.get("android.text", "TextUtils");
        ClassName Log = ClassName.get("android.util", "Log");
        ClassName Toast = ClassName.get("android.widget", "Toast");

        ClassName AlertDialog = ClassName.get("androidx.appcompat.app", "AlertDialog");
        ClassName ActivityCompat = ClassName.get("androidx.core.app", "ActivityCompat");
        ClassName InvocationTargetException = ClassName.get("java.lang.reflect", "InvocationTargetException");
        ClassName Method = ClassName.get("java.lang.reflect", "Method");
        ClassName PermissionHelper = ClassName.get("cn.roy.zlib.permission_ext", "PermissionHelper");
        ClassName RequestPermissionContextHolder = ClassName.get("cn.roy.zlib.permission_ext", "RequestPermissionContextHolder");

        log("生成扩展类的个数：" + processMap.values().size());
        for (RequestPermissionClassParams classParams : processMap.values()) {
            // 构造方法
            MethodSpec constructor = MethodSpec.methodBuilder("setContext")
                    .returns(void.class)
                    .addParameter(ParameterSpec.builder(Context, "context").build())
                    .addStatement("this.context = context")
                    .addModifiers(Modifier.PUBLIC)
                    .build();
            List<RequestPermissionMethodParams> methodParamsList = classParams.getMethodParamsList();
            List<MethodSpec> methodSpecList = new ArrayList<>(methodParamsList.size());
            for (RequestPermissionMethodParams methodParams : methodParamsList) {
                String methodName = methodParams.getMethodName();
                log("处理方法：" + methodName);
                RequestPermissionParams annotationParams = methodParams.getAnnotationParams();
                TypeMirror returnType = methodParams.getReturnType();
                List<RequestPermissionMethodParams.MethodParameter> parameterList = methodParams.getParameterList();
                List<ParameterSpec> parameterSpecList = new ArrayList<>(parameterList.size());
                List<String> parameterClassList = new ArrayList<>();
                for (RequestPermissionMethodParams.MethodParameter methodParameter : parameterList) {
                    TypeName parameterType = ClassName.get(methodParameter.getType());
                    ParameterSpec parameterSpec = ParameterSpec.builder(parameterType,
                            methodParameter.getName()).build();
                    parameterSpecList.add(parameterSpec);
                    parameterClassList.add(parameterType.toString() + ".class");
                }
                String permissions = Arrays.stream(annotationParams.getPermissions())
                        .map(e -> String.format("\"%s\"", e))
                        .collect(Collectors.joining(","));
                log("转换后：" + permissions);
                String returnStr = "";
                String returnStr2 = "";
                if (returnType.toString().equals("void")) {
                    returnStr = "method.invoke(context, path);";
                    returnStr2 = "return;";
                } else {
                    returnStr = "return (" + returnType.toString() + ")method.invoke(context, path);";
                    returnStr2 = "return null;";
                }
                CodeBlock block = CodeBlock.builder().add("if (hasPermission) {\n" +
                        "    try {\n" +
                        "        Log.d(\"RequestPermissionExt\",\"执行真实方法\");\n" +
                        "        $T method = context.getClass()\n" +
                        "                .getDeclaredMethod(methodName + \"_real\", methodParams);\n" +
                        "        $L\n" +
                        "    } catch (IllegalAccessException e) {\n" +
                        "        e.printStackTrace();\n" +
                        "    } catch ($T e) {\n" +
                        "        e.printStackTrace();\n" +
                        "    } catch (NoSuchMethodException e) {\n" +
                        "        e.printStackTrace();\n" +
                        "    }\n" +
                        "    $L\n" +
                        "}", Method, returnStr, InvocationTargetException, returnStr2).build();

                CodeBlock block2 = CodeBlock.builder().add("if (context instanceof $T && autoApply) {\n" +
                                "    Activity activity = (Activity) context;\n" +
                                "    if (!$T.isEmpty(applyPermissionTip)) {\n" +
                                "        new $T.Builder(context)\n" +
                                "                .setMessage(applyPermissionTip)\n" +
                                "                .setPositiveButton(\"确定\", (dialog, which) -> {\n" +
                                "                    dialog.dismiss();\n" +
                                "                    $T.requestPermissions(activity, permissions,\n" +
                                "                            applyPermissionCode);\n" +
                                "                }).setNegativeButton(\"取消\", (dialog, which) -> dialog.dismiss())\n" +
                                "                .show();\n" +
                                "    } else {\n" +
                                "        ActivityCompat.requestPermissions(activity, permissions, applyPermissionCode);\n" +
                                "    }\n" +
                                "} else {\n" +
                                "    $T.makeText(context, lackPermissionTip, Toast.LENGTH_SHORT).show()",
                        Activity, TextUtils, AlertDialog, ActivityCompat, Toast).build();
                MethodSpec method = MethodSpec.methodBuilder(methodName)
                        .returns(ClassName.get(returnType))
                        .addModifiers(Modifier.PUBLIC)
                        .addParameters(parameterSpecList)
                        .addStatement("$T.d(\"RequestPermissionExt\",\"进入代理方法\")", Log)
                        .addStatement("$T[] permissions = {$L}", String.class, permissions)
                        .addStatement("boolean autoApply = $L", annotationParams.isAutoApply())
                        .addStatement("int applyPermissionCode = $L", annotationParams.getApplyPermissionCode())
                        .addStatement("String applyPermissionTip = $S", annotationParams.getApplyPermissionTip())
                        .addStatement("String lackPermissionTip = $S", annotationParams.getLackPermissionTip())
                        .addStatement("String methodName = $S", methodName)
                        .addStatement("Class<?>[] methodParams = {$L}", parameterClassList.stream().collect(Collectors.joining(",")))
                        .addStatement("boolean hasPermission = $T.hasPermission(context, permissions)", PermissionHelper)
                        .addStatement(block)
                        .addStatement(block2)
                        .addCode("}\n")
                        .addCode("$L\n", returnStr2)
                        .build();
                methodSpecList.add(method);
            }
            // 属性
            FieldSpec fieldSpec = FieldSpec.builder(Context, "context", Modifier.PRIVATE).build();
            // 类信息
            String className = classParams.getClassName();
            int index = className.lastIndexOf(".");
            String packageName = className.substring(0, index);
            String newClassName = className.substring(index + 1) + "_RequestPermissionExt";
            log("创建新类，包名：" + packageName);
            log("创建新类，类名：" + newClassName);
            TypeSpec typeSpec = TypeSpec.classBuilder(newClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(RequestPermissionContextHolder)
                    .addField(fieldSpec)
                    .addMethod(constructor)
                    .addMethods(methodSpecList)
                    .build();
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
                log("生成类文件发生异常：" + e.getMessage());
            }
        }
    }

}