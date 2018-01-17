package com.lwz.smartpillow;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;
import utils.CalculateSignature;
import utils.CheckNetwork;
import utils.ToastUtils;
import utils.URL_UNIVERSAL;

public class RegisterActivity extends AppCompatActivity {
    private TextView tv_register;
    private EditText et_account, et_mailbox, et_password;
    private ProgressDialog progressDialog;
    private ImageView iv_back;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //设置状态栏字体颜色为黑色
            window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        tv_register = (TextView) findViewById(R.id.tv_register);
        et_account = (EditText) findViewById(R.id.et_account);
        et_mailbox = (EditText) findViewById(R.id.et_mailbox);
        et_password = (EditText) findViewById(R.id.et_password);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("提交数据中...");

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_account.getText().toString().equals(""))
                    ToastUtils.showToast(getApplicationContext(), "用户名不能为空");
                else if(et_mailbox.getText().toString().equals(""))
                    ToastUtils.showToast(getApplicationContext(), "邮箱不能为空");
                else if(et_password.getText().toString().equals(""))
                    ToastUtils.showToast(getApplicationContext(), "密码不能为空");
                else {
                    if(CheckNetwork.CheckNetState(getApplicationContext())) {
                        progressDialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                register();
                            }
                        }, 1000);
                    } else
                        ToastUtils.showToast(getApplicationContext(), "当前网络不可用");
                }

            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register() {
        String[] data = CalculateSignature.getSignature().split("@");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("telephone", et_account.getText().toString());
        jsonObject.put("password", et_password.getText().toString());
        jsonObject.put("mailbox", et_mailbox.getText().toString());
        Log.i("register", jsonObject.toJSONString());
        OkHttpUtils.postString().url(URL_UNIVERSAL.REGISTER)
                .addHeader("appkey", URL_UNIVERSAL.APPKEY)
                .addHeader("random", data[0])
                .addHeader("timestamp", data[1])
                .addHeader("signature", data[2])
                .content(jsonObject.toJSONString())
                .mediaType(MediaType.parse("application/json"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.i("register", "接口访问失败：" + call + "---" + e);
                        ToastUtils.showToast(getApplicationContext(), "获取数据失败");
                        dialogDismiss();
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("register", "接口访问成功：" + response);
                        try {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if(code.equals("200") && status.equals("ok")) {
                                dialogDismiss();
                                ToastUtils.showToast(getApplicationContext(), "注册成功");
                                Intent intent = new Intent();
                                intent.putExtra("telephone", et_account.getText().toString());
                                intent.putExtra("password", et_password.getText().toString());
                                setResult(2, intent);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1800);

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
}
