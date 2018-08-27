package com.lwz.smartpillow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import entity.ViewData;

/**
 * Created by Li Wenzhao on 2017/11/1.
 */

public class Shanxing extends View {
    private int[] mColors = {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE,Color.WHITE};
    private Paint paint;    //画笔
    private ArrayList<ViewData> viewDatas;    //数据集
    private int w;          //View宽高
    private int h;
    private RectF rectF;    //矩形
    private onViewClick mViewClick;
    private int pressType;

    public Shanxing(Context context) {
        super(context);
        initPaint();    //设置画笔
    }

    //设置数据
    public void setData(ArrayList<ViewData> viewDatas) {
        this.viewDatas = viewDatas;
        initData();     //设置数据的百分度和角度
        invalidate();   //刷新UI
    }

    public Shanxing(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public Shanxing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    //初始化画笔
    private void initPaint() {
        paint = new Paint();
        //设置画笔默认颜色
        paint.setColor(Color.WHITE);
        //设置画笔模式：填充
        paint.setStyle(Paint.Style.FILL);
        //
        paint.setTextSize(40);
        //初始化区域
        rectF = new RectF();
    }

    //确定View大小
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;     //获取宽高
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(w / 2, h / 2);             //将画布坐标原点移到中心位置
        float currentStartAngle = -30;                //起始角度
        float r = (float) (Math.min(w, h) / 2);     //饼状图半径(取宽高里最小的值)
        rectF.set(-r, -r, r, r);                    //设置将要用来画扇形的矩形的轮廓
        paint.setAntiAlias(true);                   //抗锯齿
        for (int i = 0; i < viewDatas.size(); i++) {
            ViewData viewData = viewDatas.get(i);
            paint.setColor(viewData.color);
            if(i + 1 == pressType)
                paint.setColor(getResources().getColor(R.color.select_red));
            //绘制扇形(通过绘制圆弧)
            canvas.drawArc(rectF, currentStartAngle, viewData.angle, true, paint);
            //绘制扇形上文字
            float textAngle = currentStartAngle + viewData.angle / 2;    //计算文字位置角度
            float x = (float) ((r + r/3) / 2 * Math.cos(Math.toRadians(textAngle)));    //计算文字位置坐标
            float y = (float) ((r + r/3) / 2 * Math.sin(Math.toRadians(textAngle)));
            paint.setColor(Color.BLACK);        //文字颜色
            if(i + 1 == pressType)
                paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
            int baseLineY = (int) (y - top/2 - bottom/2);//基线中间点的y轴计算公式
            canvas.drawText(viewData.name, x, baseLineY, paint);    //绘制文字

            currentStartAngle += viewData.angle;     //改变起始角度
        }
        paint.setColor(getResources().getColor(R.color.gray_bg3));
        paint.setStrokeWidth((float) 7.0);              //设置线宽
        canvas.drawLine(0, -r, 0, r, paint);
        canvas.drawLine((float)(r * Math.cos(Math.toRadians(30.0))), (float)(-r * Math.sin(Math.toRadians(30.0))), (float)(-r * Math.cos(Math.toRadians(30.0))), (float)(r * Math.sin(Math.toRadians(30.0))), paint);
        canvas.drawLine((float)(r * Math.cos(Math.toRadians(30.0))), (float)(r * Math.sin(Math.toRadians(30.0))), (float)(-r * Math.cos(Math.toRadians(30.0))), (float)(-r * Math.sin(Math.toRadians(30.0))), paint);
        canvas.drawCircle(0,0,r / 3 ,paint);
    }

    private void initData() {
        if (null == viewDatas || viewDatas.size() == 0) {
            return;
        }

        float sumValue = 0;                 //数值和
        for (int i = 0; i < viewDatas.size(); i++) {
            ViewData viewData = viewDatas.get(i);
            sumValue += viewData.value;
            int j = i % mColors.length;     //设置颜色
            viewData.color = mColors[j];
        }

        for (ViewData data : viewDatas) {
            float percentage = data.value / sumValue;    //计算百分比
            float angle = percentage * 360;           //对应的角度
            data.percentage = percentage;
            data.angle = angle;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    pressType = distract(x,y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    mViewClick.onClick(x, y, distract(x,y));
                }
                break;
        }
        return true;
    }

    public void setOnViewClick(onViewClick click) {
        this.mViewClick = click;
    }

    public interface onViewClick {
        /**
         * @param scrollX 从按下到抬起,X轴方向移动的距离
         * @param scrollY 从按下到抬起,Y轴方向移动的距离
         */
        void onClick(float scrollX, float scrollY , int pressType);
    }

    private int distract(int x, int y) {
        float xx = w / 2;
        float yy = h / 2;
        float r = (float) (Math.min(w, h) / 2);
        float dis = (float) Math.sqrt((x-xx)*(x-xx) + (y-yy)*(y-yy));  //点击点与圆心的距离
        float hh = yy - y;      //圆心与点击点的纵坐标之差
        float radius = (float) (Math.asin(hh / dis) * 180 / Math.PI);  //将Math.asin算出来的弧度值转换成角度
        //如果点击区域在中间开关键
        if( dis <= r /3)
            return 0;
        else {
            if(x > w / 2) {
                //如果点击区域在中心右边
                if(radius > 30.0)
                    return 6;
                else if(radius < -30.0)
                    return 2;
                else if(radius > -30.0 && radius < 30.0)
                    return 1;
            } else {
                //如果点击区域在中心左边
                if(radius > 30.0)
                    return 5;
                else if(radius < -30.0)
                    return 3;
                else if(radius > -30.0 && radius < 30.0)
                    return 4;
            }
        }
        return -1;
    }

    public void updateUI(int pressIndex) {
        pressType = pressIndex;
        invalidate();
    }
}
