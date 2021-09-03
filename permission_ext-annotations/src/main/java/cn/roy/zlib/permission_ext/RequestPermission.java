package cn.roy.zlib.permission_ext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 权限注解
 * @Author: Roy Z
 * @Date: 2021/08/04
 * @Version: v1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface RequestPermission {

    /**
     * 权限集合
     *
     * @return
     */
    String[] permissions() default {};

    /**
     * 是否自动申请权限
     *
     * @return
     */
    boolean autoApply() default false;

    /**
     * 申请权限标识Code
     *
     * @return
     */
    int applyPermissionCode() default 0;

    /**
     * 申请权限前提示语
     *
     * @return
     */
    String applyPermissionTip() default "";

    /**
     * 缺乏权限提示语
     *
     * @return
     */
    String lackPermissionTip() default "";

}
