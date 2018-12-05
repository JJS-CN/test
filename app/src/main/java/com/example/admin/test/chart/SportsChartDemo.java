package com.example.admin.test.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
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

import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：运动数据折线图
 * Created by jjs on 2018/8/31.
 */

public class SportsChartDemo extends View {
    private float minHeight = 165;//dp
    private TextPaint mTextPaint;//文本画笔
    private Paint mPaint;//边线画笔
    private Paint mPolyPaint;//主要折线画笔
    private Scroller scroller;//滚动操作类
    private VelocityTracker velocityTracker;//flying效果辅助类
    private int mStartColor = Color.parseColor("#C7C7F9");//左侧文本颜色
    private int mEndColor = Color.parseColor("#ACC2FA");//底部文本颜色
    private int mCenterColor = Color.parseColor("#50C7C7F9");//居中透明色
    private int mPoiColor = Color.parseColor("#81ADFF");//poi颜色
    private int mLeftMoveing = 0;//左移动偏移
    private int mMarkerDrawableId = R.mipmap.ic_chart_message;//marker点的图片
    private int[] mMarkerSize;//marker宽高
    private Rect mChartRect = new Rect();//滚动区域
    private List<ChartEntity> mEntities = new ArrayList<>();//点内容
    private List<String> mLeftList = new ArrayList<>();//左侧文本
    private int mAxisYspace;//Y轴间距
    private int mAxisXspace;//X轴间距
    private int mAxisYvalue;//Y轴的数据计算
    private GradientDrawable mXLineDrawable = new GradientDrawable();
    private List<int[]> mPoiList = new ArrayList<>();//处理之后的点xy集合
    private OnLeftTextChangeListener mListener;
    private int[] mClickXY = new int[]{0, 0};//按下的xy轴位置
    private Bitmap mMarker;
    private int kmWidth;//km 的字体宽度

    public SportsChartDemo(Context context) {
        super(context);
        init();
    }

    public SportsChartDemo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SportsChartDemo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPolyPaint = new Paint();
        mPolyPaint.setAntiAlias(true);
        mPolyPaint.setStrokeWidth(dp2px(4));
        mPolyPaint.setStyle(Paint.Style.STROKE);

        scroller = new Scroller(getContext());

      /*  BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;*/
        mMarker = BitmapFactory.decodeResource(getResources(), mMarkerDrawableId, null);
        mMarkerSize = new int[]{mMarker.getWidth(), mMarker.getHeight()};

        //  mXLineDrawable.setColors(new int[]{mStartColor, mEndColor});
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
        //X轴绘制
        for (int i = 0; i <= 5; i++) {
            int baseline = (int) (mChartRect.bottom - i * mAxisYspace);
            if (i == 5) {
                baseline = (int) (mChartRect.bottom - 4.5f * mAxisYspace);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mXLineDrawable.setColors(new int[]{mStartColor, mEndColor});
                //  mXLineDrawable.setColors(new int[]{mGradientUtil.getColor(mLeftMoveing / (mChartRect.right - mChartRect.left)), mGradientUtil.getColor(mLeftMoveing + getWidth() - mChartRect.left / (mChartRect.right - mChartRect.left))});
            } else {
                mXLineDrawable.setColor(mEndColor);
            }

            mXLineDrawable.setBounds(mChartRect.left, baseline - 1, mChartRect.right - mLeftMoveing, baseline + 1);
            mXLineDrawable.draw(canvas);
        }
    }

    private void drawAxisX(Canvas canvas) {
        //X轴左侧文本
        for (int i = 0; i < mLeftList.size(); i++) {
            int baseline = mChartRect.bottom - (i + 1) * mAxisYspace;
            mTextPaint.setColor(mStartColor);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mLeftList.get(i) + "", mChartRect.left / 2, baseline + mTextPaint.getFontMetrics().bottom, mTextPaint);
        }
    }


    private void drawLineY(Canvas canvas) {
        mPaint.setColor(mStartColor);
        canvas.drawLine(mChartRect.left, mChartRect.top, mChartRect.left, mChartRect.bottom, mPaint);

        Path pathY = new Path();
        for (int i = 0; i < mEntities.size(); i++) {
            int x = (int) ((i + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
            if (x < mChartRect.left || x > getWidth()) {
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
        mPaint.setShader(new LinearGradient(mChartRect.left, 0, mChartRect.right, 0, mStartColor, mEndColor, Shader.TileMode.CLAMP));
        canvas.drawPath(pathY, mPaint);
        // int x = mEntities.size() * mAxisXspace + mChartRect.left - mLeftMoveing;
        canvas.drawLine(mChartRect.right - mLeftMoveing, mChartRect.bottom, mChartRect.right - mLeftMoveing, mChartRect.top, mPaint);
    }

    private void drawAxisY(Canvas canvas) {
        //X轴左侧文本
        for (int i = 0; i < mEntities.size(); i++) {
            int x = (int) ((i + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
            if (x < mChartRect.left || x > getWidth()) {
                continue;
            }
            mTextPaint.setColor(mEndColor);
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mEntities.get(i).bottomName + "", x, mChartRect.bottom + dp2px(5) - mTextPaint.getFontMetrics().ascent, mTextPaint);
        }
    }

    private void drawPolyLine(Canvas canvas) {
        mPoiList.clear();
        Path path = new Path();
        Path centerPath = new Path();
        for (int i = 0; i < mEntities.size(); i++) {
            int x = (int) ((i + 0.5f) * mAxisXspace) + mChartRect.left - mLeftMoveing;
            int y = (int) (mChartRect.bottom - mEntities.get(i).value / mAxisYvalue * mAxisYspace);
            /*if (y > mChartRect.bottom - mPolyPaint.getStrokeWidth() / 2) {
                y = (int) (mChartRect.bottom - mPolyPaint.getStrokeWidth() / 2);
            }*/
            if (i == 0) {
                centerPath.moveTo(x, mChartRect.bottom);
                centerPath.lineTo(x, y);
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
                centerPath.lineTo(x, y);
            }
            if (i == mEntities.size() - 1) {
                centerPath.lineTo(x, mChartRect.bottom);
                centerPath.close();
            }
            mPoiList.add(new int[]{x, y});
        }
        canvas.save();
        canvas.clipRect(mChartRect);
        mPolyPaint.setPathEffect(new CornerPathEffect(25));
        mPolyPaint.setShader(new LinearGradient(mChartRect.left, 0, mChartRect.right, 0, mStartColor, mEndColor, Shader.TileMode.CLAMP));
        canvas.drawPath(path, mPolyPaint);
        mPaint.setColor(mCenterColor);
        canvas.drawPath(centerPath, mPaint);

        canvas.restore();
    }

    private int mCheckPosition = -1;

    private void drawMarker(Canvas canvas) {
        /*canvas.save();
        int margin = (int) dp2px(6);
        canvas.clipRect(mChartRect.left - margin, mChartRect.top - margin, mChartRect.right + margin, mChartRect.bottom + margin);
    */

        int radius = (int) dp2px(12);//触摸半径
        for (int i = 0; i < mPoiList.size(); i++) {
            int x = mPoiList.get(i)[0];
            int y = mPoiList.get(i)[1];
            if (x < mChartRect.left || x > getWidth()) {
                continue;
            }
            mPaint.setColor(mPoiColor);
            canvas.drawCircle(x, y, dp2px(6), mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(x, y, dp2px(3), mPaint);

            if (Math.abs(x - mClickXY[0]) < radius && Math.abs(y - mClickXY[1]) < radius) {
                mCheckPosition = i;
            }
        }
        if (mCheckPosition >= 0 && mCheckPosition < mPoiList.size()) {
            if (mPoiList.get(mCheckPosition)[0] > mChartRect.left) {
                int x = mPoiList.get(mCheckPosition)[0] - mMarker.getWidth() / 2;
                int y = mPoiList.get(mCheckPosition)[1] - mMarker.getHeight();
                canvas.drawBitmap(mMarker, x, y, mPaint);
                mTextPaint.setTextSize(sp2px(12));
                mTextPaint.setColor(mPoiColor);
                mTextPaint.setFakeBoldText(true);
                int textWidth = (int) mTextPaint.measureText(mEntities.get(mCheckPosition).value + "");
                canvas.drawText(mEntities.get(mCheckPosition).value + "", mPoiList.get(mCheckPosition)[0] - (textWidth + kmWidth) / 2, y + mMarker.getHeight() / 2, mTextPaint);
                mTextPaint.setTextSize(sp2px(10));
                canvas.drawText("km", mPoiList.get(mCheckPosition)[0] - (textWidth + kmWidth) / 2 + textWidth + dp2px(1), y + mMarker.getHeight() / 2, mTextPaint);

                mTextPaint.setFakeBoldText(false);
            }
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
            maxValue = Math.max(entity.value, maxValue);
        }
        mAxisYvalue = (int) Math.ceil(maxValue / 4);//用于数据计算
        float mTextWidth = 0;
        mLeftList.clear();
        for (int i = 1; i < 5; i++) {
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
            int textHeight = (int) (Math.abs(mTextPaint.getFontMetrics().ascent) + mTextPaint.getFontMetrics().bottom + dp2px(8));//下面padding，计算偏移量
            mAxisYspace = (int) ((getHeight() - textHeight - mMarkerSize[1]) / 4f);//Y轴间距-预留顶部maker显示区域，剩下区域等分4份
            mAxisXspace = (int) (mAxisYspace * 0.9f);//X轴间距

            mChartRect.bottom = getHeight() - textHeight;//底部线，X轴初始线
            mChartRect.top = mMarkerSize[1] - mAxisYspace / 2;//顶部
            mChartRect.right = (int) (mAxisXspace * (mEntities.size() + 1.5f));
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
        public float value;//Y轴高度值
        public String bottomName;//底部文本

        public ChartEntity(float value, String bottomName) {
            this.value = value;
            this.bottomName = bottomName;
        }
    }

    //供外部重写,left文本内容
    public interface OnLeftTextChangeListener {
        String onChange(int value);
    }

}

