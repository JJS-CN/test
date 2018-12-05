package com.example.admin.test.gradient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * 说明：
 * Created by jjs on 2018/12/4
 */

public class HeartbeatView extends View {
    private int mGapSizeH = 128;//高度上的数据点个数
    private int mGapSizeW = 10;//宽度上的数据点个数---每条data
    private int mPoiGapHeight = 4;//竖向间隔
    private int mPoiGapWidth;//横向间隔
    private int mItemWidth;//每条data的宽度
    private int mDataCount = -1;//数据次数
    private int mWidthDataSize = 5;//横向数据条数
    private Path mPoiPath;//数据
    private Path mAnimPath;//执行动画时使用的数据
    private boolean isClear = false;
    private PathMeasure mPathMeasure;

    private Paint mPaint;
    private float mValue;
    private Path mTemPath;

    public HeartbeatView(Context context) {
        super(context);
        init();
    }

    public HeartbeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartbeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mTemPath = new Path();
    }

    int[] a;

    public void setData(final int[] data) {
        mDataCount++;
        a = data;

        if (mPathMeasure == null) {
            //执行延时，内部先持有一个数据，简单适配数据延迟问题
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPathMeasure = new PathMeasure();
                    mPoiPath = creatPath(data);

                    mPathMeasure.setPath(mPoiPath, false);
                    startPathAnim();
                }
            }, 190);
        } else {
            mPathMeasure = new PathMeasure();
            Path path = creatPath(data);
            mPathMeasure.setPath(path, false);
            startPathAnim();
        }
    }

    private Path creatPath(int[] data) {
        int index = mDataCount % mWidthDataSize;
        Path path = new Path();
        for (int i = 0; i < data.length; i++) {
            if (i == 0) {
                path.moveTo(i * mPoiGapWidth + mItemWidth * index + getPaddingLeft(), getHeight() - getPaddingBottom() - data[i] * mPoiGapHeight);
            }
            path.lineTo(i * mPoiGapWidth + mItemWidth * index + getPaddingLeft(), getHeight() - getPaddingBottom() - data[i] * mPoiGapHeight);
        }
        return path;
    }

    public void clear() {
        mDataCount = 0;
        //清除画布,重走onDraw
        isClear = true;
        invalidate();
        //todo 动画
        mPathMeasure = null;
    }

    Bitmap mBitmap;
    Canvas mCanvas;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPathMeasure == null) {
            return;
        }
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
        if (isClear) {
            isClear = false;
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPaint(paint);
        }
        mTemPath.reset();
        mPathMeasure.getSegment(0, mValue, mTemPath, true);
        PathMeasure measure = new PathMeasure();
        measure.setPath(mTemPath, false);
        Log.e("eeee", mValue + "==" + (mPathMeasure.getLength()) + "==" + measure.getLength());
        mCanvas.drawPath(mTemPath, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int fastW = 0;
        int fastH = 0;
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            //给默认值
            fastW = 600;
            fastH = 300;
        } else if (widthMode != MeasureSpec.AT_MOST) {
            fastW = width;
            int gap = height / mGapSizeH;
            if (gap < mPoiGapHeight) {
                //说明高度很小
                fastH = mPoiGapHeight * mGapSizeH + getPaddingTop() + getPaddingBottom();
            } else {
                //说明高度很高
                fastH = height + getPaddingTop() + getPaddingBottom();
            }
        }
        setMeasuredDimension(fastW, fastH);
        mPoiGapWidth = (fastW - getPaddingLeft() - getPaddingRight()) / mWidthDataSize / mGapSizeW;
        mItemWidth = mPoiGapWidth * mGapSizeW;
    }

    // 开启路径动画
    public void startPathAnim() {
        // 0 － getLength()
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(400);
        // 减速插值器
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValue = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition

                invalidate();
                // new Rect(getPaddingLeft() + index * mItemWidth, 0, getPaddingLeft() + (index + 1) * mItemWidth, getHeight())
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDataCount++;
                mPoiPath = creatPath(a);
                int index = mDataCount % mWidthDataSize;
                if (index == 0) {
                    isClear = true;
                }
                mPathMeasure.setPath(mPoiPath, false);
                startPathAnim();
            }
        });
        valueAnimator.start();
    }

}
