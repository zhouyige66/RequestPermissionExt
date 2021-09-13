package cn.roy.zlib.permission_ext;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/09/13
 * @Version: v1.0
 */
public interface OnApplyPermissionActionListener {
    /**
     * 执行授权
     */
    void executeAction();

    /**
     * 取消授权
     */
    void cancelAction();
}
