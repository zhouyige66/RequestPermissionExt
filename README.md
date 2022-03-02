## 简介
* 一个基于APT、Transform API实现的注解处理权限申请的库。
## 原理
* 使用APT处理注解@RequestPermission，解析注解属性，使用javapoet生成判断权限、申请权限的代码
* 自定义Gradle插件，找到包含@RequestPermission注解的方法，使用javassist修改对应原方法名，并插入一个与原方法同名的方法，并调用javapoet生成的类的方法
## 使用方法
1. 项目根目录build.gradle（tag为对应的git tag,如1.0.0）
   ```
   repositories {
        ...
        // TODO 1.添加jitpack仓库地址
        maven { url 'https://jitpack.io' }
   }
   dependencies {
        ···
        // TODO 2.添加插件classpath
        classpath "com.github.zhouyige66.RequestPermissionExt:permission_ext-plugin:tag"
   }
   ```
2. app build.gradle配置（tag为对应的git tag,如1.0.0）
   ```
   plugins {
      ···
      // TODO 3.应用插件
      id 'cn.roy.zlib.permission_ext.plugin'
   }
   dependencies {
      ···
      // TODO 4.添加注解处理器
      annotationProcessor 'com.github.zhouyige66.RequestPermissionExt:permission_ext-compiler:tag'
      // TODO 5.添加注解绑定器
      implementation 'com.github.zhouyige66.RequestPermissionExt:permission_ext:tag'
   }
   ```
3. 代码中使用，请注意PermissionHelper.register与PermissionHelper.unRegister需要成对使用，不一定在activity中使用
   ```
   // TODO 6.绑定辅助器（如activity的onCreate()方法）
   PermissionHelper.register(Object obj, Context context); 
   // TODO 7.解除绑定（如activity的onDestory()方法）
   PermissionHelper.unRegister(Object obj);
   // TODO 8.使用注解
   @RequestPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
         Manifest.permission.RECORD_AUDIO},
         autoApply = true,
         applyPermissionCode = 10000,
         applyPermissionTip = "应用需要存储权限、录音机权限，请授予存储权限",
         lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限"
   )
   public void openCamera(String path) {
     // 业务代码
   }
   ```
4. 待完善功能
 * [ ] 权限申请成功后，暂未实现自动调用业务方法，需要用户自己在onRequestPermissionsResult中根据结果自行调用（后续版本会考虑实现）。
 * [ ] 计划使用ContentProvider的方式实现初始化。
 * [ ] 计划使用独立的授权Activity的方式实现对授权结果的处理。
 * [ ] 计划实现对kotlin的支持。
---
## 关于作者
* Email： zhouyige66@163.com
* 有任何建议或者使用中遇到问题都可以给我发邮件。
