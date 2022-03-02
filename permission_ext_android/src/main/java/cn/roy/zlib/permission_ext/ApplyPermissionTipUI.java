package cn.roy.zlib.permission_ext;

import android.app.Activity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

/**
 * @Description: 申请权限提示UI
 * @Author: Roy Z
 * @Date: 2021/09/13
 * @Version: v1.0
 */
public abstract class ApplyPermissionTipUI implements OnApplyPermissionActionListener {
    private Activity context;
    private String tipMessage;
    private String[] permissions;
    private int applyPermissionCode;

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public String getTipMessage() {
        return tipMessage;
    }

    public void setTipMessage(String tipMessage) {
        this.tipMessage = tipMessage;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public int getApplyPermissionCode() {
        return applyPermissionCode;
    }

    public void setApplyPermissionCode(int applyPermissionCode) {
        this.applyPermissionCode = applyPermissionCode;
    }

    /**
     * 显示申请权限的视图
     */
    public abstract void display();

    @Override
    public void executeAction() {
        ActivityCompat.requestPermissions(context, permissions, applyPermissionCode);
    }

    @Override
    public void cancelAction() {
        Toast.makeText(this.context, tipMessage, Toast.LENGTH_SHORT).show();
    }
}
