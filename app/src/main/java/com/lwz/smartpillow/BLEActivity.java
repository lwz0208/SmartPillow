package com.lwz.smartpillow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import adapter.BLEDeviceAdapter;
import entity.BLEDevice;
import utils.BlueDeviceUtils;

public class BLEActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ListView listView;
    private BLEDeviceAdapter adapter;
    private List<BLEDevice> bleDevices = new ArrayList<>();
    //定义对象
    private BluetoothLeScanner bluetoothLeScanner;
    private List<BluetoothDevice> devices = new ArrayList<>();//存放扫描结果
    private Message msg;
    private long SCAN_SECOND = 10000;
    private TextView tv_disconnect, tv_searching;

    //startScan()回调函数,5.0以上使用
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult results) {
            super.onScanResult(callbackType, results);
            BluetoothDevice device = results.getDevice();
            if(device != null) {
                if (!devices.contains(device) && device.getName()!= null) {  //判断是否已经添加
                    devices.add(device);//也可以添加devices.getName()到列表，这里省略            }
                    BLEDevice bleDevice = new BLEDevice(device.getName(), 0);
                    bleDevices.add(bleDevice);
                    adapter.notifyDataSetChanged();
                    // callbackType：回调类型
                    // result：扫描的结果，不包括传统蓝牙        }
                }
            }
        }
    };

    //startScan()回调函数,4.3-5.0间使用
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i,byte[] bytes) {

            if (bluetoothDevice != null){
                //过滤掉其他设备
                if (!devices.contains(bluetoothDevice) && bluetoothDevice.getName()!= null){
                    devices.add(bluetoothDevice);
                    BLEDevice bleDevice = new BLEDevice(bluetoothDevice.getName(), 0);
                    bleDevices.add(bleDevice);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "请打开您的蓝牙",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    scanBleDevice(true);
                    break;
                default:
                    break;
            }
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        msg = new Message();
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
                        BlueDeviceUtils.isLink = false;
                        BlueDeviceUtils.bluetoothDevice = null;
                        BlueDeviceUtils.bluetoothGatt.disconnect();
                        devices.clear();
                        bleDevices.clear();
                        adapter.notifyDataSetChanged();
                        scanBleDevice(true);
                        tv_disconnect.setVisibility(View.INVISIBLE);
                    }
                }).setNegativeButton("取消", null);
                builder.show();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(BlueDeviceUtils.isLink && bleDevices.get(position).getDeviceName().equals(BlueDeviceUtils.bluetoothDevice.getName() + "  (已连接)"))
                    Log.i("info", "点击了自己");
                else {
                    BlueDeviceUtils.isLink = false;
                    BlueDeviceUtils.bluetoothDevice = devices.get(position);
                    setResult(3);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothLeScanner.stopScan(mScanCallback);
                    }
                });
                finish();
            }
        });
        if(BlueDeviceUtils.bluetoothDevice != null && BlueDeviceUtils.isLink) {
            bleDevices.add(new BLEDevice(BlueDeviceUtils.bluetoothDevice.getName() + "  (已连接)", 1));
            devices.add(BlueDeviceUtils.bluetoothDevice);
            tv_disconnect.setVisibility(View.VISIBLE);
        }
        adapter = new BLEDeviceAdapter(getApplicationContext(), bleDevices);
        listView.setAdapter(adapter);

        if(BlueDeviceUtils.mBluetoothAdapter != null && BlueDeviceUtils.mBluetoothAdapter.isEnabled()) {
            msg.what = 1;
            handler.sendMessage(msg);
        } else {
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    private void scanBleDevice(boolean enable){
        //android 5.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            bluetoothLeScanner = BlueDeviceUtils.mBluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(mScanCallback);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeScanner.stopScan(mScanCallback);
                    progressBar.setVisibility(View.INVISIBLE);
                    tv_searching.setVisibility(View.GONE);
                }
            },SCAN_SECOND);

        } else {
            //android 5.0以下
            if (enable){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BlueDeviceUtils.mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                },SCAN_SECOND);
                BlueDeviceUtils.mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                BlueDeviceUtils.mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }
}
