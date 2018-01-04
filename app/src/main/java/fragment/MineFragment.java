package fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lwz.smartpillow.BLEActivity;
import com.lwz.smartpillow.LoginActivity;
import com.lwz.smartpillow.MainActivity;
import com.lwz.smartpillow.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import utils.SharedPrefsUtil;
import utils.ToastUtils;
import utils.URL_UNIVERSAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    private TextView tv_exit, tv_account, tv_mailbox;
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
        tv_account.setText(SharedPrefsUtil.getValue(getContext(), "username", ""));
        tv_exit.setOnClickListener(this);
        getUserInfo();
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
                            } else {
                                ToastUtils.showToast(getContext(), message);
                            }
                        } catch (Exception e) {
                            ToastUtils.showToast(getContext(), "获取信息失败");
                        }
                    }
                });
    }
}
