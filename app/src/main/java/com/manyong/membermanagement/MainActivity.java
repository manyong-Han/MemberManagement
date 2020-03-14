package com.manyong.membermanagement;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.manyong.membermanagement.adapter.MemberPagerAdapter;
import com.manyong.membermanagement.database.dbHandler;
import com.manyong.membermanagement.event.ActivityResultEvent;
import com.manyong.membermanagement.login.LoginActivity;
import com.manyong.membermanagement.util.BusProvider;
import com.manyong.membermanagement.util.LoginSharedPreference;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hanman-yong on 2020-03-11.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    private final String TAG = "MainAct Info";
    private ViewPager mViewPager;
    private String mCurrentPhotoPath;
    private MemberPagerAdapter adapter = new MemberPagerAdapter(getSupportFragmentManager());
    private dbHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tedPermission();

        if (handler == null) {
            handler = dbHandler.open(MainActivity.this);
        }

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new MemberInfoFragment(), "회원 정보");
        adapter.addFragment(new MemberListFragment(), "회원 목록");
        viewPager.setAdapter(adapter);
    }

    // 퍼미션 물어보기. (카메라, 저장소 접근)
    private void tedPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BusProvider.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_logout:
                LoginSharedPreference.removeAttribute(MainActivity.this, LoginActivity.LOGIN_ID);
                LoginSharedPreference.removeAttribute(MainActivity.this, LoginActivity.AUTO_ID);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
