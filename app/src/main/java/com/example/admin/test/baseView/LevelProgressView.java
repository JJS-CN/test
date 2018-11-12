package com.example.admin.test.baseView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.Scroller;

import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：荣誉等级---进度条的绘制
 * Created by jjs on 2018/8/24.
 */

public class LevelProgressView extends View {
    private int mPoiOk = R.mipmap.ic_poi_ok;//已达到位置
    private int mPoiNo = R.mipmap.ic_poi_no;//未达到位置
    private int mPoiLevelBg = R.mipmap.ic_poi_progess;//当前位置标识
    private int mProgressBgColor = Color.parseColor("#F2F2F2");//背景
    private int mProgressStartColor = Color.parseColor("#B7B5FD");//前景渐变开始色
    private int mProgressEndColor = Color.parseColor("#81ADFF");//前景渐变结束色
    private int mMaxProgress = 100;//最大分数
    private int mProgress = 0;//当前分数
    private int mOneInterval = 10;//单份间隔
    private int mProgressHeight = 20;//进度条高度
    private List<PoiEntity> mPoiList = new ArrayList<>();//点数据
    private int[] mPoiCircle;//poi点宽高
    private int[] mPoiLevel;//位置宽高

    private Paint mPaint;
    private TextPaint mTextPaint;
    private float mTextAsent;//文字绘制位置H

    private Scroller mScroller;
    private int mLeftBorder = 0;//左边界
    private int mRightBorder;//右边界
    private int offset = 30;//左侧偏移量

    private VelocityTracker mVelocityTracker = null;

    public LevelProgressView(Context context) {
        super(context);
        init();
    }

    public LevelProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LevelProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mOneInterval = display.getWidth() / 350;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), mPoiOk, options);
        mPoiCircle = new int[]{options.outWidth, options.outHeight};
        BitmapFactory.decodeResource(getResources(), mPoiLevelBg, options);
        mPoiLevel = new int[]{options.outWidth, options.outHeight};

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setTextSize(sp2px(13));

        mProgressHeight = (int) dp2px(6);
        mTextAsent = mTextPaint.getFontMetrics().ascent;

        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, (int) dp2px(105));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRightBorder = mOneInterval * mMaxProgress + mPoiCircle[0] - getWidth() + offset * 2;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mProgressBgColor);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(mProgressHeight / 2);
        int top = mPoiLevel[1] + mPoiCircle[1] / 2 + 20;
        Rect rect = new Rect(mPoiCircle[0] / 2 + offset, top, mPoiCircle[0] / 2 + mOneInterval * mMaxProgress + offset, top + mProgressHeight);
        gradientDrawable.setBounds(rect);
        //绘制背景
        gradientDrawable.draw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            gradientDrawable.setColors(new int[]{mProgressStartColor, mProgressEndColor});
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        } else {
            gradientDrawable.setColor(mProgressStartColor);
        }
        rect.right = mPoiCircle[0] / 2 + mOneInterval * mProgress + offset;
        gradientDrawable.setBounds(rect);
        //绘制前景
        gradientDrawable.draw(canvas);
        //绘制progress图
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mPoiLevelBg);
        canvas.drawBitmap(bitmap, rect.right - mPoiLevel[0] / 2, rect.top - mPoiLevel[1] - mPoiCircle[1] / 2, mPaint);
        //绘制progress文本
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.WHITE);
        canvas.drawText(mProgress + "", (rect.right - mPoiLevel[0] / 2) + bitmap.getWidth() / 2, rect.top - mPoiLevel[1] - mPoiCircle[1] / 2 - mTextAsent + 10, mTextPaint);
        bitmap.recycle();
        //绘制poi点
        for (int i = 0; i < mPoiList.size(); i++) {
            PoiEntity entity = mPoiList.get(i);
            Bitmap poiBmp = BitmapFactory.decodeResource(getResources(), entity.progress <= mProgress ? mPoiOk : mPoiNo);
            canvas.drawBitmap(poiBmp, mOneInterval * entity.progress + offset, (rect.top + rect.bottom) / 2 - poiBmp.getHeight() / 2 + 5, mPaint);
            poiBmp.recycle();
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setColor(Color.DKGRAY);
            //绘制文本
            canvas.drawText(entity.message, mPoiCircle[0] / 2 + mOneInterval * entity.progress + offset, rect.bottom + poiBmp.getHeight() / 4 - mTextAsent, mTextPaint);
            mTextPaint.setColor(Color.GRAY);
            canvas.drawText(entity.progress + "", mPoiCircle[0] / 2 + mOneInterval * entity.progress + offset, rect.bottom + poiBmp.getHeight() / 4 - mTextAsent + 50, mTextPaint);
        }

    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public void setPoiList(List<PoiEntity> poiList) {
        mPoiList = poiList != null ? poiList : new ArrayList<PoiEntity>();
    }

    private float dp2px(float dp) {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dp * dm.density + 0.5f;
    }

    private float sp2px(float sp) {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return sp * dm.scaledDensity + 0.5f;
    }

    @Override
    public void computeScroll() {
        // 重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    float moveX;
    float mXLastMove;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXLastMove = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getRawX();
                int scrolledX = (int) (mXLastMove - moveX);
                if (getScrollX() + scrolledX < mLeftBorder) {
                    scrollTo(mLeftBorder, 0);
                    return true;
                } else if (getScrollX() + scrolledX > mRightBorder) {
                    scrollTo(mRightBorder, 0);
                    return true;
                }
                scrollBy(scrolledX, 0);
                mXLastMove = moveX;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                mScroller.fling(getScrollX(), 0, -velocityX, 0, mLeftBorder, mRightBorder, 0, 0);
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                invalidate();
                break;

        }
        return true;
    }

    public static class PoiEntity {
        public int progress;//位置点
        public String message;//说明文本

        @Override
        public String toString() {
            return message;
        }

        public PoiEntity(int progress, String message) {
            this.progress = progress;
            this.message = message;
        }
    }

}
