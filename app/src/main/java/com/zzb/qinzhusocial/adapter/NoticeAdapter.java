package com.zzb.qinzhusocial.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseImageView;
import com.zzb.qinzhusocial.R;
import com.zzb.qinzhusocial.bean.NoticeBean;
import com.zzb.qinzhusocial.db.DBManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 张梓彬 on 2017/7/6 0006.
 */

public class NoticeAdapter extends MyBaseAdapter<NoticeBean> {
    private static final String TAG = "TAG";
    private OnBtnClickListener onBtnClickListener;
    private DBManager dbManager;
    private ArrayList<NoticeBean> noticeBeanList;
    public NoticeAdapter(Context context) {
        super(context);
        dbManager = new DBManager(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_notice, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        final NoticeBean noticeBean = list.get(position);
        Log.e(TAG, "noticeBean.getName(): "+noticeBean.getName() );

        String isAgree = "";
        noticeBeanList = dbManager.searchDataWithId((position+1)+"");
        Log.e(TAG, "getView: "+noticeBeanList );
        for (int i = 0; i < noticeBeanList.size(); i++) {
            NoticeBean notice = noticeBeanList.get(i);
            isAgree = notice.getIsAgree();
            Log.e(TAG, "getView: "+notice.getIsAgree() );
            Log.e(TAG, "getView: "+notice.get_id() );
            Log.e(TAG, "getView: "+notice.getName() );
            Log.e(TAG, "getView: "+notice.getReason() );
        }

        Log.e(TAG, "agree== "+isAgree );

        if (isAgree!=null){
            if (isAgree.equals("yes")){
                viewHolder.btnAgree.setVisibility(View.GONE);
                viewHolder.tvAgree.setVisibility(View.VISIBLE);
            }else{
                viewHolder.btnAgree.setVisibility(View.VISIBLE);
                viewHolder.tvAgree.setVisibility(View.GONE);
            }
        }

        viewHolder.tv_name.setText(noticeBean.getName());
        viewHolder.tv_reason.setText(noticeBean.getReason());
        viewHolder.btnAgree.setTag(position);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                onBtnClickListener.onBtnClick(v,(int)v.getTag(),noticeBean.getName(), finalViewHolder.btnAgree, finalViewHolder.tvAgree);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.avatar)
        EaseImageView avatar;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_reason)
        TextView tv_reason;
        @BindView(R.id.btn_agree)
        Button btnAgree;
        @BindView(R.id.tv_agree)
        TextView tvAgree;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static interface OnBtnClickListener {
        void onBtnClick(View view, int position,String name,Button button,TextView textView);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        this.onBtnClickListener = listener;

    }


}
