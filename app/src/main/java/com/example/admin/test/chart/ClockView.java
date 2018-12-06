package com.example.admin.test.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.TimeUtils;

/**
 * 说明：
 * Created by jjs on 2018/12/5
 */

public class ClockView extends View {

    private int mW;
    private int mH;
    private Paint mPaint;
    private int mAnimatedValue;

    public ClockView(Context context) {
        super(context);  init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);  init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint=new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(10);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int mWidth = 0, mHeight = 0;
        if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = 300;
        } else if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = width >= 300 ? width : 300;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = 300;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = height >= 300 ? height : 300;
        }
        if (mWidth != mHeight) {
            mWidth = mHeight = Math.max(mWidth, mHeight);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mW / 2, mH / 2, mW / 2-10/2, mPaint);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(mW / 2, mH / 2, 10, mPaint);
        mPaint.setStrokeWidth(6);//时
        mPaint.setColor(Color.parseColor("#FF00FF"));
        canvas.save();
       // canvas.rotate(mAnimatedValue/3600%12*60,mW/2,mH/2);
        canvas.rotate(TimeUtils.getNowDate().getHours()*30,mW/2,mH/2);
        canvas.drawLine(mW/2,mH/2,mW/2,mH/2-60,mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setStrokeWidth(4);//分
        mPaint.setColor(Color.parseColor("#0055FF"));
        canvas.rotate(TimeUtils.getNowDate().getMinutes()*6,mW/2,mH/2);
        canvas.drawLine(mW/2,mH/2,mW/2,mH/2-80,mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setStrokeWidth(2);//秒
        mPaint.setColor(Color.parseColor("#CC5555"));
        canvas.rotate(TimeUtils.getNowDate().getSeconds()*6,mW/2,mH/2);
        canvas.drawLine(mW/2,mH/2,mW/2,mH/2-100,mPaint);
        canvas.restore();
        invalidate();
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
    }
}
