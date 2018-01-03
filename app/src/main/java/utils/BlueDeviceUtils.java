package utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Li Wenzhao on 2017/11/6.
 */

public class BlueDeviceUtils {
    public static boolean isLink = false;
    public static boolean isConnecting = false;
    public static BluetoothManager mBluetoothManager;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothGatt bluetoothGatt;
    public static BluetoothGattService bluetoothGattService;
    public static BluetoothGattCharacteristic bluetoothGattCharacteristic;

    public static void linkBlueDevice(final Context context) {
        BlueDeviceUtils.bluetoothGatt = BlueDeviceUtils.bluetoothDevice.connectGatt(context.getApplicationContext(), false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {//状态变为 已连接
                    Log.i("onConnectionStateChange", "连接成功");
                    gatt.discoverServices();//连接成功，开始搜索服务，一定要调用此方法，否则获取不到服务
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) { //状态变为 未连接
                    BlueDeviceUtils.isLink = false;
                    BlueDeviceUtils.isConnecting = false;
                    BlueDeviceUtils.bluetoothGatt.close();
                    BlueDeviceUtils.bluetoothGatt = null;
                    BlueDeviceUtils.bluetoothGattService = null;
                    BlueDeviceUtils.bluetoothGattCharacteristic = null;
                    BlueDeviceUtils.bluetoothDevice = null;
                    Log.i("onConnectionStateChange", "连接断开");
                }
                return;
            }

            public void onServicesDiscovered(BluetoothGatt gatt, final int status) {
                //发现服务后的响应函数
                super.onServicesDiscovered(gatt, status);
                Log.i("onServicesDiscovered", "------------------------");
                String service_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";//已知服务
                String characteristic_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";//已知特征
                BlueDeviceUtils.bluetoothGattService = BlueDeviceUtils.bluetoothGatt.getService(UUID.fromString(service_UUID));//通过UUID找到服务
                BlueDeviceUtils.bluetoothGattCharacteristic = BlueDeviceUtils.bluetoothGattService.getCharacteristic(UUID.fromString(characteristic_UUID));//找到服务后在通过UUID找到特征
                if (BlueDeviceUtils.bluetoothGattCharacteristic != null) {
                    gatt.setCharacteristicNotification(BlueDeviceUtils.bluetoothGattCharacteristic, true);//启用onCharacteristicChanged(），用于接收数据
                    Intent intent = new Intent();
                    intent.setAction("SUCCESS");
                    context.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("FAILURE");
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                //用此函数接收数据
                super.onCharacteristicChanged(gatt, characteristic);
//                byte[] bytesreceive = characteristic.getValue();
//                for(int i = 0; i < bytesreceive.length; i++)
//                    Log.i("info", bytesreceive[i] + "-");
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    //写入成功
                    Log.i("onCharacteristicWrite", "写入成功");
                } else if(status == BluetoothGatt.GATT_FAILURE) {
                    //写入失败
                    Log.i("onCharacteristicWrite", "写入失败");
                }
            }
        });
    }
}
