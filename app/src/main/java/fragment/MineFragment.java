package fragment;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lwz.smartpillow.BLEActivity;
import com.lwz.smartpillow.LoginActivity;
import com.lwz.smartpillow.MainActivity;
import com.lwz.smartpillow.R;
import com.lwz.smartpillow.ResetPasswordActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Calendar;

import okhttp3.Call;
import okhttp3.MediaType;
import utils.CalculateSignature;
import utils.SharedPrefsUtil;
import utils.ToastUtils;
import utils.URL_UNIVERSAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    private TextView tv_exit, tv_account, tv_mailbox, tv_birthday, tv_sex, tv_nickname;
    private RelativeLayout rl_birthday, rl_sex, rl_nickname, rl_resetPassword;
    private Calendar calendar;
    public MineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        tv_exit = (TextView) view.findViewById(R.id.tv_exit);
        tv_account = (TextView) view.findViewById(R.id.tv_account);
        tv_mailbox = (TextView) view.findViewById(R.id.tv_mailbox);
        tv_birthday = (TextView) view.findViewById(R.id.tv_birthday);
        tv_sex = (TextView) view.findViewById(R.id.tv_sex);
        tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);

        rl_birthday = (RelativeLayout) view.findViewById(R.id.rl_birthday);
        rl_sex = (RelativeLayout) view.findViewById(R.id.rl_sex);
        rl_nickname = (RelativeLayout) view.findViewById(R.id.rl_nickname);
        rl_resetPassword = (RelativeLayout) view.findViewById(R.id.rl_resetPassword);

        tv_account.setText(SharedPrefsUtil.getValue(getContext(), "username", ""));
        tv_exit.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        rl_resetPassword.setOnClickListener(this);
        getUserInfo();

        calendar = Calendar.getInstance();
        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("是否退出当前账号");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPrefsUtil.putValue(getContext(), "loginStatus", 0);
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }).setNegativeButton("否", null);
                builder.show();
                break;
            case R.id.rl_birthday:
                new DatePickerDialog(getContext(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // TODO Auto-generated method stub
                                tv_birthday.setText(year + "-"+ (monthOfYear + 1) + "-"+ dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.rl_sex:
                int index;
                if(tv_sex.getText().toString().equals("女"))
                    index = 1;
                else
                    index = 0;
                AlertDialog.Builder builderSex = new AlertDialog.Builder(getContext());
                final String[] items = { "男", "女"};
                builderSex.setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setUserBasicInfo(1 - which, tv_nickname.getText().toString());
                        dialog.dismiss();
                    }
                });
                builderSex.show();
                break;
            case R.id.rl_nickname:
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialog = inflater.inflate(R.layout.dialog_nickname,(ViewGroup) view.findViewById(R.id.dialog));
                final EditText editText = (EditText) dialog.findViewById(R.id.et);
                if(!(tv_nickname.getText().toString().equals("未填写") || tv_nickname.getText().toString().equals("——")))
                    editText.setText(tv_nickname.getText().toString());

                AlertDialog.Builder builderNiackname = new AlertDialog.Builder(getContext());
                builderNiackname.setTitle("填写你的昵称");
                builderNiackname.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setUserBasicInfo(tv_sex.getText().toString().equals("女") ? 0:1, editText.getText().toString());
                    }
                }).setNegativeButton("取消", null);

                builderNiackname.setView(dialog);
                builderNiackname.show();
                break;
            case R.id.rl_resetPassword:
                startActivity(new Intent(getActivity(), ResetPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    private void getUserInfo() {
        OkHttpUtils.get().url(URL_UNIVERSAL.GET_USER_INFO)
                .addParams("telephone", SharedPrefsUtil.getValue(getContext(), "username", ""))
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.i("getUserInfo", "接口访问失败：" + call + "---" + e);
                        ToastUtils.showToast(getContext(), "获取信息失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("getUserInfo", "接口访问成功：" + response);
                        try {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if(code.equals("200") && status.equals("ok")) {
                                JSONObject object = jsonObject.getJSONObject("data");
                                tv_mailbox.setText(object.getString("UEmail"));
                                if(object.get("USex") == null)
                                    tv_sex.setText("未填写");
                                else
                                    tv_sex.setText(object.getIntValue("USex") == 0 ? "女":"男");
                                if(object.get("UName") == null)
                                    tv_nickname.setText("未填写");
                                else
                                    tv_nickname.setText(object.getString("UName"));
                            } else {
                                ToastUtils.showToast(getContext(), message);
                            }
                        } catch (Exception e) {
                            ToastUtils.showToast(getContext(), "获取信息失败");
                        }
                    }
                });
    }

    private void setUserBasicInfo(final int sex, final String nickname) {
        String[] data = CalculateSignature.getSignature().split("@");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("telephone", SharedPrefsUtil.getValue(getContext(), "username", ""));
        jsonObject.put("birthday", "2018-01-01");
        jsonObject.put("sex", sex);
        jsonObject.put("nickname", nickname);
        Log.i("setUserBasicInfo", jsonObject.toJSONString());
        OkHttpUtils.postString().url(URL_UNIVERSAL.SET_USER_BASIC_INFO)
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
                        Log.i("setUserBasicInfo", "接口访问失败：" + call + "---" + e);
                        ToastUtils.showToast(getContext(), "提交信息失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("setUserBasicInfo", "接口访问成功：" + response);
                        try {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if(code.equals("200") && status.equals("ok")) {
                                tv_sex.setText(sex == 0 ? "女":"男");
                                tv_nickname.setText(nickname);
                            } else {
                                ToastUtils.showToast(getContext(), message);
                            }
                        } catch (Exception e) {
                            ToastUtils.showToast(getContext(), "提交信息失败");
                        }
                    }
                });

    }

}
