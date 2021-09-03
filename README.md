## 简介
* 一个基于APT、Transform API实现的注解处理权限申请的库。
## 使用方法
1. 项目根目录build.gradle
    ```
    repositories {
        ...
         // TODO 1.添加jitpack仓库地址
         maven { url 'https://jitpack.io' }
    }
    dependencies {
         ···
         // TODO 2.添加插件classpath
         classpath "com.github.zhouyige66.RequestPermissionExt:permission_ext-plugin:1.0.0"
    }
    ```
2. app build.gradle配置
    ```
    plugins {
       ···
       // TODO 3.应用插件
       id 'cn.roy.zlib.permission_ext.plugin'
    }
    dependencies {
       ···
       // TODO 4.添加注解处理器
       annotationProcessor 'com.github.zhouyige66.RequestPermissionExt:permission_ext-compiler:1.0.0'
       // TODO 5.添加注解绑定器
       implementation 'com.github.zhouyige66.RequestPermissionExt:permission_ext:1.0.0'
    }
    ```
3. 代码中使用
    ```
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
   权限申请成功后，暂未实现自动调用业务方法，需要用户自己在onRequestPermissionsResult中根据结果自行调用（后续版本会考虑实现）。
---
## 关于作者
* Email： zhouyige66@163.com
* 有任何建议或者使用中遇到问题都可以给我发邮件。
