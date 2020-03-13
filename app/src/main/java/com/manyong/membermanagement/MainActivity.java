package com.manyong.membermanagement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.manyong.membermanagement.adapter.MemberPagerAdapter;

/**
 * Created by hanman-yong on 2020-03-11.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    MemberPagerAdapter adapter = new MemberPagerAdapter(getSupportFragmentManager());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
