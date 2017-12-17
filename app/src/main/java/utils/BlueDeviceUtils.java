package utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;

/**
 * Created by Li Wenzhao on 2017/11/6.
 */

public class BlueDeviceUtils {
    public static boolean isLink = false;
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothManager mBluetoothManager;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothGatt bluetoothGatt;
    public static BluetoothGattService bluetoothGattService;
    public static BluetoothGattCharacteristic bluetoothGattCharacteristic;
}
