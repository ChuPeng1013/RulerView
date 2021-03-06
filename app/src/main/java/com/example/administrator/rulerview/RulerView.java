package com.example.administrator.rulerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by ChuPeng on 2016/10/25.
 */
public class RulerView extends View
{
    private Paint paint;
    private Rect rect;
    private float rulerLength;
    private float rulerWidth;
    private float xcm;//此屏幕上每厘米包含多少个像素点
    private float xmm;//此屏幕上每毫米包含多少个像素点
    private int leftMarr;
    private int shortMarkLine = 5;
    private int longMarkLine = 13;
    private int middleMarkLine = 8;
    private int markLine;
    private int lastX;
    private int lastY;
    private int width;//当前屏幕的宽
    private int height;//当前屏幕的高
    private Context context;
    public RulerView(Context context)
    {
        super(context);
        this.context = context;
    }

    public RulerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    //设置控件的尺寸
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕窗口
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        //获取当前屏幕的宽
        width = windowManager.getDefaultDisplay().getWidth();
        //获取当前屏幕的高
        height = windowManager.getDefaultDisplay().getHeight();
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    //设置控件的宽
    private int measureWidth(int widthMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        }
        else if(widthMode == MeasureSpec.AT_MOST)
        {
            width = Math.min(width, widthSize);
        }
        return width;
    }

    //设置控件的高
    private int measureHeight(int heightMeasureSpec)
    {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        }
        else if(heightMode == MeasureSpec.AT_MOST)
        {
            height = Math.min(height, heightSize);
        }
        return height;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //初始化画笔
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setTextSize(20);
        //获取屏幕窗口
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics phoneDisplay = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(phoneDisplay);
        //dm.xdpi是在屏幕的x维度得到确切的物理像素的值，单位是pixal/inch（此屏幕上每英寸包含多少个像素点）
        //将单位是pixal/inch转换成单位是pixal/cm
        xcm = (float) (phoneDisplay.xdpi / 2.54);
        //刻度尺的长度
        rulerLength = 6 * xcm;
        //刻度尺的宽度
        rulerWidth = xcm;
        //绘制尺子的外边框
        canvas.drawRect(0, 0, (int)rulerLength + 30, (int)rulerWidth, paint);
        //最小刻度的长度
        xmm = rulerLength/60;
        for(int i = 0; i <= 60; i++)
        {
            //绘制数字
            if(i%10 == 0)
            {
                markLine = longMarkLine;
                canvas.drawText(String.valueOf(i/10), (int)(15+i*xmm), markLine + 20, paint);
            }
            else if(i%5 == 0)
            {
                markLine = middleMarkLine;
            }
            else
            {
                markLine = shortMarkLine;
            }
            canvas.drawLine((int)(15+i*xmm), 0, (int)(15+i*xmm), markLine, paint);
        }
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        //获得当前View的位置坐标(这里获取的是绝对坐标)
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch(event.getAction())
        {
            //手指按下
            case MotionEvent.ACTION_DOWN:
                //将开始移动前的坐标储存到lastX和lastY中
                lastX = rawX;
                lastY = rawY;
                break;
            //手指移动
            case MotionEvent.ACTION_MOVE:
                //使用移动后的绝对坐标减去初始坐标，计算出View的偏移量
                int offsetX = rawX - lastX;
                int offsetY = rawY - lastY;
                //使用layout()对移动后的View重绘
                layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX, getBottom() + offsetY);
                //对View重绘后需要重新设置初始坐标
                lastX = rawX;
                lastY = rawY;
                break;
            //手指离开
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
