package fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lwz.smartpillow.R;
import com.lwz.smartpillow.WholeDayCurveActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

public class CurveFragment extends Fragment implements View.OnClickListener{
    private RelativeLayout rl_lastMonth, rl_nextMonth;
    private RecyclerView recyclerView;
    private CalendarRecycleViewAdapter adapter;
    private ArrayList<calendarData> datas = new ArrayList<>();
    private Calendar calendar;
    private int currentYear, currentMonth, currentDay, displayYear, displayMonth;
    private TextView tv_today, tv_date, tv_wholeActive;

    private LineChartView chartActive;
    private List<PointValue> mPointValuesActive = new ArrayList<>();
    private List<AxisValue> mAxisValuesXActive = new ArrayList<>();
    private List<AxisValue> mAxisValuesYActive = new ArrayList<>();

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

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        displayYear = currentYear;
        displayMonth = currentMonth;

        adapter = new CalendarRecycleViewAdapter(getContext(), datas, displayYear, displayMonth);
        adapter.setOnItemClickListener(new CalendarRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("onItemClick", datas.get(position).getDay());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        initCalendarData(displayYear, displayMonth);

        chartActive = (LineChartView) view.findViewById(R.id.chartActive);
        tv_wholeActive = (TextView) view.findViewById(R.id.tv_wholeActive);
        tv_wholeActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WholeDayCurveActivity.class);
                startActivity(intent);
            }
        });
        initActiveLineChart();
        return view;
    }


    private void initCalendarData(int Year, int Month) {
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
            calendarData calendarData = new calendarData(i + "", "20min");
            datas.add(calendarData);
        }

        adapter.setDisplayDate(displayYear, displayMonth);
        adapter.notifyDataSetChanged();
        tv_date.setText(displayYear + "年" + displayMonth + "月");
    }

    private boolean isLeapYear(int currentYear) {
        if((currentYear % 4 == 0 && currentYear % 100 != 0) || currentYear % 400 == 0)
            return true;
        else
            return false;
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
                initCalendarData(displayYear, displayMonth);
                break;
            case R.id.rl_nextMonth:
                if(displayMonth == 12) {
                    displayYear = displayYear + 1;
                    displayMonth = 1;
                } else
                    displayMonth++;
                initCalendarData(displayYear, displayMonth);
                break;
            case R.id.tv_today:
                displayYear = currentYear;
                displayMonth = currentMonth;
                initCalendarData(displayYear, displayMonth);
                break;
            default:
                break;
        }
    }

    private void initActiveLineChart() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int m = hour < 4 ? 0 : hour - 4;
        //X轴标注
        for (int i = m; i < hour + 1; i++) {
            mAxisValuesXActive.add(new AxisValue(i - m).setLabel(i + "时  "));
        }
        //Y轴标注
        for (int i = 0; i < 101; i = i + 20) {
            mAxisValuesYActive.add(new AxisValue(i).setLabel(i + ""));
        }
        //描点
        for (int i = m; i < hour + 1; i++) {
            mPointValuesActive.add(new PointValue(i - m, (float) Math.random() * 100f + 0f));
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
}
