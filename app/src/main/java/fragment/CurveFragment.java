package fragment;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwz.smartpillow.R;
import com.lwz.smartpillow.WholeDayCurveActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CalendarRecycleViewAdapter;
import entity.calendarData;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.MediaType;
import utils.CalculateSignature;
import utils.SharedPrefsUtil;
import utils.ToastUtils;
import utils.URL_UNIVERSAL;

public class CurveFragment extends Fragment implements View.OnClickListener{
    private RelativeLayout rl_lastMonth, rl_nextMonth;
    private RecyclerView recyclerView;
    private CalendarRecycleViewAdapter adapter;
    private ArrayList<calendarData> datas = new ArrayList<>();
    private Calendar calendar;
    private int currentYear, currentMonth, currentDay, displayYear, displayMonth, displayDay;
    private TextView tv_today, tv_date, tv_wholeActive;
    private List<Map<String, Object>> displayMonthData = new ArrayList<>();
    private List<Map<String, Object>> displayDayData = new ArrayList<>();

    private LineChartView chartActive;
    private List<PointValue> mPointValuesActive = new ArrayList<>();
    private List<AxisValue> mAxisValuesXActive = new ArrayList<>();
    private List<AxisValue> mAxisValuesYActive = new ArrayList<>();
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

    public CurveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_curve, container, false);
        tv_today = (TextView) view.findViewById(R.id.tv_today);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        rl_lastMonth = (RelativeLayout) view.findViewById(R.id.rl_lastMonth);
        rl_nextMonth = (RelativeLayout) view.findViewById(R.id.rl_nextMonth);
        rl_lastMonth.setOnClickListener(this);
        rl_nextMonth.setOnClickListener(this);
        tv_today.setOnClickListener(this);
        tv_date.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        displayYear = currentYear;
        displayMonth = currentMonth;
        displayDay = currentDay;

        adapter = new CalendarRecycleViewAdapter(getContext(), datas, displayYear, displayMonth);
        adapter.setOnItemClickListener(new CalendarRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!datas.get(position).getDay().equals("")) {
                    displayDay = Integer.parseInt(datas.get(position).getDay());
                    adapter.setDisplayDate(displayYear, displayMonth, Integer.parseInt(datas.get(position).getDay()));
                    adapter.notifyDataSetChanged();
                    try {
                        getDisplayData(sdf.format(sdf.parse(displayYear + "-" + displayMonth + "-" + Integer.parseInt(datas.get(position).getDay()))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        initCalendarData(displayYear, displayMonth, 0);

        chartActive = (LineChartView) view.findViewById(R.id.chartActive);
        tv_wholeActive = (TextView) view.findViewById(R.id.tv_wholeActive);
        tv_wholeActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(getActivity(), WholeDayCurveActivity.class);
                    intent.putExtra("date", sdf.format(sdf.parse(displayYear + "-" + displayMonth + "-" + displayDay)));
                    startActivity(intent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            getDisplayData(sdf.format(sdf.parse(currentYear + "-" + currentMonth + "-" + currentDay)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }


    private void initCalendarData(int Year, int Month, int selectDay) {
        datas.clear();
        int daysInMonth = 0;
        if(Month == 1 || Month == 3 || Month == 5 || Month == 7 || Month == 8 || Month == 10 || Month == 12)
            daysInMonth = 31;
        else if(Month == 4 || Month == 6 || Month == 9 || Month == 11)
            daysInMonth = 30;
        else if((Month == 2) && isLeapYear(Year))
            daysInMonth = 29;
        else if((Month == 2) && !(isLeapYear(Year)))
            daysInMonth = 28;

        //设置calendar指定的Year-Month-1位本月第一天
        calendar.set(Year, Month - 1, 1);
        //获取当月第一天是周几
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //填充第一天之前的空白部分
        for(int i = 0; i < weekDay; i++) {
            calendarData calendarData = new calendarData("", "");
            datas.add(calendarData);
        }
        for(int i = 1; i <= daysInMonth;  i++) {
            calendarData calendarData = new calendarData();
            calendarData.setDay(i + "");
            calendarData.setUseTime("");
            datas.add(calendarData);
        }
        adapter.setDisplayDate(displayYear, displayMonth, selectDay);
        adapter.notifyDataSetChanged();
        tv_date.setText(displayYear + "年" + displayMonth + "月");


        if(Year < currentYear || (Year == currentYear && Month < currentMonth)){
            try {
                String startDate = Year + "-" + Month + "-" + 1;
                String endDate = Year + "-" + Month + "-" + daysInMonth;
                getActiveData(sdf.format(sdf.parse(startDate)), sdf.format(sdf.parse(endDate)), Year, Month, daysInMonth, weekDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(Year == currentYear && Month == currentMonth){
            try {
                String startDate = Year + "-" + Month + "-" + 1;
                String endDate = Year + "-" + Month + "-" + currentDay;
                getActiveData(sdf.format(sdf.parse(startDate)), sdf.format(sdf.parse(endDate)), Year, Month, daysInMonth, weekDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isLeapYear(int currentYear) {
        if((currentYear % 4 == 0 && currentYear % 100 != 0) || currentYear % 400 == 0)
            return true;
        else
            return false;
    }

    private String getDayOfUseTime(String date) {
        for(int i = 0; i < displayMonthData.size(); i++)
            if(displayMonthData.get(i).get("date").toString().equals(date)) {
                float usetime = Float.parseFloat(displayMonthData.get(i).get("useTime").toString()) ;
                if(usetime < 60)
                    return displayMonthData.get(i).get("useTime").toString() + "min";
                else
                    return ((int)(usetime / 60) < 25 ? (int)(usetime / 60)  : 24) + "h";
            }
       return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_lastMonth:
                if(displayMonth == 1) {
                    displayYear = displayYear - 1;
                    displayMonth = 12;
                } else
                    displayMonth--;
                displayDay = 1;
                initCalendarData(displayYear, displayMonth, 0);
                try {
                    getDisplayData(sdf.format(sdf.parse(displayYear + "-" + displayMonth + "-" + 1)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_nextMonth:
                if(displayMonth == 12) {
                    displayYear = displayYear + 1;
                    displayMonth = 1;
                } else
                    displayMonth++;
                displayDay = 1;
                initCalendarData(displayYear, displayMonth, 0);
                try {
                    getDisplayData(sdf.format(sdf.parse(displayYear + "-" + displayMonth + "-" + 1)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_today:
                displayYear = currentYear;
                displayMonth = currentMonth;
                displayDay = currentDay;
                initCalendarData(displayYear, displayMonth, 0);
                try {
                    getDisplayData(sdf.format(sdf.parse(currentYear + "-" + currentMonth + "-" + currentDay)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_date:
                new DatePickerDialog(getContext(),AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // TODO Auto-generated method stub
                                displayYear = year;
                                displayMonth = monthOfYear + 1;
                                displayDay = dayOfMonth;
                                initCalendarData(displayYear, displayMonth, dayOfMonth);
                                try {
                                    getDisplayData(sdf.format(sdf.parse(displayYear + "-" + displayMonth + "-" + dayOfMonth)));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
            default:
                break;
        }
    }

    private void initActiveLineChart() {
        mAxisValuesXActive.clear();
        mAxisValuesYActive.clear();
        mPointValuesActive.clear();

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int m = hour < 4 ? 0 : hour - 4;
        //X轴标注
        for (int i = m - 1; i < hour; i++) {
            mAxisValuesXActive.add(new AxisValue(i - m + 1).setLabel(i + "时  "));
        }
        //Y轴标注
        for (int i = 0; i < 101; i = i + 20) {
            mAxisValuesYActive.add(new AxisValue(i).setLabel(i + ""));
        }
        //描点
        for (int i = m - 1; i < hour; i++) {
            mPointValuesActive.add(new PointValue(i - m + 1, getHourOfUseTime(i)));
        }

        Line line = new Line(mPointValuesActive).setColor(Color.parseColor("#ffffff")).setCubic(false);  //折线的颜色
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(false);//曲线是否平滑
        //line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        //line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        line.setPointRadius(4);

        lines.add(line);

        LineChartData data = new LineChartData();
        data.setValueLabelBackgroundColor(Color.TRANSPARENT);     //此处设置坐标点旁边的文字背景
        data.setValueLabelBackgroundEnabled(false);
        data.setValueLabelsTextColor(Color.parseColor("#ffffff"));  //此处设置坐标点旁边的文字颜色
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        //axisX.setName("未来几天的天气");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setValues(mAxisValuesXActive);  //填充X轴的坐标名称
        axisX.setLineColor(R.color.white);
        data.setAxisXBottom(axisX); //x 轴在底部

        Axis axisY = new Axis();  //Y轴
        axisY.setTextColor(Color.WHITE);  //设置字体颜色
        //axisY.setName("温度");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        axisY.setLineColor(R.color.white);
        axisY.setValues(mAxisValuesYActive);  //填充Y轴的坐标名称
        data.setAxisYLeft(axisY);  //Y轴设置在左边

        chartActive.setZoomEnabled(false);
        chartActive.setScrollEnabled(false);
        chartActive.setLineChartData(data);

        Viewport v = new Viewport(chartActive.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        chartActive.setMaximumViewport(v);
        chartActive.setCurrentViewport(v);
    }

//    private void getMessage() {
//        String[] data = CalculateSignature.getSignature().split("@");
//        OkHttpUtils.get().url(URL_UNIVERSAL.GET_MESSAGE)
//                .addHeader("appkey", URL_UNIVERSAL.APPKEY)
//                .addHeader("random", data[0])
//                .addHeader("timestamp", data[1])
//                .addHeader("signature", data[2])
//                .addParams("telphone", SharedPrefsUtil.getValue(getContext(), "username", ""))
//                .addParams("pushflag", "-1")
//                .build()
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        Log.i("getMessage", "接口访问失败：" + call + "---" + e);
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        Log.i("getMessage", "接口访问成功：" + response);
//                        JSONObject jsonObject = JSON.parseObject(response);
//                    }
//                });
//    }
//
//    private void pushOperateInfo() {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("Operator", "Liwenzhao");
//        jsonObject.put("Usex", 1);
//        jsonObject.put("LoginPlace", "wuhan");
//        jsonObject.put("StartAge", 10);
//        jsonObject.put("EndAge", 50);
//        jsonObject.put("StartIncome", 0);
//        jsonObject.put("EndIncome", 0);
//        jsonObject.put("PushContent", "22222222222222");
//        //Log.i("pushOperateInfo", jsonObject.toJSONString());
//        OkHttpUtils.postString().url(URL_UNIVERSAL.PUSH_OPERATE_INFO)
//                .content(jsonObject.toJSONString())
//                .mediaType(MediaType.parse("application/json"))
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        Log.i("pushOperateInfo", "接口访问失败：" + call + "---" + e);
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        Log.i("pushOperateInfo", "接口访问成功：" + response);
//                        JSONObject jsonObject = JSON.parseObject(response);
//                    }
//                });
//
//    }

    private void getActiveData(String startDate, String endDate, final int Year, final int Month, final int daysInMonth, final int weekDay) {
        String[] data = CalculateSignature.getSignature().split("@");
        OkHttpUtils.get().url(URL_UNIVERSAL.GET_ACTIVE_DATA)
                .addHeader("appkey", URL_UNIVERSAL.APPKEY)
                .addHeader("random", data[0])
                .addHeader("timestamp", data[1])
                .addHeader("signature", data[2])
                .addParams("telephone", SharedPrefsUtil.getValue(getContext(), "username", ""))
                .addParams("startDate", startDate)
                .addParams("endDate", endDate)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.i("getActiveData", "接口访问失败：" + call + "---" + e);
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("getActiveData", "接口访问成功：" + response);
                        try {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (code.equals("200") && status.equals("ok")) {
                                displayMonthData.clear();
                                JSONArray monthArray = jsonObject.getJSONArray("data");
                                if(monthArray.size() != 0){
                                    if(monthArray.size() == 1) {
                                        JSONObject dayObject = monthArray.getJSONObject(0);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("date", dayObject.getString("ADDate").substring(0,10));
                                        map.put("useTime", (int)(dayObject.getFloatValue("ActivityDegreeVal") * 60 + 0.5));
                                        displayMonthData.add(map);
                                    } else {
                                        float allTime = 0;
                                        for(int i = 0; i < monthArray.size(); i++) {
                                            JSONObject dayObject = monthArray.getJSONObject(i);
                                            if(i > 0) {
                                                if(i == (monthArray.size() - 1)) {
                                                    if(!dayObject.getString("ADDate").substring(0,10).equals(monthArray.getJSONObject(i-1).getString("ADDate").substring(0,10))){
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("date", monthArray.getJSONObject(i-1).getString("ADDate").substring(0,10));
                                                        map.put("useTime", (int)(allTime + 0.5));
                                                        displayMonthData.add(map);

                                                        allTime = dayObject.getFloatValue("ActivityDegreeVal") * 60;
                                                        Map<String, Object> map2 = new HashMap<>();
                                                        map2.put("date", monthArray.getJSONObject(i).getString("ADDate").substring(0,10));
                                                        map2.put("useTime", (int)(allTime + 0.5));
                                                        displayMonthData.add(map2);
                                                        break;
                                                    } else {
                                                        allTime += dayObject.getFloatValue("ActivityDegreeVal") * 60;
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("date", monthArray.getJSONObject(i).getString("ADDate").substring(0,10));
                                                        map.put("useTime", (int)(allTime + 0.5));
                                                        displayMonthData.add(map);
                                                        break;
                                                    }

                                                } else {
                                                    if(!dayObject.getString("ADDate").substring(0,10).equals(monthArray.getJSONObject(i-1).getString("ADDate").substring(0,10))){
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("date", monthArray.getJSONObject(i-1).getString("ADDate").substring(0,10));
                                                        map.put("useTime", (int)(allTime + 0.5));
                                                        displayMonthData.add(map);
                                                        allTime = 0;
                                                    }
                                                }
                                            }
                                            allTime += dayObject.getFloatValue("ActivityDegreeVal") * 60;
                                        }
                                    }

                                    datas.clear();
                                    //填充第一天之前的空白部分
                                    for(int i = 0; i < weekDay; i++) {
                                        calendarData calendarData = new calendarData("", "");
                                        datas.add(calendarData);
                                    }
                                    SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                                    for(int i = 1; i <= daysInMonth;  i++) {
                                        calendarData calendarData = new calendarData();
                                        if(Year < currentYear || (Year == currentYear && Month < currentMonth) || (Year == currentYear && Month == currentMonth && i <= currentDay)){
                                            calendarData.setDay(i + "");
                                            try {
                                                calendarData.setUseTime(getDayOfUseTime(sdf.format(sdf.parse(Year + "-" + Month + "-" + i))));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            calendarData.setDay(i + "");
                                            calendarData.setUseTime("");
                                        }
                                        datas.add(calendarData);
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                            } else {
                                ToastUtils.showToast(getContext(), message);
                            }
                        } catch (Exception e) {
                            //ToastUtils.showToast(getContext(), "获取数据失败");
                        }
                    }
                });
    }

    private void getDisplayData(String date) {
        String[] data = CalculateSignature.getSignature().split("@");
        OkHttpUtils.get().url(URL_UNIVERSAL.GET_ACTIVE_DATA)
                .addHeader("appkey", URL_UNIVERSAL.APPKEY)
                .addHeader("random", data[0])
                .addHeader("timestamp", data[1])
                .addHeader("signature", data[2])
                .addParams("telephone", SharedPrefsUtil.getValue(getContext(), "username", ""))
                .addParams("startDate", date)
                .addParams("endDate", date)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.i("getActiveData", "接口访问失败：" + call + "---" + e);
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("getActiveData", "接口访问成功：" + response);
                        try {
                            JSONObject jsonObject = JSON.parseObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (code.equals("200") && status.equals("ok")) {
                                displayDayData.clear();
                                JSONArray dayArray = jsonObject.getJSONArray("data");
                                if(dayArray.size() != 0){
                                    for(int i = 0; i < dayArray.size(); i++) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("startTime", dayArray.getJSONObject(i).getIntValue("StartTime"));
                                        map.put("activeVal", dayArray.getJSONObject(i).getFloatValue("ActivityDegreeVal") * 100);
                                        displayDayData.add(map);
                                    }
                                }
                                initActiveLineChart();
                            } else {
                                ToastUtils.showToast(getContext(), message);
                            }
                        } catch (Exception e) {
                            //ToastUtils.showToast(getContext(), "获取数据失败");
                        }
                    }
                });
    }

    private float getHourOfUseTime(int hour) {
        for(int i = 0; i < displayDayData.size(); i++)
            if((int)displayDayData.get(i).get("startTime") == hour) {
                return (float) displayDayData.get(i).get("activeVal");
            }
        return 0;
    }
}
