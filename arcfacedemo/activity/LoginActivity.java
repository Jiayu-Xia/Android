package com.arcsoft.arcfacedemo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.arcfacedemo.session.Session;

public class LoginActivity extends AppCompatActivity {
    private EditText mAccount;
    private EditText mPassword;
    private TextView mLogin;
    private String dPassword;
    private String dAccount;
    private GetRequest getRequest;
    public static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        getRequest=new GetRequest();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dAccount = mAccount.getText().toString().trim();
                dPassword = mPassword.getText().toString().trim();
                Log.e("TAG", "dAccount = " + dAccount + "dPassword = " + dPassword);

                if (TextUtils.isEmpty(dAccount) || TextUtils.isEmpty(dPassword)) {
                    Toast.makeText(LoginActivity.this, "账号或者密码为空", Toast.LENGTH_SHORT).show();
                } else{
                    //retrofit请求登录接口
                    getRequest.login(dAccount, dPassword);
                    //接收handle回调，retrofit的回调信息
                    handler=new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                           Bundle bundle=msg.getData();

                           String userName=bundle.getString("userName");
                           String password=bundle.getString("password");
                           if (userName==null||password==null){
                               Toast.makeText(LoginActivity.this, "账号或者密码不正确，请检查后重新输入", Toast.LENGTH_SHORT).show();
                               return;
                           }

                           Session.getInstance().setToken(userName+password);
                           Session.getInstance().setState(true);
                           startActivity(new Intent(LoginActivity.this,ChooseFunctionActivity.class));
                           finish();
                        }
                    };
                }
            }
        });
    }
    private void initView() {
        mAccount = (EditText)  findViewById(R.id.et_account);
        mPassword = (EditText) findViewById(R.id.et_password);
        mLogin = (TextView) findViewById(R.id.btn_login);
    }
}