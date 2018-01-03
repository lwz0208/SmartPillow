package com.lwz.smartpillow;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import utils.CalculateSignature;
import utils.CheckNetwork;
import utils.SharedPrefsUtil;
import utils.ToastUtils;
import utils.URL_UNIVERSAL;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_register, tv_login;
    private EditText et_account, et_password;
    private Dialog progressDialog;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);

        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);

        progressDialog = new Dialog(LoginActivity.this,R.style.progress_dialog);
        progressDialog.setContentView(R.layout.loading_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("正在登录");

        if(!TextUtils.isEmpty(SharedPrefsUtil.getValue(this, "username", ""))) {
            et_account.setText(SharedPrefsUtil.getValue(this, "username", ""));
            et_password.setText(SharedPrefsUtil.getValue(this, "password", ""));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 1);
                break;
            case R.id.tv_login:
                if(et_account.getText().toString().equals(""))
                    ToastUtils.showToast(this, "用户名不能为空");
                else if(et_password.getText().toString().equals(""))
                    ToastUtils.showToast(this, "密码不能为空");
                else {
                    if(CheckNetwork.CheckNetState(this)) {
                        progressDialog.show();
                        login(et_account.getText().toString(), et_password.getText().toString());
                    } else
                        ToastUtils.showToast(this, "当前网络不可用");
                }
                break;
        }
    }

    private void login(String telephone, String password) {
        String[] data = CalculateSignature.getSignature().split("@");
        OkHttpUtils.get().url(URL_UNIVERSAL.LOGIN)
                .addHeader("appkey", URL_UNIVERSAL.APPKEY)
                .addHeader("random", data[0])
                .addHeader("timestamp", data[1])
                .addHeader("signature", data[2])
                .addParams("telephone", telephone)
                .addParams("password", password)
                .addParams("loginplace", "wuhan")
                .addParams("logindevice", "iphone6sp")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.i("login", "接口访问失败：" + call + "---" + e);
                        ToastUtils.showToast(getApplicationContext(), "获取数据失败");
                        dialogDismiss();
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("login", "接口访问成功：" + response);
                        try {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if(code.equals("200") && status.equals("ok")) {
                                SharedPrefsUtil.putValue(LoginActivity.this, "username", et_account.getText().toString());
                                SharedPrefsUtil.putValue(LoginActivity.this, "password", et_password.getText().toString());
                                SharedPrefsUtil.putValue(LoginActivity.this, "loginStatus", 1);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                );
                                finish();
                            } else {
                                ToastUtils.showToast(getApplicationContext(), message);
                            }
                        } catch (Exception e) {
                            ToastUtils.showToast(getApplicationContext(), "获取数据失败");
                        }
                        dialogDismiss();
                    }
                });
    }

    private void dialogDismiss() {
        if(progressDialog != null){
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 2:
                String telephone = data.getStringExtra("telephone");
                String password = data.getStringExtra("password");
                et_account.setText(telephone);
                et_password.setText(password);
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        login(et_account.getText().toString(), et_password.getText().toString());
                    }
                }, 1500);
                break;
            default:
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
