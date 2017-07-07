package com.zzb.qinzhusocial.application;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.zhy.autolayout.config.AutoLayoutConifg;
import com.zzb.qinzhusocial.bean.NoticeBean;
import com.zzb.qinzhusocial.db.DBManager;

import java.util.ArrayList;

/**
 * Created by 张梓彬 on 2017/7/5 0005.
 */

public class MyApplication extends Application {
    private static final String TAG = "TAG";
    private DBManager dbManager;
    private ArrayList<NoticeBean> noticeList;

    @Override
    public void onCreate() {
        super.onCreate();
        AutoLayoutConifg.getInstance().useDeviceSize();
        EMOptions options = new EMOptions();
        options.setAutoLogin(false);//不自动登录
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, options);
        dbManager = new DBManager(this);

        initView();
    }




    private void initView() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactInvited(String username, String reason) {
                Message msg = new Message();
                msg.obj = username;
                getFiendHandler.sendMessage(msg);
                noticeList = dbManager.searchData(username);
                String mName = "";
                String mAgree = "";
                for (NoticeBean noticeBean : noticeList) {
                    mName = String.valueOf(noticeBean.name);
                    mAgree = String.valueOf(noticeBean.isAgree);
                }

                if (mName.indexOf(username) != -1 && mAgree.indexOf("true") != -1) {
                    Log.e(TAG, "数据库有数据 ");

                } else {
                    Log.e(TAG, "数据库无数据 ");
                    dbManager.add(username, reason);
                }
            }

            @Override
            public void onFriendRequestAccepted(String s) {
                Message msg = new Message();
                msg.obj = s;
                agreeFiendHandler.sendMessage(msg);

            }

            @Override
            public void onFriendRequestDeclined(String s) {
                Message msg = new Message();
                msg.obj = s;
                refuseFiendHandler.sendMessage(msg);

            }

            @Override
            public void onContactDeleted(String username) {
                Message msg = new Message();
                msg.obj = username;
                delFiendHandler.sendMessage(msg);

            }


            @Override
            public void onContactAdded(String username) {
                Message msg = new Message();
                msg.obj = username;
                addFiendHandler.sendMessage(msg);
            }
        });
    }


    private Handler getFiendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Toast.makeText(MyApplication.this, result + "请求添加您为好友", Toast.LENGTH_SHORT).show();
        }
    };


    private Handler agreeFiendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Toast.makeText(MyApplication.this, result +"已同意您的好友请求", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler refuseFiendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Toast.makeText(MyApplication.this, result +"已拒绝您的好友请求", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler delFiendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Toast.makeText(MyApplication.this, "您与"+result+"不再是好友", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler addFiendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            Toast.makeText(MyApplication.this, "您添加了新的好友"+result, Toast.LENGTH_SHORT).show();
        }
    };




}
