package entity;

/**
 * Created by Li Wenzhao on 2017/11/5.
 */

public class calendarData {
    public String day;
    public String useTime;

    public calendarData() {

    }
    public calendarData(String day, String useTime) {
        this.day = day;
        this.useTime = useTime;
    }

    public void setDay(String day) {
        this.day = day;
    }
    public String getDay() {
        return day;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }
    public String getUseTime() {
        return useTime;
    }
}
