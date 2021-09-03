package cn.roy.zlib.permission_ext.demo;

import android.Manifest;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cn.roy.zlib.permission_ext.PermissionHelper;
import cn.roy.zlib.permission_ext.RequestPermission;
import cn.roy.zlib.permission_ext.RequestPermissionContextHolder;
import cn.roy.zlib.permission_ext.demo.ui.main.SectionsPagerAdapter;
import cn.roy.zlib.permission_ext.demo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                openCamera("你好，世界！");
            }
        });

        // TODO 6.绑定辅助器
        PermissionHelper.register(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PermissionHelper.unRegister(this);
    }

    @RequestPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO},
            autoApply = true,
            applyPermissionCode = 10000,
            applyPermissionTip = "应用需要存储权限、录音机权限，请授予存储权限",
            lackPermissionTip = "缺乏相应权限，请进入应用管理页面授予相应权限"
    )
    public void openCamera(String path) {
        Toast.makeText(this, "传递参数：" + path, Toast.LENGTH_SHORT).show();
    }

}