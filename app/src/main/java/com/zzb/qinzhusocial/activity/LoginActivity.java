package com.zzb.qinzhusocial.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zzb.qinzhusocial.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.edt_username)
    EditText edtUsername;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    private AlertDialog.Builder dialog;
    private AlertDialog dia;
    private View viewDilog;
    private Button btn_reg;
    private EditText et_phone;
    private EditText et_password;
    private EditText et_password_again;
    private Unbinder unbinder;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登陆");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
    }


    private void showRegisterDialog() {
        dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        viewDilog = View.inflate(this, R.layout.dialog_register, null);
        Button btn_dismiss = (Button) viewDilog.findViewById(R.id.btn_dismiss);
        btn_reg = (Button) viewDilog.findViewById(R.id.btn_reg);
        //电话号码
        et_phone = (EditText) viewDilog.findViewById(R.id.et_phone);
        //首次输入密码
        et_password = (EditText) viewDilog.findViewById(R.id.et_password);
        //再次输入密码
        et_password_again = (EditText) viewDilog.findViewById(R.id.et_password_again);
        //加载布局
        dialog.setView(viewDilog);
        dia = dialog.show();
        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_password.getText().toString().equals(et_password_again.getText().toString())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(et_phone.getText().toString(), et_password.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        dia.dismiss();

                                    }
                                });
                            } catch (HyphenateException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "注册异常", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(LoginActivity.this, "两次密码不一致，请重新确认密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.btn_login, R.id.tv_forgetpwds, R.id.tv_registered})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginInterface();
                    }
                }).start();

                break;
            case R.id.tv_forgetpwds:
                break;
            case R.id.tv_registered:
                showRegisterDialog();
                break;
        }
    }

    private void loginInterface() {
        EMClient.getInstance().login(edtUsername.getText().toString(), edtPassword.getText().toString(), new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登录聊天服务器成功！", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登录聊天服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
