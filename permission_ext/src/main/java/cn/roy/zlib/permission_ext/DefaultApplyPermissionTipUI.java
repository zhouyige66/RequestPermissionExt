package cn.roy.zlib.permission_ext;

import androidx.appcompat.app.AlertDialog;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/09/13
 * @Version: v1.0
 */
public class DefaultApplyPermissionTipUI extends ApplyPermissionTipUI {
    private AlertDialog alertDialog;

    @Override
    public void display() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(getTipMessage())
                    .setPositiveButton("去授权", (dialog, which) -> {
                        dialog.dismiss();
                        executeAction();
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                        cancelAction();
                    })
                    .create();
        }
        alertDialog.show();
    }

}
