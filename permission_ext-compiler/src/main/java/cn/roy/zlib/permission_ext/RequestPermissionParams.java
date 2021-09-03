package cn.roy.zlib.permission_ext;

/**
 * @Description: 权限注解参数
 * @Author: Roy Z
 * @Date: 2021/08/09
 * @Version: v1.0
 */
public class RequestPermissionParams {
    private String[] permissions;
    private boolean autoApply;
    private String applyPermissionTip;
    private int applyPermissionCode;
    private String lackPermissionTip;

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public boolean isAutoApply() {
        return autoApply;
    }

    public void setAutoApply(boolean autoApply) {
        this.autoApply = autoApply;
    }

    public String getApplyPermissionTip() {
        return applyPermissionTip;
    }

    public void setApplyPermissionTip(String applyPermissionTip) {
        this.applyPermissionTip = applyPermissionTip;
    }

    public int getApplyPermissionCode() {
        return applyPermissionCode;
    }

    public void setApplyPermissionCode(int applyPermissionCode) {
        this.applyPermissionCode = applyPermissionCode;
    }

    public String getLackPermissionTip() {
        return lackPermissionTip;
    }

    public void setLackPermissionTip(String lackPermissionTip) {
        this.lackPermissionTip = lackPermissionTip;
    }
}
