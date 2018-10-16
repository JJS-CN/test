package com.example.admin.test.chart;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Scroller;

import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：运动数据
 * Created by jjs on 2018/8/30.
 */

public class SportsDataChart extends View {

    private float minHeight = 145;//dp
    private TextPaint mTextPaint;
    private Paint mPaint;
    private float mTextSize = 10;//sp
    private int mLeftTextColor = Color.parseColor("#C7C7F9");//左侧文本颜色
    private int mBottomTextColor = Color.parseColor("#ACC2FA");//底部文本颜色
    private int mStartColor = mLeftTextColor;//开始颜色
    private int mEndColor = mBottomTextColor;//结束颜色
    private int mCenterColor = Color.parseColor("#50C7C7F9");//居中透明色
    private int mPoiColor = Color.parseColor("#81ADFF");//poi颜色
    private int mTextPaintWidth = 2;//文字粗细

    private GradientDrawable mAxisX;//X轴线
    private GradientDrawable mAxisY;//Y轴线
    private float mAxisYSpac;//X轴之间的间隔，即为高度间隔（由高度动态计算）-用于坐标计算
    private float mAxisXSpac;//Y轴之间的间隔，即为宽度间隔（由X进行系数相乘得到）-用于坐标计算
    private float mAxisXSpacValue;//用于数据计算
    //private float mAxisYSpacValue;//用于数据计算
    private float mSpacXYsc = 0.9f;//宽度为高度的0.9
    private int mBaseAxisY;//baseY轴坐标点

    private int mMarkerDrawableId = R.mipmap.ic_chart_message;
    private int[] mMarkerSize;//marker宽高
    private int mLeftOffset = 0;//左偏移
    private Rect mChartRect = new Rect();
    private Scroller scroller;
    private VelocityTracker velocityTracker;

    private List<SportChartEntity> mChartEntities = new ArrayList<>();//数据
    private OnLeftTextChangeListener mTextChangeListener = new OnLeftTextChangeListener() {
        @Override
        public String onChange(int value) {
            return String.valueOf(value);
        }
    };

    public SportsDataChart(Context context) {
        super(context);
        init();
    }

    public SportsDataChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SportsDataChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float minH = dp2px(minHeight);//设置最小高度
        if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, (int) minH);
        }
    }

    private void init() {
        //初始化参数
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(sp2px(mTextSize));
        mTextPaint.setStrokeWidth(mTextPaintWidth);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);

        //X横轴默认全部渐变
        mAxisX = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{mStartColor, mEndColor});
        //Y纵轴第一条实线初始色，其他都为虚线，根据进-步数 设置为对应的渐变颜色
        mAxisY = new GradientDrawable();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), mMarkerDrawableId, options);
        mMarkerSize = new int[]{options.outWidth, options.outHeight};

        scroller = new Scroller(getContext());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLeftOffset < 0) {
            mLeftOffset = 0;
        }
        drawBaseXY(canvas);
        drawLineX(canvas);
        drawLineY(canvas);
    }


    private void drawLineX(Canvas canvas) {
        for (int i = 1; i <= 5; i++) {
            int baseline = (int) (mChartRect.bottom - i * mAxisYSpac);
            if (i == 5) {
                baseline = (int) (mChartRect.bottom - 4.5f * mAxisYSpac);
            }
            mAxisX.setBounds(mChartRect.left, baseline - 1, mChartRect.right - mLeftOffset, baseline + 1);
            mAxisX.draw(canvas);
        }
    }

    private void drawLineY(Canvas canvas) {
        Path pathY = new Path();
        for (int i = 0; i < mChartEntities.size(); i++) {
            int x = (int) ((i + 0.5f) * mAxisXSpac) + mChartRect.left - mLeftOffset;
            if (x < mChartRect.left) {
                continue;
            }
            pathY.moveTo(x, mChartRect.bottom);
            pathY.lineTo(x, mChartRect.top);
        }
        Paint mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(mEndColor);
        mPaint.setAntiAlias(true);
        mPaint.setPathEffect(new DashPathEffect(new float[]{15, 5}, 0));
        canvas.drawPath(pathY, mPaint);
    }

    private void drawBaseXY(Canvas canvas) {
        mTextPaint.setColor(mStartColor);
        canvas.drawLine(mChartRect.left, mChartRect.top, mChartRect.left, mChartRect.bottom, mTextPaint);//X轴
        mAxisX.setBounds(mChartRect.left, mChartRect.bottom - 1, mChartRect.right, mChartRect.bottom + 1);
        mAxisX.draw(canvas);
    }


    public void setChartEntities(List<SportChartEntity> chartEntities) {
        mChartEntities = chartEntities;
        invalidateAll();
    }


    public void setTextChangeListener(OnLeftTextChangeListener textChangeListener) {
        mTextChangeListener = textChangeListener;
    }

    private void invalidateAll() {
        float width = 0;
        float maxValue = 0;
        for (int i = 0; i < mChartEntities.size(); i++) {
            SportChartEntity entity = mChartEntities.get(i);
            width = Math.max(mTextPaint.measureText(entity.bottomName), width);
            maxValue = Math.max(entity.value, maxValue);
        }
        mChartRect.left = (int) (width + dp2px(5));

        mAxisXSpacValue = (int) Math.ceil(maxValue / 4);//用于数据计算
        invalidate();
    }

    //供外部重写,left文本内容
    public interface OnLeftTextChangeListener {
        String onChange(int value);
    }

    //数据，限定一Y轴一个数据
    public static class SportChartEntity {
        public float value;//Y轴高度值
        public String bottomName;//底部文本

        public SportChartEntity(float value, String bottomName) {
            this.value = value;
            this.bottomName = bottomName;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            int textHeight = (int) (Math.abs(mTextPaint.getFontMetrics().ascent) + mTextPaint.getFontMetrics().bottom + dp2px(8));//下面padding，计算偏移量
            mAxisYSpac = (getHeight() - textHeight - mMarkerSize[1]) / 4f;
            mBaseAxisY = getHeight() - textHeight;
            mAxisXSpac = mAxisYSpac * mSpacXYsc;

            mChartRect.bottom = getHeight() - textHeight;
            mChartRect.top = (int) (mMarkerSize[1] - mAxisYSpac / 2);
            mChartRect.right = (int) (mAxisXSpac * (mChartEntities.size() + 1));
        }
    }

    int movingThisTime;
    int lastPointX;

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            movingThisTime = (scroller.getCurrX() - lastPointX);
            mLeftOffset = mLeftOffset + movingThisTime;
            lastPointX = scroller.getCurrX();
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPointX = (int) event.getX();
                scroller.abortAnimation();//如果在滑动终止动画
                initOrResetVelocityTracker();//初始化速度跟踪器
                //Log.i("computeScroll","ACTION_DOWN:"+lastPointX);
                break;
            case MotionEvent.ACTION_MOVE:
                float movex = event.getX();
                movingThisTime = (int) (lastPointX - movex);
                mLeftOffset = mLeftOffset + movingThisTime;
                lastPointX = (int) movex;
                invalidate();
                velocityTracker.addMovement(event);//将用户的action添加到跟踪器中。
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000, ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity());//根据已经到达的点计算当前速度。
                int initialVelocity = (int) velocityTracker.getXVelocity();//获得最后的速度
                velocityTracker.clear();
                //通过scroller让它飞起来
                scroller.fling((int) event.getX(), (int) event.getY(), -initialVelocity / 2,
                        0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                lastPointX = (int) event.getX();
                invalidate();
                recycleVelocityTracker();//回收速度跟踪器
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    private void initOrResetVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
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
}
