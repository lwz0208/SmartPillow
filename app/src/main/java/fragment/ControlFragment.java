package fragment;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lwz.smartpillow.BLEActivity;
import com.lwz.smartpillow.R;
import com.lwz.smartpillow.Shanxing;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import baiduvoice.BaiduASRDigitalDialog;
import baiduvoice.ChainRecogListener;
import baiduvoice.CommonRecogParams;
import baiduvoice.DigitalDialogInput;
import baiduvoice.IStatus;
import baiduvoice.Logger;
import baiduvoice.MessageStatusRecogListener;
import baiduvoice.MyRecognizer;
import baiduvoice.OnlineRecogParams;
import baiduvoice.SimpleTransApplication;
import baiduvoice.StatusRecogListener;
import entity.ViewData;
import utils.BlueDeviceUtils;
import utils.URL_UNIVERSAL;

import static android.app.Activity.RESULT_OK;

public class ControlFragment extends Fragment implements View.OnClickListener, IStatus {
    private Shanxing shanxing;
    private ArrayList<ViewData> viewDatas = new ArrayList<>();
    private int currentVoice;
    private TextView tv_test;
    private TextView tv_status1, tv_status2, tv_status3, tv_status4, tv_direction, tv_direction_anti, tv_musicVoice;
    private ImageView iv_play, iv_left, iv_right, iv_switch, iv_link, iv_voice, iv_light, iv_voicd_add, iv_voicd_less;
    private boolean isOpen, isPlaying, isLightOpen;
    private Message msg;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleMsg(msg);
            switch (msg.what) {
                case 30:
                    Toast.makeText(getActivity().getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    BlueDeviceUtils.isLink = true;
                    break;
                case 40:
                    Toast.makeText(getActivity().getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    BlueDeviceUtils.bluetoothDevice = null;
                    BlueDeviceUtils.isLink = false;
                    break;
            }
        }
    };

    //百度语音变量
    private MyRecognizer myRecognizer;
    private CommonRecogParams apiParams;
    private int status;
    private DigitalDialogInput input;
    private ChainRecogListener listener;
    private CommonRecogParams getApiParams() {
        return new OnlineRecogParams(getActivity());
    }

    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    private void start() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Map<String, Object> params = apiParams.fetch(sp);  // params可以手动填入
        // BaiduASRDigitalDialog的输入参数
        input = new DigitalDialogInput(myRecognizer, listener, params);
        Intent intent = new Intent(getActivity(), BaiduASRDigitalDialog.class);
        // 在BaiduASRDialog中读取
        ((SimpleTransApplication) getActivity().getApplicationContext()).setDigitalDialogInput(input);
        startActivityForResult(intent,2);
    }


    /**
     * 开始录音后，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用。
     */
    private void stop() {
        myRecognizer.stop();
    }

    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    private void cancel() {
        myRecognizer.cancel();
    }

    public ControlFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化百度语音sdk
        listener = new ChainRecogListener();
        listener.addListener(new MessageStatusRecogListener(handler));
        myRecognizer = new MyRecognizer(getContext(), listener);
        apiParams = getApiParams();
        status = STATUS_NONE;

        View view = inflater.inflate(R.layout.fragment_control, container, false);
        isOpen = isPlaying = isLightOpen = false;

        shanxing = (Shanxing) view.findViewById(R.id.shanxing);
        for (int i = 0; i < 6; i++) {
            ViewData data = new ViewData(1, i + 1 + "");
            viewDatas.add(data);
        }
        shanxing.setData(viewDatas);
        shanxing.setOnViewClick(new Shanxing.onViewClick() {
            @Override
            public void onClick(float scrollX, float scrollY, int pressType) {
                if (pressType != -1) {
                    if (pressType == 0) {
                        if (isOpen) {
                            iv_switch.setImageResource(R.drawable.switch_close);
                            isOpen = false;
                            sendDataToBlueDevice(URL_UNIVERSAL.SWITCH_CLOSE);
                        } else {
                            iv_switch.setImageResource(R.drawable.switch_open);
                            isOpen = true;
                            sendDataToBlueDevice(URL_UNIVERSAL.SWITCH_OPEN);
                        }
                    } else {
                        switch (pressType) {
                            case 1:
                                sendDataToBlueDevice(URL_UNIVERSAL.SPEED_ONE);
                                break;
                            case 2:
                                sendDataToBlueDevice(URL_UNIVERSAL.SPEED_TWO);
                                break;
                            case 3:
                                sendDataToBlueDevice(URL_UNIVERSAL.SPEED_THREE);
                                break;
                            case 4:
                                sendDataToBlueDevice(URL_UNIVERSAL.SPEED_FOUR);
                                break;
                            case 5:
                                sendDataToBlueDevice(URL_UNIVERSAL.SPEED_FIVE);
                                break;
                            case 6:
                                sendDataToBlueDevice(URL_UNIVERSAL.SPEED_SIX);
                                break;
                        }
                    }
                }
            }
        });

        tv_status1 = (TextView) view.findViewById(R.id.tv_status1);
        tv_status2 = (TextView) view.findViewById(R.id.tv_status2);
        tv_status3 = (TextView) view.findViewById(R.id.tv_status3);
        tv_status4 = (TextView) view.findViewById(R.id.tv_status4);
        tv_direction = (TextView) view.findViewById(R.id.tv_direction);
        tv_direction_anti = (TextView) view.findViewById(R.id.tv_direction_anti);
        tv_musicVoice = (TextView) view.findViewById(R.id.tv_musicVoice);
        tv_test = (TextView) view.findViewById(R.id.tv_test);

        iv_play = (ImageView) view.findViewById(R.id.iv_play);
        iv_left = (ImageView) view.findViewById(R.id.iv_left);
        iv_right = (ImageView) view.findViewById(R.id.iv_right);
        iv_switch = (ImageView) view.findViewById(R.id.iv_switch);
        iv_link = (ImageView) view.findViewById(R.id.iv_link);
        iv_voice = (ImageView) view.findViewById(R.id.iv_voice);
        iv_light = (ImageView) view.findViewById(R.id.iv_light);
        iv_voicd_add = (ImageView) view.findViewById(R.id.iv_voicd_add);
        iv_voicd_less = (ImageView) view.findViewById(R.id.iv_voicd_less);

        tv_direction.setOnClickListener(this);
        tv_direction_anti.setOnClickListener(this);
        tv_status1.setOnClickListener(this);
        tv_status2.setOnClickListener(this);
        tv_status3.setOnClickListener(this);
        tv_status4.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        iv_link.setOnClickListener(this);
        iv_light.setOnClickListener(this);
        iv_voicd_add.setOnClickListener(this);
        iv_voicd_less.setOnClickListener(this);
        iv_voice.setOnClickListener(this);

        msg = new Message();

        currentVoice = 2;
        tv_musicVoice.setText("音量："+ String.valueOf(currentVoice));

        return view;
    }

    private void setModeTextColor(int type) {
        switch (type) {
            case 0:
                tv_status1.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status2.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status3.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status4.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status1.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status2.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status3.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status4.setBackgroundResource(R.color.gray_bg1_alpha);
                break;
            case 1:
                tv_status1.setBackgroundResource(R.drawable.mode_sselect_bg);
                tv_status2.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status3.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status4.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status1.setTextColor(getResources().getColor(R.color.white));
                tv_status2.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status3.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status4.setTextColor(getResources().getColor(R.color.bottom_text_color));
                break;
            case 2:
                tv_status1.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status2.setBackgroundResource(R.drawable.mode_sselect_bg);
                tv_status3.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status4.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status1.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status2.setTextColor(getResources().getColor(R.color.white));
                tv_status3.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status4.setTextColor(getResources().getColor(R.color.bottom_text_color));
                break;
            case 3:
                tv_status1.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status2.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status3.setBackgroundResource(R.drawable.mode_sselect_bg);
                tv_status4.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status1.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status2.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status3.setTextColor(getResources().getColor(R.color.white));
                tv_status4.setTextColor(getResources().getColor(R.color.bottom_text_color));
                break;
            case 4:
                tv_status1.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status2.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status3.setBackgroundResource(R.color.gray_bg1_alpha);
                tv_status4.setBackgroundResource(R.drawable.mode_sselect_bg);
                tv_status1.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status2.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status3.setTextColor(getResources().getColor(R.color.bottom_text_color));
                tv_status4.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }

    }

    private void sendDataToBlueDevice(String type) {
        if (!BlueDeviceUtils.mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getActivity().getApplicationContext(), "请打开蓝牙", Toast.LENGTH_SHORT).show();
        } else {
            if (BlueDeviceUtils.isLink) {
                if (isOpen || type.equals("90")) {
                    BlueDeviceUtils.bluetoothGattCharacteristic.setValue(type + type);
                    BlueDeviceUtils.bluetoothGatt.writeCharacteristic(BlueDeviceUtils.bluetoothGattCharacteristic);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "请打开开关", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "请连接设备", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetDirection() {
        tv_direction.setTextColor(getResources().getColor(R.color.select_red));
        tv_direction.setBackgroundResource(R.drawable.direction_unselect_bg);
        tv_direction_anti.setTextColor(getResources().getColor(R.color.select_red));
        tv_direction_anti.setBackgroundResource(R.drawable.direction_unselect_bg);
    }

    private void linkBlueDevice() {
        BlueDeviceUtils.bluetoothGatt = BlueDeviceUtils.bluetoothDevice.connectGatt(getActivity().getApplicationContext(), false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {//状态变为 已连接
                    Log.i("info", "连接成功");
                    gatt.discoverServices();//连接成功，开始搜索服务，一定要调用此方法，否则获取不到服务
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) { //状态变为 未连接
                    BlueDeviceUtils.isLink = false;
                    BlueDeviceUtils.bluetoothGatt.close();
                    BlueDeviceUtils.bluetoothDevice = null;
                    Log.i("info", "连接断开");
                }
                return;
            }

            public void onServicesDiscovered(BluetoothGatt gatt, final int status) {
                //发现服务后的响应函数
                super.onServicesDiscovered(gatt, status);
                Log.i("info", "------------------------");
                String service_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";//已知服务
                String characteristic_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";//已知特征
                BlueDeviceUtils.bluetoothGattService = BlueDeviceUtils.bluetoothGatt.getService(UUID.fromString(service_UUID));//通过UUID找到服务
                BlueDeviceUtils.bluetoothGattCharacteristic = BlueDeviceUtils.bluetoothGattService.getCharacteristic(UUID.fromString(characteristic_UUID));//找到服务后在通过UUID找到特征
                if (BlueDeviceUtils.bluetoothGattCharacteristic != null) {
                    gatt.setCharacteristicNotification(BlueDeviceUtils.bluetoothGattCharacteristic, true);//启用onCharacteristicChanged(），用于接收数据
                    msg.what = 30;
                    msg.obj = "设备连接成功";
                    handler.sendMessage(msg);
                } else {
                    msg.what = 40;
                    msg.obj = "设备连接失败";
                    handler.sendMessage(msg);
                    return;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 3) {
            linkBlueDevice();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_direction:
                tv_direction.setTextColor(getResources().getColor(R.color.white));
                tv_direction.setBackgroundResource(R.drawable.directtion_select_bg);
                tv_direction_anti.setTextColor(getResources().getColor(R.color.select_red));
                tv_direction_anti.setBackgroundResource(R.drawable.direction_unselect_bg);
                setModeTextColor(0);
                sendDataToBlueDevice(URL_UNIVERSAL.DIRECTION_POSITIVE);
                break;
            case R.id.tv_direction_anti:
                tv_direction_anti.setTextColor(getResources().getColor(R.color.white));
                tv_direction_anti.setBackgroundResource(R.drawable.directtion_select_bg);
                tv_direction.setTextColor(getResources().getColor(R.color.select_red));
                tv_direction.setBackgroundResource(R.drawable.direction_unselect_bg);
                setModeTextColor(0);
                sendDataToBlueDevice(URL_UNIVERSAL.DIRECTION_NEGATIVE);
                break;
            case R.id.tv_status1:
                setModeTextColor(1);
                resetDirection();
                sendDataToBlueDevice(URL_UNIVERSAL.MODE_ONE);
                break;
            case R.id.tv_status2:
                setModeTextColor(2);
                resetDirection();
                sendDataToBlueDevice(URL_UNIVERSAL.MODE_TWO);
                break;
            case R.id.tv_status3:
                setModeTextColor(3);
                resetDirection();
                sendDataToBlueDevice(URL_UNIVERSAL.MODE_THREE);
                break;
            case R.id.tv_status4:
                setModeTextColor(4);
                sendDataToBlueDevice(URL_UNIVERSAL.MODE_FOUR);
                resetDirection();
                break;
            case R.id.iv_play:
                if (isPlaying) {
                    isPlaying = false;
                    iv_play.setImageResource(R.drawable.musci_play);
                    sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_CLOSE);
                } else {
                    isPlaying = true;
                    iv_play.setImageResource(R.drawable.musci_stop);
                    sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_OPEN);
                }
                break;
            case R.id.iv_left:
                sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_LAST);
                break;
            case R.id.iv_right:
                sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_NEXT);
                break;
            case R.id.iv_voice:
                switch (status) {
                    case STATUS_NONE: // 初始状态
                        start();
                        status = STATUS_WAITING_READY;
                        //tv_test.setText("");
                        break;
                    case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
                    case STATUS_READY: // 引擎准备完毕。
                    case STATUS_SPEAKING:
                    case STATUS_FINISHED:// 长语音情况
                    case STATUS_RECOGNITION:
                        stop();
                        status = STATUS_STOPPED; // 引擎识别中
                        break;
                    case STATUS_STOPPED: // 引擎识别中
                        cancel();
                        status = STATUS_NONE; // 识别结束，回到初始状态
                        break;
                }
                break;
            case R.id.iv_link:
                startActivityForResult(new Intent(getActivity(), BLEActivity.class), 1);
                break;
            case R.id.iv_voicd_add:
                currentVoice++;
                switch (currentVoice) {
                    case 2:
                        sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_VOICE_TWO);
                        iv_voicd_less.setImageResource(R.drawable.voice_less);
                        iv_voicd_less.setEnabled(true);
                        break;
                    case 3:
                        sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_VOICE_THREE);
                        break;
                    case 4:
                        sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_VOICE_FOUR);
                        iv_voicd_add.setEnabled(false);
                        iv_voicd_add.setImageResource(R.drawable.voice_add_unuse);
                        break;
                    case 5:
                        currentVoice = 4;
                        break;
                    default:
                        break;
                }
                tv_musicVoice.setText("音量："+ String.valueOf(currentVoice));
                break;
            case R.id.iv_voicd_less:
                currentVoice--;
                switch (currentVoice) {
                    case 0:
                        currentVoice = 1;
                        break;
                    case 1:
                        sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_VOICE_ONE);
                        iv_voicd_less.setEnabled(false);
                        iv_voicd_less.setImageResource(R.drawable.voice_less_unuse);
                        break;
                    case 2:
                        sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_VOICE_TWO);
                        break;
                    case 3:
                        sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_VOICE_THREE);
                        iv_voicd_add.setEnabled(true);
                        iv_voicd_add.setImageResource(R.drawable.voice_add);
                        break;
                    default:
                        break;
                }
                tv_musicVoice.setText("音量："+ String.valueOf(currentVoice));
                break;
            case R.id.iv_light:
                if (isLightOpen) {
                    isLightOpen = false;
                    iv_light.setImageResource(R.drawable.light_close);
                    sendDataToBlueDevice(URL_UNIVERSAL.LIGHT_OPEN);
                } else {
                    isLightOpen = true;
                    iv_light.setImageResource(R.drawable.light_open);
                    sendDataToBlueDevice(URL_UNIVERSAL.LIGHT_CLOSE);
                }
                break;
            default:
                break;
        }
    }

    private void handleMsg(Message msg) {
        switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1) {
                    dealWithVoiceMessage(msg.obj.toString());
                    //tv_test.setText(msg.obj.toString());
                }

                //故意不写break
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                break;

        }
    }

    private void dealWithVoiceMessage(String msg) {
        if(msg.contains("正转") || msg.contains("正传")  || msg.contains("症状")) {
            tv_direction.setTextColor(getResources().getColor(R.color.white));
            tv_direction.setBackgroundResource(R.drawable.directtion_select_bg);
            tv_direction_anti.setTextColor(getResources().getColor(R.color.select_red));
            tv_direction_anti.setBackgroundResource(R.drawable.direction_unselect_bg);
            setModeTextColor(0);
            sendDataToBlueDevice(URL_UNIVERSAL.DIRECTION_POSITIVE);
        } else if(msg.contains("反转")) {
            tv_direction_anti.setTextColor(getResources().getColor(R.color.white));
            tv_direction_anti.setBackgroundResource(R.drawable.directtion_select_bg);
            tv_direction.setTextColor(getResources().getColor(R.color.select_red));
            tv_direction.setBackgroundResource(R.drawable.direction_unselect_bg);
            setModeTextColor(0);
            sendDataToBlueDevice(URL_UNIVERSAL.DIRECTION_NEGATIVE);
        } else if(msg.contains("开灯")) {
            if(!isLightOpen) {
                isLightOpen = true;
                iv_light.setImageResource(R.drawable.light_open);
                sendDataToBlueDevice(URL_UNIVERSAL.LIGHT_CLOSE);
            }
        } else if(msg.contains("关灯")) {
            if(isLightOpen) {
                isLightOpen = false;
                iv_light.setImageResource(R.drawable.light_close);
                sendDataToBlueDevice(URL_UNIVERSAL.LIGHT_OPEN);
            }
        } else if(msg.contains("播放音乐")) {
            if(!isPlaying) {
                isPlaying = true;
                iv_play.setImageResource(R.drawable.musci_stop);
                sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_OPEN);
            }
        } else if(msg.contains("停止音乐")) {
            if(isPlaying) {
                isPlaying = false;
                iv_play.setImageResource(R.drawable.musci_play);
                sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_CLOSE);
            }
        } else if(msg.contains("上一曲")) {
            sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_LAST);
        } else if(msg.contains("下一曲")) {
            sendDataToBlueDevice(URL_UNIVERSAL.MUSIC_NEXT);
        } else if(msg.contains("打开电源")) {
            if(!isOpen) {
                iv_switch.setImageResource(R.drawable.switch_open);
                isOpen = true;
                sendDataToBlueDevice(URL_UNIVERSAL.SWITCH_OPEN);
            }
        } else if(msg.contains("关闭电源")) {
            if(isOpen) {
                iv_switch.setImageResource(R.drawable.switch_close);
                isOpen = false;
                sendDataToBlueDevice(URL_UNIVERSAL.SWITCH_CLOSE);
            }
        }
    }
}
