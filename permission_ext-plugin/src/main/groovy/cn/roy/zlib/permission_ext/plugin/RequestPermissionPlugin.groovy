package cn.roy.zlib.permission_ext.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

class RequestPermissionPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def transform = new RequestPermissionPluginTransform(project)
        def extension = project.extensions.getByType(AppExtension.class)
        // 注册转换器
        extension.registerTransform(transform)
    }

}