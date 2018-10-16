package com.example.admin.test.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.FloatRange;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 说明：膝关节耐久度曲线图
 * 由于转折点为圆弧曲线，精度将小幅度丢失
 * Created by jjs on 2018/8/31.
 */

public class DurableChart extends View {
    private float minHeight = 240;//dp 257
    private TextPaint mTextPaint;
    private Paint mPaint;
    private Paint mPolyPaint;
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private int mLeftMoveing = 0;//左移动偏移
    private int mMarkerDrawableId = R.mipmap.ic_poi_durable;
    private int[] mMarkerSize;//marker宽高
    private Rect mChartRect = new Rect();
    private List<ChartEntity> mEntities = new ArrayList<>();//内容
    private float mAxisYspace;//Y轴间距
    private int mAxisXspace;//X轴间距
    private float mAxisYvalue;//Y轴的数据计算
    private List<int[]> mPoiList = new ArrayList<>();
    private Bitmap mMarker;
    private int endPoiLength = 2;//尾部多余结束点数量

    public DurableChart(Context context) {
        super(context);
        init();
    }

    public DurableChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DurableChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setLayerType(LAYER_TYPE_SOFTWARE, null);//禁用硬件加速，因为要绘制虚线
        //初始化参数
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(sp2px(10));
        mTextPaint.setStrokeWidth(2);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPolyPaint = new Paint();
        mPolyPaint.setAntiAlias(true);
        mPolyPaint.setStrokeWidth(dp2px(4));
        mPolyPaint.setStyle(Paint.Style.STROKE);

        scroller = new Scroller(getContext());

        mMarker = BitmapFactory.decodeResource(getResources(), mMarkerDrawableId, null);
        mMarkerSize = new int[]{mMarker.getWidth(), mMarker.getHeight()};

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mEntities.size() == 0) {
            return;
        }
        if (mLeftMoveing < 0 - getWidth() / 2 + mAxisXspace / 2) {
            //左边界限制
            mLeftMoveing = 0 - getWidth() / 2 + mAxisXspace / 2;
        } else if (mLeftMoveing > mChartRect.right - getWidth() / 2 - mAxisXspace * (1 + endPoiLength)) {
            //右边界限制
            mLeftMoveing = (int) (mChartRect.right - getWidth() / 2 - mAxisXspace * (1 + endPoiLength));
        }
        Rect bgRect = new Rect(0, 0, getWidth(), getHeight());
        GradientDrawable drawable = new GradientDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            drawable.setColors(new int[]{Color.parseColor("#544D94"), Color.parseColor("#305C87")});
            drawable.setOrientation(GradientDrawable.Orientation.BL_TR);
        } else {
            drawable.setColor(Color.parseColor("#305C87"));
        }
        drawable.setBounds(bgRect);
        drawable.draw(canvas);
        drawLineX(canvas);
        drawAxisY(canvas);
        drawPolyLine(canvas);
        drawMarker(canvas);

        GradientDrawable drawable2 = new GradientDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            drawable2.setColors(new int[]{0xFF544D94, 0x50544D94, 0x00544D94, 0x00305C87, 0x50305C87, 0xFF305C87});
            drawable2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        } else {
            drawable2.setColor(0x90305C87);
        }

        drawable2.setBounds(bgRect);
        drawable2.draw(canvas);
    }

    private void drawLineX(Canvas canvas) {
        //X轴绘制
        int[] colors = new int[]{Color.parseColor("#444283"), Color.parseColor("#6A70B3"), Color.parseColor("#37598C")};
        float[] floats = new float[]{0.2f, 0.6f, 0.2f};
        Paint paint = new Paint();
        paint.setPathEffect(new DashPathEffect(new float[]{25, 15}, 0));
        paint.setStrokeWidth(3);
        paint.setShader(new LinearGradient(mChartRect.left, 0, mChartRect.right, 0, colors, floats, Shader.TileMode.CLAMP));

        for (int i = 0; i <= 3; i++) {
            int baseline = (int) (mChartRect.bottom - i * mAxisYspace);
            int left = mChartRect.left;
            int right = mChartRect.right;
            canvas.drawLine(left, baseline, right, baseline, paint);
        }
    }


    private void drawAxisY(Canvas canvas) {
        //绘制底部文本
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(sp2px(12));
        for (int i = 0; i < mEntities.size(); i++) {
            int x = (int) ((i + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
            if (x < mChartRect.left - 50 || x > getWidth() + 50) {
                continue;
            }
            mTextPaint.setColor(Color.WHITE);
            canvas.drawText(mEntities.get(i).bottomName + "", x, mChartRect.bottom + mAxisYspace * 0.6f, mTextPaint);
        }
    }

    private void drawPolyLine(Canvas canvas) {
        mPoiList.clear();
        Path mPolyPath = new Path();
        float orangeLength = 0;//橙色线长度
        float pathLength = 0;//当前线长度
        boolean isMeasure = false;//是否已经测量完成
        int oldX = 0;
        int oldY = 0;
        for (int i = 0; i < mEntities.size(); i++) {
            int x = (int) ((i + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
            int y = (int) (mChartRect.bottom - mEntities.get(i).value / mAxisYvalue * mAxisYspace);
            if (i == 0) {
                mPolyPath.moveTo(x, y);
            } else {
                //path位置正常，没有偏差
                mPolyPath.lineTo(x - (x - oldX) * 0.2f, y + (oldY - y) * 0.2f);
                int lastX = (int) ((i + 1 + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
                int lastY = (int) (mChartRect.bottom - mEntities.get(i + 1 > mEntities.size() - 1 ? i : i + 1).value / mAxisYvalue * mAxisYspace);
                mPolyPath.quadTo(x, y, x + (lastX - x) * 0.2f, y + (lastY - y) * 0.2f);
            }
            mPoiList.add(new int[]{x, y});

            if (!isMeasure) {
                //todo 可优化，+Xspace进行判断是否超过
                if (x >= getWidth() / 2) {
                    isMeasure = true;
                    //x超过中间点，取上一个参数，并取差值
                    orangeLength = pathLength;
                    //计算新长度
                    PathMeasure measure = new PathMeasure(mPolyPath, false);
                    float l = measure.getLength();
                    //计算中间位置差值，百分比
                    float l2 = l - orangeLength;//超出X与未超出X的长度差
                    float sc = ((float) mAxisXspace - ((float) x - getWidth() / 2)) / (float) mAxisXspace;//未超出X距中间 相对于Xspace的百分比
                    float sss = l2 * sc;//根据百分比计算实际长度
                    orangeLength += sss - mAxisXspace * 0.30f * Math.min(1f, (i > 1) ? (x - oldX) / mAxisXspace : 1f * sc);//叠加
                }
                PathMeasure measure = new PathMeasure(mPolyPath, false);
                pathLength = measure.getLength();
            }
            oldX = x;
            oldY = y;

        }
        mPolyPaint.setStrokeCap(Paint.Cap.ROUND);
        mPolyPaint.setColor(Color.parseColor("#4871AB"));
        canvas.drawPath(mPolyPath, mPolyPaint);
        //绘制橙色线
        Path nPath = new Path();
        PathMeasure measure = new PathMeasure(mPolyPath, false);
        measure.getSegment(0, orangeLength, nPath, true);//根据长度，截取path
        measure.getPosTan(orangeLength, markerXY, null);//根据长度，获取xy和方向

        mPolyPaint.setShader(new LinearGradient(0, 0, getWidth() / 2, 0, Color.parseColor("#30ffffff"), Color.parseColor("#EF9905"), Shader.TileMode.CLAMP));
        canvas.drawPath(nPath, mPolyPaint);
        mPolyPaint.setShader(null);
    }

    private float[] markerXY = new float[2];

    private void drawMarker(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#EF9905"));
        canvas.drawCircle(getWidth() / 2, markerXY[1], dp2px(6), mPaint);
        canvas.drawBitmap(mMarker, getWidth() / 2 - mMarker.getWidth() / 2, markerXY[1] - mMarker.getHeight(), null);

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(sp2px(15));
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        String value = ((int) ((mAxisYspace * 3f + mMarkerSize[1] - markerXY[1]) / (mAxisYspace * 3f) * 100f) - 5) + "%";
        canvas.drawText(value, getWidth() / 2, markerXY[1] - mMarker.getHeight() * 0.5f, mTextPaint);
        mTextPaint.setFakeBoldText(false);
    }


    /**
     * 设置数据
     */
    public void setEntities(List<ChartEntity> entities) {
        mEntities = entities != null ? entities : new ArrayList<ChartEntity>();
        float lastValue = mEntities.get(mEntities.size() - 1).value;

        for (int i = 0; i < endPoiLength; i++) {
            float value = i == 0 ? lastValue * 0.3f : i == 1 ? lastValue * 0.4f : 0f;
            String day = new SimpleDateFormat("M.dd").format(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * (i + 1)));
            mEntities.add(new ChartEntity(value, "预计" + day));
        }
        float maxValue = 0;
        for (int i = 0; i < mEntities.size(); i++) {
            ChartEntity entity = mEntities.get(i);
            maxValue = Math.max(entity.value, maxValue);
        }
        mAxisYvalue = 1f / 3f;//用于数据计算
        mChartRect.left = 0;
        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mAxisYspace = (int) ((getHeight() - (mMarkerSize[1] - dp2px(8))) / 4.2f);//Y轴间距
            mAxisXspace = (int) (mAxisYspace * 1.5f);//X轴间距

            mChartRect.bottom = (int) (getHeight() - mAxisYspace * 1.2f);//底部线，X轴初始线
            mChartRect.top = (int) (mMarkerSize[1] - dp2px(8));//顶部
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
        } else if (!isMove) {
            //todo 执行位置定位
        }
    }

    private boolean isMove;//是否还在滑动

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = new int[]{(int) event.getX(), (int) event.getY()};
                lastPointX = (int) event.getX();
                scroller.abortAnimation();//如果在滑动终止动画
                initOrResetVelocityTracker();//初始化速度跟踪器
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                float movex = event.getX();
                movingThisTime = (int) (lastPointX - movex);
                mLeftMoveing = mLeftMoveing + movingThisTime;
                lastPointX = (int) movex;
                invalidate();
                velocityTracker.addMovement(event);//将用户的action添加到跟踪器中。
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isMove = false;
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
        public float value;//Y轴高度值
        public String bottomName;//底部文本

        public ChartEntity(@FloatRange(from = 0f, to = 1f) float value, String bottomName) {
            this.value = value;
            this.bottomName = bottomName;
        }
    }


}

