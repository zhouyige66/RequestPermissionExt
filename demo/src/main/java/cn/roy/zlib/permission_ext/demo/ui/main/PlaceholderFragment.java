package cn.roy.zlib.permission_ext.demo.ui.main;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

import cn.roy.zlib.permission_ext.PermissionHelper;
import cn.roy.zlib.permission_ext.RequestPermission;
import cn.roy.zlib.permission_ext.demo.bean.User;
import cn.roy.zlib.permission_ext.demo.util.SettingUIJumpUtils;
import cn.roy.zlib.permission_ext.demo.databinding.FragmentMainBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.sectionLabel;
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        binding.btn.setOnClickListener(view -> {
            new SettingUIJumpUtils(getActivity(), getActivity().getPackageName())
                    .jumpPermissionPage();
        });
        binding.btn1.setOnClickListener(view -> {
            openExternalStoragePermission();

        });
        binding.btn2.setOnClickListener(view -> {
            openExternalStoragePermission2();
        });
        binding.btn3.setOnClickListener(view -> {
            String dirPath = getDirPath("pic");
            if (dirPath != null) {
                Toast.makeText(getActivity(), "路径为：" + dirPath, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btn4.setOnClickListener(view -> {
            User u = getUser("你好");
            if (u == null) {
                return;
            }
            Toast.makeText(getActivity(), u.getName(), Toast.LENGTH_SHORT).show();
        });
        binding.btn5.setOnClickListener(view -> {
            applyMultiPermission();
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PermissionHelper.register(this, getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        PermissionHelper.unRegister(this);
    }

    @RequestPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE},
            autoApply = true,
            applyPermissionCode = 10001,
            applyPermissionTip = "应用需要存储权限用于缓存数据，提高应用使用体验，请授予存储权限",
            lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限"
    )
    public void openExternalStoragePermission() {
        Toast.makeText(getActivity(), "存储权限已获取，可执行操作", Toast.LENGTH_SHORT).show();
    }

    @RequestPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE},
            autoApply = true,
            applyPermissionCode = 10002,
            applyPermissionTip = "应用需要存储权限用于缓存数据，提高应用使用体验，请授予存储权限",
            lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限",
            applyPermissionTipUIClassName = "cn.roy.zlib.permission_ext.demo.component.PermissionTipDialog"
    )
    public void openExternalStoragePermission2() {
        Toast.makeText(getActivity(), "存储权限已获取，可执行操作", Toast.LENGTH_SHORT).show();
    }

    @RequestPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE},
            autoApply = true,
            applyPermissionCode = 10003,
            applyPermissionTip = "应用需要存储权限用于保存用户数据，请授予存储权限",
            lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限"
    )
    public String getDirPath(String path) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + path;
    }

    @RequestPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE},
            autoApply = true,
            applyPermissionCode = 10004,
            applyPermissionTip = "应用需要存储权限用于保存用户数据，请授予存储权限",
            lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限"
    )
    public User getUser(String path) {
        User user = new User();
        user.setAge(29);
        user.setName(path + "，Roy");
        user.setId("001");
        return user;
    }

    @RequestPermission(permissions = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO},
            autoApply = true,
            applyPermissionCode = 10005,
            applyPermissionTip = "应用需要相机、录音机权限用于用户打卡信息，请授予存储权限",
            lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限"
    )
    public void applyMultiPermission() {
        Toast.makeText(getActivity(), "获取多权限成功", Toast.LENGTH_SHORT).show();
    }

}