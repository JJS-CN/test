package com.example.admin.test.baseView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/8/25.
 */

public class BasicRatingBar extends LinearLayout {
    private int DEFAULT_MAX_RATING = 5;
    private int DEFAULT_MIN_RATING = 1;
    private float DEFAULT_STEP_SIZE = 1f;//默认一整颗星星的改变
    private float DEFAULT_RATING = 1f;//默认有几颗星星
    private int mMaxRating = DEFAULT_MAX_RATING;//最大星星数
    private float mMinRating = DEFAULT_MIN_RATING;//最小数
    private float mRating = DEFAULT_RATING;//当前数
    private float mStepSize = DEFAULT_STEP_SIZE;//增加的步长
    private float mRatingInterval = 2;//星星之间的间隔(dp)
    private boolean isIntercept = false;//是否拦截触摸事件。

    private int DEFAULT_PADDING = 5;//padding （dp）

    private int mRatingBg = R.mipmap.ic_rating_bg;
    private int mRatingSeek = R.mipmap.ic_rating_seek;
    private int[] mRatingSize;
    private int[] mRatingSeekSize;
    private List<RectF> mRects;

    private OnRatingChangeListener mRatingChangeListener;//当前星星数改变时回调

    public BasicRatingBar(Context context) {
        super(context);
        init();
    }

    public BasicRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasicRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRatingInterval = dp2px(mRatingInterval);
        DEFAULT_PADDING = (int) dp2px(DEFAULT_PADDING);
        this.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        this.setBackgroundColor(Color.TRANSPARENT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), mRatingBg, options);
        mRatingSize = new int[]{options.outWidth, options.outHeight};
        BitmapFactory.decodeResource(getResources(), mRatingSeek, options);
        mRatingSeekSize = new int[]{options.outWidth, options.outHeight};

        setRating(mRating);//初始触发监听
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension((int) (mRatingSize[0] * mMaxRating + (mMaxRating - 1) * mRatingInterval) + getPaddingLeft() + getPaddingRight(), mRatingSize[1] + getPaddingTop() + getPaddingBottom());
    }

    private Paint mPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }
        mRects = new ArrayList<>();
        for (int i = 1; i <= mMaxRating; i++) {
            drawRating(canvas, mRatingBg, 1f, i);
            if (mRating >= i) {
                drawRating(canvas, mRatingSeek, 1f, i);
            } else if (mRating <= i - 1) {

            } else {
                drawRating(canvas, mRatingSeek, mRating - (i - 1), i);
            }
        }

    }

    private void drawRating(Canvas canvas, int drawbg, float step, int position) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawbg, null);
        int left = (int) (mRatingSize[0] * (position - 1) + mRatingInterval * (position - 1)) + getPaddingLeft();
        int dy = getPaddingTop();
        if (drawbg == mRatingSeek) {
            left = left + (mRatingSize[0] - mRatingSeekSize[0]) / 2;
            dy = dy + (mRatingSize[1] - mRatingSeekSize[1]) / 2;
        }

        canvas.save();
        canvas.translate(left, dy);
        if (drawbg == mRatingBg) {
            mRects.add(new RectF(left, 0, left + bmp.getWidth(), getHeight()));
        }
        int right = (int) (bmp.getWidth() * step);
        canvas.drawBitmap(bmp, new Rect(0, 0, right, bmp.getHeight()), new RectF(0, 0, right, bmp.getHeight()), mPaint);
        canvas.restore();
        bmp.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isIntercept) {
            return false;
        }
        for (int i = 0; i < mRects.size(); i++) {
            RectF mRect = mRects.get(i);
            if (mRect.contains(event.getX(), 0)) {
                float step = (event.getX() - mRect.left) / (mRect.right - mRect.left);
                int count = (int) (step / mStepSize);
                if (step % mStepSize >= mStepSize / 2) {
                    ++count;
                }
                setRating(i + count * mStepSize);
                return true;
            } else if (mRect.right < event.getX() && i < mRects.size() - 1 && mRects.get(i + 1).left > event.getX()) {
                setRating(i + 1);
                return true;
            } else if (i == mRects.size() - 1 && event.getX() > mRect.right) {
                setRating(mMaxRating);
                return true;
            } else if (i == 0 && event.getX() < mRect.left) {
                setRating(mMinRating);
                return true;
            }
        }

        return true;
    }

    public float getRating() {
        return mRating;
    }

    public void setMaxRating(int maxRating) {
        mMaxRating = maxRating;
        invalidate();
    }

    public void setMinRating(float minRating) {
        mMinRating = minRating;
    }

    public void setRating(float rating) {
        if (rating < mMinRating) {
            rating = mMinRating;
        }
        mRating = rating;
        if (mRatingChangeListener != null) {
            mRatingChangeListener.onChange(mRating);
        }
        invalidate();
    }

    public void setStepSize(float stepSize) {
        mStepSize = stepSize;
    }

    public void setIntercept(boolean intercept) {
        isIntercept = intercept;
    }

    public void setRatingChangeListener(OnRatingChangeListener ratingChangeListener) {
        mRatingChangeListener = ratingChangeListener;
    }

    public interface OnRatingChangeListener {
        void onChange(float rating);
    }

    public void setRatingInterval(float ratingInterval) {
        mRatingInterval = dp2px(ratingInterval);
    }


    private float dp2px(float dp) {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dp * dm.density + 0.5f;
    }
}
