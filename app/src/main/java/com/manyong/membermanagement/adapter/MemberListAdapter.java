package com.manyong.membermanagement.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manyong.membermanagement.R;
import com.manyong.membermanagement.database.dbHandler;
import com.manyong.membermanagement.item.MemberItem;
import com.manyong.membermanagement.util.DialogSampleUtil;

import java.util.List;

/**
 * Created by hanman-yong on 2020-03-12.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    private Activity activity;
    private List<MemberItem> dataList;
    private dbHandler dbhandler;

    public MemberListAdapter(Activity activity, List<MemberItem> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if (dbhandler == null) {
            dbhandler = dbHandler.open(activity.getApplicationContext());
        }

        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mID;
        TextView mName;
        TextView mReg_Date;
        ImageView mProfile_Image;

        public ViewHolder(View itemView) {
            super(itemView);

            mID = (TextView) itemView.findViewById(R.id.mID);
            mName = (TextView) itemView.findViewById(R.id.mName);
            mReg_Date = (TextView) itemView.findViewById(R.id.mDateTextView);
            mProfile_Image = (ImageView) itemView.findViewById(R.id.member_profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 관리자 승격
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {//Yes
                                dbhandler.member_update(dataList.get(getAdapterPosition()).getId());
                            }
                        }
                    };

                    DialogSampleUtil.showConfirmDialog(activity, "", "선택한 회원을 관리자로 설정하시겠습니까?", handler);

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {//Yes
                                removeMemo(dataList.get(getAdapterPosition()).getId());
                                removeItemView(getAdapterPosition());
                            }
                        }
                    };

                    DialogSampleUtil.showConfirmDialog(activity, "", "선택한 회원을 강퇴 하시겠습니까?", handler);

                    return false;
                }
            });
        }
    }

    private void removeItemView(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size());
    }

    private void removeMemo(String id) {
        dbhandler.member_delete(id);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MemberItem data = dataList.get(position);

        holder.mID.setText(data.getId());
        holder.mName.setText(data.getName());
        holder.mReg_Date.setText(data.getReg_date());

        try {
            Uri profile_uri = Uri.parse(data.getProfile_image());
            holder.mProfile_Image.setImageURI(profile_uri);
        } catch (Exception e) {
            Log.d("imege_null", e.getMessage());
            holder.mProfile_Image.setImageResource(R.drawable.user);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}