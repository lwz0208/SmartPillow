package com.lwz.smartpillow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import adapter.BLEDeviceAdapter;
import utils.BlueDeviceUtils;

public class BLEActivity extends AppCompatActivity {
    private boolean isScanning = false;
    private ProgressBar progressBar;
    private ListView listView;
    private BLEDeviceAdapter adapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private List<BluetoothDevice> devices = new ArrayList<>();//存放扫描结果
    private long SCAN_SECOND = 10000;
    private TextView tv_disconnect, tv_searching;
    private ImageView iv_back;
    private BluetoothStateBroadcastReceiver mReceiver;

    //startScan()回调函数,5.0以上使用
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult results) {
            super.onScanResult(callbackType, results);
            BluetoothDevice device = results.getDevice();
            if(device != null) {
                if (!devices.contains(device) && device.getName()!= null) {  //判断是否已经添加
                    devices.add(device);//也可以添加devices.getName()到列表，这里省略            }
                    adapter.notifyDataSetChanged();
                    // callbackType：回调类型
                    // result：扫描的结果，不包括传统蓝牙        }
                }
            }
        }
    };

    //startScan()回调函数,4.3-5.0间使用
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice bluetoothDevice, int i,byte[] bytes) {
//
//            if (bluetoothDevice != null){
//                //过滤掉其他设备
//                if (!devices.contains(bluetoothDevice) && bluetoothDevice.getName()!= null){
//                    devices.add(bluetoothDevice);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        }
//    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "请打开您的蓝牙",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    tv_searching.setVisibility(View.GONE);
                    break;
                case 1:
                    scanBleDevice();
                    break;
                case 2:
                    disConnectLink();
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        //透明状态栏
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
                setResult(3);
                finish();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_disconnect = (TextView) findViewById(R.id.tv_disconnect);
        tv_searching = (TextView) findViewById(R.id.tv_searching);
        tv_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BLEActivity.this);
                builder.setMessage("是否断开当前设备连接");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disConnectLink();
                        scanBleDevice();
                    }
                }).setNegativeButton("取消", null);
                builder.show();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(BlueDeviceUtils.isLink && position == 0)
                    Log.i("info", "点击了自己");
                else {
                    BlueDeviceUtils.isLink = false;
                    BlueDeviceUtils.bluetoothDevice = devices.get(position);
                    //将点击的蓝牙挪到第一行
                    if(position != 0) {
                        BluetoothDevice device = devices.get(position);
                        devices.set(position, devices.get(0));
                        devices.set(0, device);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopScan();
                        }
                    });
                    BlueDeviceUtils.isConnecting = true;
                    adapter.notifyDataSetChanged();
                    BlueDeviceUtils.linkBlueDevice(BLEActivity.this);
                }
            }
        });

        if(BlueDeviceUtils.mBluetoothAdapter == null || !BlueDeviceUtils.mBluetoothAdapter.isEnabled()) {
            handler.sendEmptyMessage(0);
        } else {
            if(BlueDeviceUtils.bluetoothDevice != null && BlueDeviceUtils.isLink) {
                devices.add(BlueDeviceUtils.bluetoothDevice);
                tv_disconnect.setVisibility(View.VISIBLE);
            } else {

                scanBleDevice();
            }
        }

        adapter = new BLEDeviceAdapter(getApplicationContext(), devices);
        listView.setAdapter(adapter);
    }

    private void scanBleDevice(){
        //android 5.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isScanning = true;
            progressBar.setVisibility(View.VISIBLE);
            tv_searching.setVisibility(View.VISIBLE);
            bluetoothLeScanner = BlueDeviceUtils.mBluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(mScanCallback);
            handler.postDelayed(runnable, SCAN_SECOND);
        }
//        } else {
//            //android 5.0以下
//            isScanning = true;
//            progressBar.setVisibility(View.VISIBLE);
//            tv_searching.setVisibility(View.VISIBLE);
//            BlueDeviceUtils.mBluetoothAdapter.startLeScan(mLeScanCallback);
//            handler.postDelayed(new Runnable() {
//                @Override
//                   public void run() {
//                    BlueDeviceUtils.mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    isScanning = false;
//                    progressBar.setVisibility(View.INVISIBLE);
//                    tv_searching.setVisibility(View.GONE);
//                }
//            },SCAN_SECOND);
//        }
    }

    private void stopScan() {
        if(isScanning && BlueDeviceUtils.mBluetoothAdapter != null && BlueDeviceUtils.mBluetoothAdapter.isEnabled()) {
            bluetoothLeScanner.stopScan(mScanCallback);
            isScanning = false;
            progressBar.setVisibility(View.INVISIBLE);
            tv_searching.setVisibility(View.GONE);
            handler.removeCallbacks(runnable);
        }
    }

    private void disConnectLink() {
        BlueDeviceUtils.isConnecting = false;
        BlueDeviceUtils.isLink = false;
        BlueDeviceUtils.bluetoothDevice = null;
        if(BlueDeviceUtils.bluetoothGatt != null)
            BlueDeviceUtils.bluetoothGatt.disconnect();
        devices.clear();
        adapter.notifyDataSetChanged();
        tv_disconnect.setVisibility(View.INVISIBLE);
    }

    private class BluetoothStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState){
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(context , "蓝牙已被关闭", Toast.LENGTH_SHORT).show();
                            if(isScanning) {
                                handler.removeCallbacks(runnable);
                            }
                            BlueDeviceUtils.isLink = false;
                            BlueDeviceUtils.isConnecting = false;
                            BlueDeviceUtils.bluetoothGatt = null;
                            isScanning = false;
                            progressBar.setVisibility(View.INVISIBLE);
                            tv_searching.setVisibility(View.GONE);
                            handler.sendEmptyMessage(2);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            handler.sendEmptyMessage(1);
                            //Toast.makeText(context , "蓝牙开启"  , Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case "SUCCESS":
                    //Toast.makeText(BLEActivity.this, "设备连接成功", Toast.LENGTH_SHORT).show();
                    BlueDeviceUtils.isConnecting = false;
                    BlueDeviceUtils.isLink = true;
                    tv_disconnect.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    break;
                case "FAILURE":
                    Toast.makeText(BLEActivity.this, "设备连接失败", Toast.LENGTH_SHORT).show();
                    BlueDeviceUtils.bluetoothDevice = null;
                    BlueDeviceUtils.isLink = false;
                    BlueDeviceUtils.isConnecting = false;
                    tv_disconnect.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(BlueDeviceUtils.mBluetoothAdapter == null || !BlueDeviceUtils.mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this , "蓝牙已被关闭", Toast.LENGTH_SHORT).show();
            BlueDeviceUtils.bluetoothGatt = null;
            isScanning = false;
            progressBar.setVisibility(View.INVISIBLE);
            tv_searching.setVisibility(View.GONE);
            disConnectLink();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mReceiver = new BluetoothStateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        intentFilter.addAction("SUCCESS");
        intentFilter.addAction("FAILURE");
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //销毁在onResume()方法中的广播
        unregisterReceiver(mReceiver);
    }

}
