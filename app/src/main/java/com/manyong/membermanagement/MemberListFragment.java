package com.manyong.membermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manyong.membermanagement.adapter.MemberListAdapter;
import com.manyong.membermanagement.database.dbHandler;
import com.manyong.membermanagement.item.MemberItem;
import com.manyong.membermanagement.login.LoginActivity;
import com.manyong.membermanagement.util.BusProvider;
import com.manyong.membermanagement.util.LoginSharedPreference;

import java.util.ArrayList;

public class MemberListFragment extends Fragment {
    private final String TAG = "MemberListFragment";

    private RecyclerView member_rcvView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView admin_text;

    private Cursor cursor;

    private dbHandler handler;

    public ArrayList<MemberItem> member_list = new ArrayList<MemberItem>();

    public MemberListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);

        if (handler == null) {
            handler = dbHandler.open(getContext());
        }

        admin_text = (TextView) view.findViewById(R.id.admin_text);
        member_rcvView = (RecyclerView) view.findViewById(R.id.act_rcv_member_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        member_rcvView.setLayoutManager(linearLayoutManager);
        member_rcvView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));

        if(admin_check()){
           admin_text.setVisibility(View.GONE);
        } else {
            member_rcvView.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                memberList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        memberList();

        return view;
    }

    public void memberList() {

        member_list.clear();

        try {
            cursor = handler.member_select();

            while (cursor.moveToNext()) {
                int idx = cursor.getInt(cursor.getColumnIndex("idx"));
                String str_id = cursor.getString(cursor.getColumnIndex("id"));
                String str_password = cursor.getString(cursor.getColumnIndex("password"));
                String str_name = cursor.getString(cursor.getColumnIndex("name"));
                String str_birth_day = cursor.getString(cursor.getColumnIndex("birthday"));
                String str_phone = cursor.getString(cursor.getColumnIndex("phone"));
                String str_classes = cursor.getString(cursor.getColumnIndex("classes"));
                String str_reg_date = cursor.getString(cursor.getColumnIndex("reg_date"));
                String str_profile_image = cursor.getString(cursor.getColumnIndex("profile_image"));

                loadMemberList(idx, str_id, str_password, str_name, str_birth_day, str_phone, str_classes, str_reg_date, str_profile_image);
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "ListFrag - 회원목록 로딩중 오류발생\n" + e.getMessage());
        }
    }

    public void loadMemberList(int idx, String id, String password, String name, String birthday, String phone, String classes, String reg_date, String profile_image) {
        MemberItem myItem = new MemberItem();

        myItem.setIdx(idx);
        myItem.setId(id);
        myItem.setPwd(password);
        myItem.setName(name);
        myItem.setBirthday(birthday);
        myItem.setPhone(phone);
        myItem.setClasses(classes);
        myItem.setReg_date(reg_date);
        myItem.setProfile_image(profile_image);

        member_list.add(myItem);

        member_rcvView.setAdapter(new MemberListAdapter(getActivity(), member_list));
    }

    public boolean admin_check(){
        String my_id = LoginSharedPreference.getAttribute(getContext(), LoginActivity.LOGIN_ID);

        cursor = handler.member_select_one(my_id);
        String str_classes = cursor.getString(cursor.getColumnIndex("classes"));
        cursor.close();

        if(str_classes.equals("관리자")){
            return true;
        } else {
            return false;
        }
    }
}