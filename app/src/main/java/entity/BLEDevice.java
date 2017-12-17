package entity;

/**
 * Created by Li Wenzhao on 2017/11/12.
 */

public class BLEDevice {
    public String deviceName;
    public int linkStatus;

    public BLEDevice(String deviceName, int linkStatus) {
        this.deviceName = deviceName;
        this.linkStatus = linkStatus;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getDeviceName() {
        return deviceName;
    }

    public void setLinkStatus(int linkStatus) {
        this.linkStatus = linkStatus;
    }
    public int getLinkStatus() {
        return linkStatus;
    }

}
