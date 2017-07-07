package com.zzb.qinzhusocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.zzb.qinzhusocial.R;
import com.zzb.qinzhusocial.fragment.ChartFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindViews({R.id.tv_message, R.id.tv_person, R.id.tv_chart})
    TextView[] tv_icon;
    private long exitTime = 0;//点击2次返回，退出程序
    private Unbinder unbinder;
    private ChartFragment chartFragment;
    private EaseContactListFragment easeContactListFragment;//联系人
    private Map<String, EaseUser> contactsMap;

    private EaseConversationListFragment easeConversationListFragment;//会话

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        contactsMap = new HashMap<String, EaseUser>();
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        chartFragment = new ChartFragment();
        easeConversationListFragment = new EaseConversationListFragment();//消息列表
        easeContactListFragment = new EaseContactListFragment();

        easeConversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat);
                startActivity(intent);
            }
        });

        getContactUser();
        tv_icon[0].setSelected(true);
        tv_icon[0].setTextColor(getResources().getColor(R.color.sel_selected_true));
        setReplaceFragment(easeConversationListFragment);


        //设置item点击事件
        easeContactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {

            @Override
            public void onListItemClicked(EaseUser user) {
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat);
                startActivity(intent);
            }
        });

        easeContactListFragment.setAddOnClickListener(new EaseContactListFragment.AddOnClickListener() {
            @Override
            public void addOnClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddFriendsActivity.class);
                startActivity(intent);
            }
        });

        easeContactListFragment.setNoticeOnClickListener(new EaseContactListFragment.NoticeOnClickListener() {
            @Override
            public void noticeOnClick(View view) {
                Intent intent = new Intent(MainActivity.this,NoticeActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setReplaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment).commit();
    }

    @OnClick({R.id.tv_message, R.id.tv_person, R.id.tv_chart})
    public void onViewClicked(View view) {
        for (int i = 0; i < tv_icon.length; i++) {
            tv_icon[i].setSelected(false);
            tv_icon[i].setTextColor(getResources().getColor(R.color.sel_selected_false));
        }

        switch (view.getId()) {
            case R.id.tv_message:
                tv_icon[0].setSelected(true);
                tv_icon[0].setTextColor(getResources().getColor(R.color.sel_selected_true));
                setReplaceFragment(easeConversationListFragment);
                break;
            case R.id.tv_person:
                tv_icon[1].setSelected(true);
                tv_icon[1].setTextColor(getResources().getColor(R.color.sel_selected_true));
                setReplaceFragment(easeContactListFragment);
                getContactUser();
                break;
            case R.id.tv_chart:
                tv_icon[2].setSelected(true);
                tv_icon[2].setTextColor(getResources().getColor(R.color.sel_selected_true));
                setReplaceFragment(chartFragment);

                break;
        }
    }

    private void getContactUser() {
        //需要设置联系人列表才能启动fragment
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    easeContactListFragment.clearList();
                    List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    for (int i = 0; i < usernames.size(); i++) {
                        Log.e("TAG", "usernames=" + usernames.get(i));
                        EaseUser easeUser = new EaseUser(usernames.get(i));
                        contactsMap.put("" + i, easeUser);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                easeContactListFragment.setContactsMap(contactsMap);
                            }
                        });
                    }

                } catch (HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "获取联系人失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        Toast.makeText(MainActivity.this, "帐号已经被强制下线", Toast.LENGTH_SHORT).show();
                        runLogin();
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        Toast.makeText(MainActivity.this, "帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        runLogin();
                        // 显示帐号在其他设备登录
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                            //连接不到聊天服务器
                        } else {
                            //当前网络不可用，请检查网络设置
                            Toast.makeText(MainActivity.this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
    }


    private void runLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //点击两次退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {//两秒内再次点击返回则退出
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                tv_icon[0].setSelected(true);
                tv_icon[0].setTextColor(getResources().getColor(R.color.sel_selected_true));
                setReplaceFragment(easeConversationListFragment);

                exitTime = System.currentTimeMillis();
            } else {
                EMClient.getInstance().logout(true);
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
