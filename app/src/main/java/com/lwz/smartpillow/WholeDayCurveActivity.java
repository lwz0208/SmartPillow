package com.lwz.smartpillow;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class WholeDayCurveActivity extends AppCompatActivity {
    private LineChartView chart;
    private List<AxisValue> mAxisValuesX = new ArrayList<>();
    private List<AxisValue> mAxisValuesY = new ArrayList<>();
    private List<PointValue> mPointValues = new ArrayList<>();
    private Calendar c;
    private int currentHour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_day_curve);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        chart = (LineChartView) findViewById(R.id.chart);
        initLineChart();
    }

    /**
     * 初始化LineChart的一些设置
     */
    private void initLineChart() {
        //初始化x轴
        for (int i = 0; i < currentHour + 1; i++) {
            mAxisValuesX.add(new AxisValue(i).setLabel(i + "时  "));
        }

        for (int i = 0; i < 101; i = i + 20) {
            mAxisValuesY.add(new AxisValue(i).setLabel(i + ""));
        }
        //描点
        for (int i = 0; i < currentHour + 1; i++) {
            mPointValues.add(new PointValue(i, (float) Math.random() * 100f + 0f));
        }


        Line line = new Line(mPointValues).setColor(Color.parseColor("#EE6363")).setCubic(false);  //折线的颜色
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
        data.setValueLabelsTextColor(Color.parseColor("#EE6363"));  //此处设置坐标点旁边的文字颜色
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setTextColor(R.color.curve_bg);  //设置字体颜色
        //axisX.setName("未来几天的天气");  //表格名称
        axisX.setTextSize(11);//设置字体大小
        axisX.setValues(mAxisValuesX);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部

        Axis axisY = new Axis();  //Y轴
        axisY.setTextColor(R.color.curve_bg);  //设置字体颜色
        //axisY.setName("温度");//y轴标注
        axisY.setTextSize(11);//设置字体大小
        axisY.setValues(mAxisValuesY);  //填充Y轴的坐标名称
        axisY.setHasLines(true);

        data.setAxisYLeft(axisY);  //Y轴设置在左边

        chart.setZoomEnabled(false);
        chart.setScrollEnabled(true);
        chart.setLineChartData(data);

        Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;

        chart.setMaximumViewport(v);

        if(currentHour > 5) {
            float dx = v.width() / currentHour * 5;
            v.right = currentHour;
            v.left = v.right - dx;
        } else {
            chart.setScrollEnabled(false);
        }
        chart.setCurrentViewport(v);
    }
}
