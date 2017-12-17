package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lwz.smartpillow.R;

import java.util.List;

import entity.BLEDevice;

/**
 * Created by Li Wenzhao on 2017/11/12.
 */

public class BLEDeviceAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<BLEDevice> bleDevices;

    public BLEDeviceAdapter(Context context, List<BLEDevice> bleDevices) {
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
        if (convertView == null)
        {
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

        myViewHolder.tv_device.setText(bleDevices.get(position).getDeviceName());
        if(bleDevices.get(position).getLinkStatus() == 1)
            myViewHolder.tv_device.setTextColor(context.getResources().getColor(R.color.select_red));
        else
            myViewHolder.tv_device.setTextColor(Color.GRAY);
        return convertView;
    }
}
