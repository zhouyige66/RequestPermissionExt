package cn.roy.zlib.permission_ext.demo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.roy.zlib.permission_ext.ApplyPermissionTipUI;
import cn.roy.zlib.permission_ext.PermissionHelper;
import cn.roy.zlib.permission_ext.RequestPermissionContextHolder;

/**
 * @Description:
 * @Author:
 * @Date: 2021/09/13
 * @Version: v1.0
 */
public final class Template implements RequestPermissionContextHolder {
    private Object proxy;
    private Context context;

    public Template(Object obj) {
        this.proxy = obj;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void openCamera(String path) {
        Log.d("RequestPermissionExt", "进入代理方法");
        String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"};
        boolean autoApply = true;
        int applyPermissionCode = 10000;
        String applyPermissionTip = "应用需要存储权限、录音机权限，请授予存储权限";
        String lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限";
        String methodName = "openCamera";
        String applyPermissionTipUIClassName = "cn.roy.zlib.permission_ext.demo.component.PermissionTipDialog";
        Class<?>[] methodParams = new Class[]{String.class};
        boolean hasPermission = PermissionHelper.hasPermission(this.context, permissions);
        if (hasPermission) {
            try {
                Log.d("RequestPermissionExt", "执行真实方法");
                Method method = this.proxy.getClass().getDeclaredMethod(methodName + "_real", methodParams);
                method.invoke(this.proxy, path);
            } catch (IllegalAccessException var11) {
                var11.printStackTrace();
            } catch (InvocationTargetException var12) {
                var12.printStackTrace();
            } catch (NoSuchMethodException var13) {
                var13.printStackTrace();
            }
            return;
        }

if (this.context instanceof Activity && autoApply) {
    Activity activity = (Activity) this.context;
    if (!TextUtils.isEmpty(applyPermissionTip)) {
        if (TextUtils.isEmpty(applyPermissionTipUIClassName)) {
            applyPermissionTipUIClassName = "cn.roy.zlib.permission_ext.DefaultApplyPermissionTipUI";
        }
        boolean exception = false;
        try {
            Class<?> aClass = Class.forName(applyPermissionTipUIClassName);
            if (aClass.getSuperclass() ==
                    Class.forName("cn.roy.zlib.permission_ext.ApplyPermissionTipUI")) {
                ApplyPermissionTipUI instance = (ApplyPermissionTipUI) aClass.newInstance();
                instance.setContext(activity);
                instance.setPermissions(permissions);
                instance.setApplyPermissionCode(applyPermissionCode);
                instance.setTipMessage(applyPermissionTip);
                instance.display();
            }
        } catch (ClassNotFoundException e) {
            exception = true;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            exception = true;
            e.printStackTrace();
        } catch (InstantiationException e) {
            exception = true;
            e.printStackTrace();
        } finally {
            if (exception) {
                Toast.makeText(this.context, lackPermissionTip, Toast.LENGTH_SHORT).show();
            }
        }
    } else {
        ActivityCompat.requestPermissions(activity, permissions, applyPermissionCode);
    }
} else {
    Toast.makeText(this.context, lackPermissionTip, Toast.LENGTH_SHORT).show();
}
    }
}
