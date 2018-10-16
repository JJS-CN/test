package com.example.admin.test.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
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
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/9/4.
 */

public class RunBarChart extends View {
    private float minHeight = 165;//dp
    private TextPaint mTextPaint;
    private Paint mPaint;
    private Paint mPolyPaint;
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private int mWalkColor = Color.parseColor("#BBB9FD");//步行颜色
    private int mRunColor = Color.parseColor("#A3D1FC");//跑步颜色
    private int mLeftMoveing = 0;//左移动偏移
    private int[] mMarkerSize;//marker宽高
    private Rect mChartRect = new Rect();
    private List<ChartEntity> mEntities = new ArrayList<>();//内容
    private List<String> mLeftList = new ArrayList<>();
    private int mAxisYspace;//Y轴间距
    private int mAxisXspace;//X轴间距
    private int mAxisYvalue;//Y轴的数据计算
    private GradientDrawable mXLineDrawable = new GradientDrawable();
    private List<int[]> mPoiList = new ArrayList<>();
    private OnLeftTextChangeListener mListener;
    private int[] mClickXY = new int[]{0, 0};//按下的xy轴位置
    private int kmWidth;//km 的字体宽度
    private int barWidth = 8;//柱形的宽度 dp

    public RunBarChart(Context context) {
        super(context);
        init();
    }

    public RunBarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RunBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化参数
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(sp2px(10));
        mTextPaint.setStrokeWidth(2);
        kmWidth = (int) mTextPaint.measureText("km");
        barWidth = (int) dp2px(barWidth);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPolyPaint = new Paint();
        mPolyPaint.setAntiAlias(true);
        mPolyPaint.setStrokeWidth(dp2px(4));
        mPolyPaint.setStyle(Paint.Style.STROKE);

        scroller = new Scroller(getContext());

        mMarkerSize = new int[]{(int) dp2px(25), (int) dp2px(25)};

        this.setLayerType(LAYER_TYPE_SOFTWARE, null);//禁用硬件加速，因为要绘制虚线

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLeftMoveing < 0) {
            //左边界限制
            mLeftMoveing = 0;
        } else if (mLeftMoveing > mChartRect.right - getWidth() + dp2px(15)) {
            //右边界限制
            mLeftMoveing = (int) (mChartRect.right - getWidth() + dp2px(15));
        }
        drawLineX(canvas);
        drawLineY(canvas);
        drawAxisX(canvas);
        drawAxisY(canvas);
        drawPolyLine(canvas);
        drawMarker(canvas);
    }

    private void drawLineX(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mRunColor);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setShader(new LinearGradient(mChartRect.left, 0, mChartRect.right - mLeftMoveing, 0, mWalkColor, mRunColor, Shader.TileMode.CLAMP));
        //X轴绘制
        for (int i = 0; i <= mLeftList.size(); i++) {
            int baseline = mChartRect.bottom - i * mAxisYspace;
            if (i == 0) {
                canvas.drawLine(mChartRect.left, baseline, mChartRect.right - mLeftMoveing, baseline, paint);
                paint.setPathEffect(new DashPathEffect(new float[]{15, 5}, 0));
                continue;
            }
            canvas.drawLine(mChartRect.left, baseline, mChartRect.right - mLeftMoveing, baseline, paint);
        }
    }

    private void drawAxisX(Canvas canvas) {
        //X轴左侧文本
        mTextPaint.setTextSize(sp2px(10));
        mTextPaint.setColor(mWalkColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mLeftList.size(); i++) {
            int baseline = mChartRect.bottom - (i + 1) * mAxisYspace;
            canvas.drawText(mLeftList.get(i) + "", mChartRect.left / 2, baseline + mTextPaint.getFontMetrics().bottom, mTextPaint);
        }
    }


    private void drawLineY(Canvas canvas) {


    }

    private void drawAxisY(Canvas canvas) {
        //X轴左侧文本
        mTextPaint.setTextSize(sp2px(10));
        mTextPaint.setColor(mRunColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mEntities.size(); i++) {
            int x = (int) ((i + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
            if (x < mChartRect.left || x > getWidth()) {
                continue;
            }
            canvas.drawText(mEntities.get(i).bottomName + "", x, mChartRect.bottom + dp2px(5) - mTextPaint.getFontMetrics().ascent, mTextPaint);
        }
    }

    private void drawPolyLine(Canvas canvas) {
        mPoiList.clear();
        canvas.save();
        canvas.clipRect(mChartRect);
        for (int i = 0; i < mEntities.size(); i++) {
            int centerX = (int) ((i + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
            int walkY = (int) (mChartRect.bottom - mEntities.get(i).walk / mAxisYvalue * mAxisYspace);//步行Y轴
            int runY = (int) (mChartRect.bottom - mEntities.get(i).run / mAxisYvalue * mAxisYspace);//跑步Y轴
            mPoiList.add(new int[]{centerX, Math.min(runY, walkY)});//最大高度，中心点
            GradientDrawable walkDraw = new GradientDrawable();
            walkDraw.setCornerRadii(new float[]{barWidth / 2, barWidth / 2, barWidth / 2, barWidth / 2, 0, 0, 0, 0});
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                walkDraw.setColors(new int[]{0x00BBB9FD, 0xFFBBB9FD});//控制渐变色
                walkDraw.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            } else {
                walkDraw.setColor(mWalkColor);
            }
            walkDraw.setBounds(centerX - barWidth - barWidth / 2, walkY, centerX - barWidth / 2, mChartRect.bottom);
            walkDraw.draw(canvas);

            GradientDrawable runDraw = (GradientDrawable) walkDraw.mutate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                runDraw.setColors(new int[]{0x00A3D1FC, 0xFFA3D1FC});//控制渐变色
                runDraw.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            } else {
                runDraw.setColor(mRunColor);
            }
            runDraw.setBounds(centerX + barWidth / 2, runY, centerX + barWidth / 2 + barWidth, mChartRect.bottom);
            runDraw.draw(canvas);

        }
        canvas.restore();
    }

    private int mCheckPosition = -1;

    private void drawMarker(Canvas canvas) {
        mTextPaint.setTextSize(sp2px(8));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        int textWid = (int) mTextPaint.measureText("跑步");
        //绘制右上角标识
        GradientDrawable runDraw = new GradientDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            runDraw.setColors(new int[]{0x00A3D1FC, 0xFFA3D1FC});//控制渐变色
            runDraw.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        } else {
            runDraw.setColor(mRunColor);
        }
        Rect barRect = new Rect();
        barRect.right = (int) (getWidth() - (textWid + dp2px(6)));
        barRect.left = (int) (barRect.right - dp2px(26));
        barRect.top = (int) dp2px(6);
        barRect.bottom = (int) (barRect.top + dp2px(6));
        runDraw.setCornerRadii(new float[]{dp2px(3), dp2px(3), 0, 0, 0, 0, dp2px(3), dp2px(3)});
        runDraw.setBounds(barRect);
        runDraw.draw(canvas);

        canvas.drawText("跑步", getWidth() - (textWid + dp2px(6)) / 2, ((barRect.bottom - barRect.top) / 2 - (mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().ascent)) / 2 + barRect.top - mTextPaint.getFontMetrics().ascent, mTextPaint);

        GradientDrawable walkBar = (GradientDrawable) runDraw.mutate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            walkBar.setColors(new int[]{0x00BBB9FD, 0xFFBBB9FD});//控制渐变色
            walkBar.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        } else {
            walkBar.setColor(mWalkColor);
        }
        barRect.top = (int) (barRect.bottom + dp2px(6));
        barRect.bottom = (int) (barRect.top + dp2px(6));

        walkBar.setBounds(barRect);
        walkBar.draw(canvas);
        canvas.drawText("步行", getWidth() - (textWid + dp2px(6)) / 2, ((barRect.bottom - barRect.top) / 2 - (mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().ascent)) / 2 + barRect.top - mTextPaint.getFontMetrics().ascent, mTextPaint);


        int radius = barWidth + barWidth / 2;//触摸半径
        for (int i = 0; i < mPoiList.size(); i++) {
            int x = mPoiList.get(i)[0];
            int y = mPoiList.get(i)[1];
            if (x < mChartRect.left || x > getWidth()) {
                continue;
            }
            if (Math.abs(x - mClickXY[0]) < radius && mClickXY[1] < mChartRect.bottom && mClickXY[1] > mChartRect.top) {
                mCheckPosition = i;
                break;
            }
        }
        if (mCheckPosition >= 0 && mCheckPosition < mPoiList.size() && mPoiList.get(mCheckPosition)[0] > mChartRect.left) {
            int x = mPoiList.get(mCheckPosition)[0];
            int y = mPoiList.get(mCheckPosition)[1];
            //绘制marker
            GradientDrawable drawable = new GradientDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                drawable.setColors(new int[]{mWalkColor, mRunColor});//控制渐变色
                drawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
            } else {
                drawable.setColor(mWalkColor);
            }
            drawable.setCornerRadius(barWidth / 2);

            mTextPaint.setColor(Color.WHITE);
            ChartEntity entity = mEntities.get(mCheckPosition);
            int textWidth = (int) Math.max(mTextPaint.measureText(entity.walk + "km"), mTextPaint.measureText(entity.run + "km"));

            Rect rect = new Rect();
            rect.bottom = (int) (y - dp2px(3));
            rect.top = (int) (rect.bottom - dp2px(23));
            rect.left = (int) (x - textWidth / 2 - dp2px(2));
            rect.right = (int) (x + textWidth / 2 + dp2px(3));
            drawable.setBounds(rect);
            drawable.draw(canvas);

            canvas.drawText(entity.walk + "km", x, ((rect.bottom - rect.top) / 2 - (mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().ascent)) / 2 + rect.top - mTextPaint.getFontMetrics().ascent, mTextPaint);
            canvas.drawText(entity.run + "km", x, (rect.bottom - rect.top) / 2 + ((rect.bottom - rect.top) / 2 - (mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().ascent)) / 2 + rect.top - mTextPaint.getFontMetrics().ascent, mTextPaint);
        }
    }


    /**
     * 设置数据
     */
    public void setEntities(List<ChartEntity> entities) {
        mEntities = entities != null ? entities : new ArrayList<ChartEntity>();
        float maxValue = 0;
        for (int i = 0; i < mEntities.size(); i++) {
            ChartEntity entity = mEntities.get(i);
            maxValue = Math.max(entity.walk, maxValue);
            maxValue = Math.max(entity.run, maxValue);
        }
        mAxisYvalue = (int) Math.ceil(maxValue / 2);//用于数据计算
        float mTextWidth = 0;
        mLeftList.clear();
        mTextPaint.setTextSize(sp2px(10));
        for (int i = 1; i < 3; i++) {
            String str = mListener != null ? mListener.onChange(mAxisYvalue * i) : mAxisYvalue + "";
            str = str == null ? "" : str;
            mTextWidth = Math.max(mTextPaint.measureText(str), mTextWidth);
            mLeftList.add(str);
        }
        mChartRect.left = (int) (mTextWidth + dp2px(5));
        invalidate();
    }

    public void setListener(OnLeftTextChangeListener listener) {
        mListener = listener;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mTextPaint.setTextSize(sp2px(10));
            int textHeight = (int) (Math.abs(mTextPaint.getFontMetrics().ascent) + mTextPaint.getFontMetrics().bottom + dp2px(8));//下面padding，计算偏移量
            mAxisYspace = (int) ((getHeight() - textHeight - mMarkerSize[1] * 2.5f) / 2f);//Y轴间距
            mAxisXspace = (int) (mAxisYspace * 1.1f);//X轴间距

            mChartRect.bottom = getHeight() - textHeight;//底部线，X轴初始线
            //   mChartRect.top = mMarkerSize[1] - mAxisYspace / 2;//顶部
            mChartRect.top = mChartRect.bottom - mAxisYspace * 2;
            mChartRect.right = (int) (mAxisXspace * (mEntities.size() + 0.5f));
        }
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

    private int movingThisTime;
    private int lastPointX;

    private int[] dx;

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            movingThisTime = (scroller.getCurrX() - lastPointX);
            mLeftMoveing = mLeftMoveing + movingThisTime;
            lastPointX = scroller.getCurrX();
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = new int[]{(int) event.getX(), (int) event.getY()};
                lastPointX = (int) event.getX();
                scroller.abortAnimation();//如果在滑动终止动画
                initOrResetVelocityTracker();//初始化速度跟踪器
                mClickXY = new int[]{0, 0};
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float movex = event.getX();
                movingThisTime = (int) (lastPointX - movex);
                mLeftMoveing = mLeftMoveing + movingThisTime;
                lastPointX = (int) movex;
                invalidate();
                velocityTracker.addMovement(event);//将用户的action添加到跟踪器中。
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (Math.abs(dx[0] - event.getX()) < dp2px(6) && Math.abs(dx[1] - event.getY()) < dp2px(6)) {
                    mClickXY = new int[]{(int) event.getX(), (int) event.getY()};
                }
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

    //数据，限定一Y轴一个数据
    public static class ChartEntity {
        public float run;
        public float walk;
        public String bottomName;//底部文本

        public ChartEntity(float run, float walk, String bottomName) {
            this.run = run;
            this.walk = walk;
            this.bottomName = bottomName;
        }
    }

    //供外部重写,left文本内容
    public interface OnLeftTextChangeListener {
        String onChange(int value);
    }


}
