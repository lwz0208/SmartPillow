package adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lwz.smartpillow.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import utils.BlueDeviceUtils;

/**
 * Created by Li Wenzhao on 2017/11/12.
 */

public class BLEDeviceAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<BluetoothDevice> bleDevices;

    public BLEDeviceAdapter(Context context, List<BluetoothDevice> bleDevices) {
        this.bleDevices = bleDevices;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        public TextView tv_device;
    }

    @Override
    public int getCount() {
        return bleDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return bleDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder myViewHolder = null;
        if (convertView == null) {
            myViewHolder = new ViewHolder();
            // 获取list_item布局文件的视图
            convertView = layoutInflater.inflate(R.layout.ble_device_item, null);
            // 获取控件对象
            myViewHolder.tv_device = (TextView) convertView
                    .findViewById(R.id.tv_device);
            // 设置控件集到convertView
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }

        if(position == 0) {
            if(BlueDeviceUtils.bluetoothDevice != null && BlueDeviceUtils.isLink) {
                myViewHolder.tv_device.setTextColor(context.getResources().getColor(R.color.select_red));
                myViewHolder.tv_device.setText(bleDevices.get(position).getName() + " (已连接)");
            } else if(BlueDeviceUtils.isConnecting) {
                myViewHolder.tv_device.setTextColor(Color.GRAY);
                myViewHolder.tv_device.setText(bleDevices.get(position).getName() + " (正在连接...)");
            } else {
                myViewHolder.tv_device.setTextColor(Color.GRAY);
                myViewHolder.tv_device.setText(bleDevices.get(position).getName());
            }
        } else {
            myViewHolder.tv_device.setTextColor(Color.GRAY);
            myViewHolder.tv_device.setText(bleDevices.get(position).getName());
        }

        return convertView;


    }
}
