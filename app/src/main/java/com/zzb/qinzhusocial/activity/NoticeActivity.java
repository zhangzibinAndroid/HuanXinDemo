package com.zzb.qinzhusocial.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zzb.qinzhusocial.R;
import com.zzb.qinzhusocial.adapter.NoticeAdapter;
import com.zzb.qinzhusocial.bean.NoticeBean;
import com.zzb.qinzhusocial.db.DBManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeActivity extends AppCompatActivity {

    @BindView(R.id.lv_notice)
    ListView lvNotice;

    private NoticeAdapter noticeAdapter;
private DBManager dbManager;
    private List<NoticeBean> noticeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        dbManager = new DBManager(this);
        noticeAdapter = new NoticeAdapter(this);











        lvNotice.setAdapter(noticeAdapter);
        noticeList = dbManager.searchData();
        for (int i = 0; i < noticeList.size(); i++) {
            NoticeBean noticeBean = noticeList.get(i);
            noticeAdapter.addDATA(noticeBean);
        }
        noticeAdapter.notifyDataSetChanged();
        noticeAdapter.setOnBtnClickListener(new NoticeAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(View view, final int position, final String name, final Button btn, final TextView textView) {
                Log.e("TAG", "position: "+position );
                Log.e("TAG", "name: "+name );
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(name);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btn.setVisibility(View.GONE);
                                    textView.setVisibility(View.VISIBLE);
                                    dbManager.upDate("yes",(position+1)+"");
                                }
                            });

                        } catch (HyphenateException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NoticeActivity.this, "接受请求异常，请检查网络", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).start();

            }
        });
    }
}
