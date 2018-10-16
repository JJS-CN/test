package com.example.admin.test.baseView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 说明：尺子
 * Created by jjs on 2018/8/23.
 */

public class RulerView extends View {
    private int mLineHeight = 50;
    private int mLineInterval = 50;
    private int mLineLength = 50;
    private float mXMove;
    private float mXLastMove;
    private OnSelectListener mSelectListener;

    // 界面可滚动的左边界
    private int leftBorder;
    // 界面可滚动的右边界
    private int rightBorder;

    private Paint mPaint;
    private TextPaint mTextPaint;

    private int position;

    public RulerView(Context context) {
        super(context);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            leftBorder = getWidth() / 2;
            rightBorder = -mLineInterval * mLineLength + getWidth() / 2;
        }
    }

    float offset = 0f;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) {
            //在此控制初始偏移量
            mXLastMove = getWidth() / 2 - 5 * mLineInterval;
            mPaint = new Paint();
            mPaint.setStrokeWidth(2);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(Color.BLACK);
            mPaint.setAntiAlias(true);
        }
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            mTextPaint.setStrokeWidth(2);
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setTextSize(30);
            mTextPaint.setAntiAlias(true);
        }
        for (int i = 0; i <= mLineLength; i++) {
            float p = Math.abs(getWidth() / 2 - (i * mLineInterval + mXLastMove + mXMove));

            if (p <= mLineInterval / 2) {
                offset = getWidth() / 2 - (i * mLineInterval + mXLastMove + mXMove);
                //变色
                mPaint.setColor(Color.RED);
                mTextPaint.setColor(Color.RED);
                if (position != i) {
                    position = i;
                    if (mSelectListener != null) {
                        mSelectListener.onSelect(position);
                    }
                }
            } else {
                mPaint.setColor(Color.BLACK);
                mTextPaint.setColor(Color.BLACK);
            }
            canvas.drawLine(i * mLineInterval + mXLastMove + mXMove, 0, i * mLineInterval + mXLastMove + mXMove, mLineHeight, mPaint);

            int textWidth = (int) mTextPaint.measureText(i + "");
            canvas.drawText(i + "", i * mLineInterval - textWidth / 2 + mXLastMove + mXMove, mLineHeight + 30, mTextPaint);
        }
        mPaint.setColor(Color.RED);
        Path mPath = new Path();
        mPath.moveTo(getWidth() / 2 - 20, 0);
        mPath.lineTo(getWidth() / 2 - 23, 3);
        mPath.lineTo(getWidth() / 2 - 1, 30);
        mPath.lineTo(getWidth() / 2 + 1, 30);
        mPath.lineTo(getWidth() / 2 + 23, 3);
        mPath.lineTo(getWidth() / 2 + 20, 0);
        mPath.close();
        Paint paint = new Paint(mPaint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(mPath, paint);
    }

    float downX;
    ValueAnimator animator;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animator != null) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (downX == 0) {
                    downX = event.getRawX();
                }
                mXMove = event.getRawX() - downX;
                if (mXLastMove + mXMove > leftBorder) {
                    downX = event.getRawX() - leftBorder + mXLastMove;
                    mXMove = leftBorder - mXLastMove;
                } else if (mXLastMove + mXMove < rightBorder) {
                    downX = event.getRawX() - rightBorder + mXLastMove;
                    mXMove = rightBorder - mXLastMove;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i <= mLineLength; i++) {
                    float p = Math.abs(getWidth() / 2 - (i * mLineInterval + mXLastMove + mXMove));

                    if (p <= mLineInterval / 2) {
                        offset = getWidth() / 2 - (i * mLineInterval + mXLastMove + mXMove);
                    }
                }
                animator = ValueAnimator.ofFloat(mXMove, mXMove + offset);
                animator.setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mXMove = (float) animation.getAnimatedValue();
                        invalidate();
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mXLastMove = mXLastMove + mXMove;

                        downX = 0;
                        mXMove = 0;
                        animator.cancel();
                        animator = null;
                    }
                });
                animator.start();
                break;
        }
        return true;
    }

    public void setSelectListener(OnSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }

}
