package com.lwz.smartpillow;

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
import utils.SharedPrefsUtil;
import utils.ToastUtils;
import utils.URL_UNIVERSAL;

public class ResetPasswordActivity extends AppCompatActivity {
    private ImageView iv_back;
    private TextView tv_submit;
    private EditText et_old, et_new;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et_old = (EditText) findViewById(R.id.et_old);
        et_new = (EditText) findViewById(R.id.et_new);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_old.getText().toString().equals("") || et_new.getText().toString().equals(""))
                    ToastUtils.showToast(getApplicationContext(), "密码不能为空");
                else {
                    if(CheckNetwork.CheckNetState(getApplicationContext())) {
                        resetPassword();
                    } else
                        ToastUtils.showToast(getApplicationContext(), "当前网络不可用");
                }
            }
        });
    }

    private void resetPassword() {
        String[] data = CalculateSignature.getSignature().split("@");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("telephone", SharedPrefsUtil.getValue(this, "username", ""));
        jsonObject.put("oldpsd", et_old.getText().toString());
        jsonObject.put("newpsd", et_new.getText().toString());
        //Log.i("resetPassword", jsonObject.toJSONString());
        OkHttpUtils.postString().url(URL_UNIVERSAL.RESET_PASSWORD)
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
                        Log.i("resetPassword", "接口访问失败：" + call + "---" + e);
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("resetPassword", "接口访问成功：" + response);
                        try {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            if(code.equals("200") && status.equals("ok")) {
                                ToastUtils.showToast(getApplicationContext(), "密码修改成功");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1500);
                            } else {
                                ToastUtils.showToast(getApplicationContext(), "密码输入错误");
                            }
                        } catch (Exception e) {
                            ToastUtils.showToast(getApplicationContext(), "提交数据失败");
                        }
                    }
                });

    }


}
