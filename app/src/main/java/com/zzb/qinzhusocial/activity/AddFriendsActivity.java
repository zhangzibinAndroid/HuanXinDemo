package com.zzb.qinzhusocial.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zzb.qinzhusocial.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFriendsActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    @BindView(R.id.edt_name)
    EditText edtName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_addFriend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_addFriend:
                if (edtName.getText().toString().equals("")) {
                    Toast.makeText(this, "用户ID不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().contactManager().addContact(edtName.getText().toString(), "加个好友呗");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddFriendsActivity.this, "发送请求成功，等待对方验证", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddFriendsActivity.this, "添加好友异常" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();

                }
                break;
        }
    }
}
