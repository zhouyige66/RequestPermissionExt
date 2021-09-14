package cn.roy.zlib.permission_ext.demo.component;

import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import cn.roy.zlib.permission_ext.ApplyPermissionTipUI;
import cn.roy.zlib.permission_ext.demo.R;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/09/13
 * @Version: v1.0
 */
public class PermissionTipDialog extends ApplyPermissionTipUI {

    private BottomSheetDialog bottomSheetDialog;

    @Override
    public void display() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(R.layout.layout_apply_permission);
            TextView tvMsg = bottomSheetDialog.findViewById(R.id.tvMsg);
            tvMsg.setText(getTipMessage());
            bottomSheetDialog.findViewById(R.id.btnCancel).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                cancelAction();
            });
            bottomSheetDialog.findViewById(R.id.btnConfirm).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                executeAction();
            });
        }
        bottomSheetDialog.show();
    }
}
