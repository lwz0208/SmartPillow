package fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lwz.smartpillow.BLEActivity;
import com.lwz.smartpillow.LoginActivity;
import com.lwz.smartpillow.R;

import utils.SharedPrefsUtil;
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
}
