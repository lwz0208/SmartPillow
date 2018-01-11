package com.lwz.smartpillow;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entity.ViewData;
import fragment.ControlFragment;
import fragment.CurveFragment;
import fragment.MineFragment;
import fragment.NewsFragment;
import okhttp3.Call;
import okhttp3.MediaType;
import utils.BlueDeviceUtils;
import utils.CalculateSignature;
import utils.SharedPrefsUtil;
import utils.URL_UNIVERSAL;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private RadioGroup bottomRg = null;
    private ControlFragment controlFragment;
    private CurveFragment curveFragment;
    private NewsFragment newsFragment;
    private MineFragment mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        bottomRg = (RadioGroup) findViewById(R.id.bottomRg);
        bottomRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.rb_control:
                        transaction.show(controlFragment);
                        transaction.hide(curveFragment);
                        transaction.hide(newsFragment);
                        transaction.hide(mineFragment);
                        break;
                    case R.id.rb_curve:
                        transaction.show(curveFragment);
                        transaction.hide(controlFragment);
                        transaction.hide(newsFragment);
                        transaction.hide(mineFragment);
                        break;
                    case R.id.rb_news:
                        transaction.show(newsFragment);
                        transaction.hide(controlFragment);
                        transaction.hide(curveFragment);
                        transaction.hide(mineFragment);
                        break;
                    case R.id.rb_mine:
                        transaction.show(mineFragment);
                        transaction.hide(controlFragment);
                        transaction.hide(curveFragment);
                        transaction.hide(newsFragment);
                        break;
                    default:
                        break;
                }
                transaction.commit();
            }
        });

        BlueDeviceUtils.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BlueDeviceUtils.mBluetoothAdapter = BlueDeviceUtils.mBluetoothManager.getAdapter();
        if (BlueDeviceUtils.mBluetoothAdapter != null) {
            if (!BlueDeviceUtils.mBluetoothAdapter.isEnabled()) {
                BlueDeviceUtils.mBluetoothAdapter.enable();
            }
        }
        initPermission();
        setDefaultFragment();
    }

    private void initPermission() {
        List<String> mPermissionList = new ArrayList<>();
        String permissions[] = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                mPermissionList.add(perm);
                //进入到这里代表没有权限.
            }
        }

        if (!mPermissionList.isEmpty()) {
            String[] per = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(this, per, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Log.i("info", "onRequestPermissionsResult granted=" + granted);
        }
    }

    private void setDefaultFragment() {
        controlFragment = new ControlFragment();
        curveFragment = new CurveFragment();
        newsFragment = new NewsFragment();
        mineFragment = new MineFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.add(R.id.fl_content, controlFragment);
        transaction.add(R.id.fl_content, curveFragment);
        transaction.add(R.id.fl_content, newsFragment);
        transaction.add(R.id.fl_content, mineFragment);

        transaction.show(controlFragment);
        transaction.hide(curveFragment);
        transaction.hide(newsFragment);
        transaction.hide(mineFragment);

        transaction.commit();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        if(BlueDeviceUtils.mBluetoothAdapter == null || !BlueDeviceUtils.mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this , "蓝牙已被关闭", Toast.LENGTH_SHORT).show();
            BlueDeviceUtils.bluetoothGatt = null;
            BlueDeviceUtils.isConnecting = false;
            BlueDeviceUtils.isLink = false;
            BlueDeviceUtils.bluetoothDevice = null;
            if(BlueDeviceUtils.bluetoothGatt != null)
                BlueDeviceUtils.bluetoothGatt.disconnect();
        }
    }
}
