package com.example.admin.test.Bezier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/9/1.
 */

public class TimeRoteView extends View {
    private TextPaint mTextPaint;
    private Paint mPaint;
    private int mStartColor = Color.parseColor("#C7C7F9");//左侧文本颜色
    private int mEndColor = Color.parseColor("#ACC2FA");//底部文本颜色
    private int minX;
    private int maxX;
    private String mTitleStr = "小时:分钟";
    private List<Float> rangeList;
    private ValueAnimator animator;
    private OnCheckedListener mCheckedListener;
    private List<String> mTimeList;//时间数据
    private List<String> mMileageList;//里程数据
    private int mCheckType;
    private int[] mTypeLeftXY;
    private int[] mTypeRightXY;

    public TimeRoteView(Context context) {
        super(context);
        init();
    }

    public TimeRoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeRoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#6681FD"));
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        minX = (int) dp2px(22);
        maxX = (int) (getWidth() - dp2px(22));

        //设置各点位置
        rangeList = new ArrayList<>();
        rangeList.add(115.71f);
        rangeList.add(167.14f);
        rangeList.add(218.57f);
        rangeList.add(270f);
        rangeList.add(321.43f);
        rangeList.add(12.86f);
        rangeList.add(64.29f);

        //设置文本
        mTimeList = new ArrayList<>();
        for (int i = 0; i <= 24 * 4; i++) {
            if (i == 0) {
                mTimeList.add("00:00");
                continue;
            }
            String hour = String.format("%0" + 2 + "d", 15 * i / 60);
            String minute = String.format("%0" + 2 + "d", 15 * i % 60);
            mTimeList.add(hour + ":" + minute);
        }
        mMileageList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mMileageList.add(i + ".00");
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBg(canvas);
        drawRound(canvas);
        drawCheck(canvas);

        drawPoint(canvas);
        drawType(canvas);
        mTextPaint.setColor(Color.parseColor("#6681FD"));
        mTextPaint.setTextSize(sp2px(13));
        canvas.drawText("目标", getWidth() / 2, getHeight() + dp2px(10) - dp2px(232) + dp2px(13) - mTextPaint.getFontMetrics().ascent, mTextPaint);
        //中间文本
        mTextPaint.setColor(Color.parseColor("#606060"));
        canvas.drawText(mTitleStr, getWidth() / 2, getHeight() - dp2px(20 + 48 + 16) - mTextPaint.getFontMetrics().bottom, mTextPaint);
    }

    private void drawBg(Canvas canvas) {
        Paint paint = getPaint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(dp2px(6), 0, -5, 0x30cccccc);
        canvas.drawCircle(getWidth() / 2, getHeight() + dp2px(10), dp2px(232), paint);

    }

    private void drawCheck(Canvas canvas) {
        Paint paint = getPaint();
        paint.setShader(new LinearGradient(getWidth() / 2 - dp2px(24), 0, getWidth() / 2 + dp2px(24), 0, mStartColor, mEndColor, Shader.TileMode.CLAMP));
        paint.setShadowLayer(dp2px(3), dp2px(-1), dp2px(3), 0x90666666);
        canvas.drawCircle(getWidth() / 2, getHeight() - dp2px(20 + 48 / 2), dp2px(48 / 2), paint);
        Bitmap dui = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_running_dui);
        paint = getPaint();
        canvas.drawBitmap(dui, getWidth() / 2 - dui.getWidth() / 2, getHeight() - dp2px(44) - dui.getHeight() / 2, paint);
    }

    private RectF mRectF;

    private void drawRound(Canvas canvas) {
        mRectF = new RectF(dp2px(22), dp2px(80), getWidth() - dp2px(22), dp2px(80 - 44) + getWidth() /*getHeight() + getHeight() - dp2px(80)*/);
        Paint paint = getPaint();
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setShader(new LinearGradient(mRectF.left, 0, mRectF.right, 0, mStartColor, mEndColor, Shader.TileMode.CLAMP));
        canvas.drawArc(mRectF, 200, 140, false, paint);

        GradientDrawable drawable = new GradientDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            drawable.setColors(new int[]{mStartColor, mEndColor});
            drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        } else {
            drawable.setColor(mEndColor);
        }
        Rect rect = new Rect((int) (getWidth() / 2 - dp2px(35)), (int) (getPoiXY(3)[1] - dp2px(18)), (int) (getWidth() / 2 + dp2px(35)), (int) (getPoiXY(3)[1] + dp2px(18)));
        drawable.setBounds(rect);
        drawable.setCornerRadius(dp2px(35));
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.draw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            drawable.setColors(new int[]{Color.WHITE, Color.WHITE});
            drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        } else {
            drawable.setColor(Color.WHITE);
        }
        int width = (int) dp2px(2);
        rect.left = rect.left + width;
        rect.right = rect.right - width;
        rect.top = rect.top + width;
        rect.bottom = rect.bottom - width;
        drawable.setBounds(rect);
        drawable.draw(canvas);
    }

    float degree;

    private void drawPoint(Canvas canvas) {
        mTextPaint.setTextSize(sp2px(15));
        mTextPaint.setFakeBoldText(true);
        Paint paint = getPaint();
        paint.setColor(Color.GRAY);
        canvas.save();

        canvas.rotate(degree, (mRectF.left + mRectF.right) / 2, (mRectF.top + mRectF.bottom) / 2);

        for (int i = 0; i < 7; i++) {
            String value = getValue(i);

            if (degree == 0 && i == 3) {
                mTextPaint.setColor(Color.parseColor("#02072E"));
            } else {
                mTextPaint.setColor(Color.parseColor("#9B9B9B"));
            }
            Path path = new Path();
            path.addArc(mRectF, rangeList.get(i) - 10, 20);

            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            canvas.drawTextOnPath(value, path, 0, (fontMetrics.bottom - fontMetrics.ascent) / 4, mTextPaint);
        }
        canvas.restore();
        mTextPaint.setFakeBoldText(false);
    }

    private void drawType(Canvas canvas) {
        Paint paint = getPaint();
        paint.setShader(new LinearGradient(0, 0, 0, 0, Color.GRAY, Color.GRAY, Shader.TileMode.CLAMP));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mCheckType == 0 ? R.mipmap.ic_running_time_check : R.mipmap.ic_running_time_nomal);
        canvas.drawBitmap(bitmap, getWidth() / 2 - dp2px(50) - bitmap.getWidth(), getPoiXY(3)[1] - dp2px(30) - bitmap.getHeight(), paint);
        mTextPaint.setTextSize(sp2px(12));
        mTextPaint.setColor(mCheckType == 0 ? mStartColor : Color.parseColor("#CECECE"));
        canvas.drawText("时间", getWidth() / 2 - dp2px(50) - bitmap.getWidth() / 2, getPoiXY(3)[1] - dp2px(27) - mTextPaint.getFontMetrics().ascent, mTextPaint);
        mTypeLeftXY = new int[]{(int) (getWidth() / 2 - dp2px(50) - bitmap.getWidth() / 2), (int) (getPoiXY(3)[1] - dp2px(27))};

        bitmap = BitmapFactory.decodeResource(getResources(), mCheckType == 0 ? R.mipmap.ic_running_licheng_nomal : R.mipmap.ic_running_licheng_check);
        canvas.drawBitmap(bitmap, getWidth() / 2 + dp2px(50), getPoiXY(3)[1] - dp2px(30) - bitmap.getHeight(), mPaint);
        mTextPaint.setColor(mCheckType == 0 ? Color.parseColor("#CECECE") : mStartColor);
        canvas.drawText("里程", getWidth() / 2 + dp2px(50) + bitmap.getWidth() / 2, getPoiXY(3)[1] - dp2px(27) - mTextPaint.getFontMetrics().ascent, mTextPaint);
        mTypeRightXY = new int[]{(int) (getWidth() / 2 + dp2px(50) + bitmap.getWidth() / 2), (int) (getPoiXY(3)[1] - dp2px(27))};

    }

    private String getValue(int position) {

        int posi = (position + drawableRote - 2);
        if (posi < 0 || posi >= (mCheckType == 0 ? mTimeList.size() : mMileageList.size())) {
            return "";
        }

        return mCheckType == 0 ? mTimeList.get(posi) : mMileageList.get(posi);
    }

    // 0为时间，1为里程
    public void setCheckType(int type) {
        this.drawableRote = 0;
        this.mTitleStr = type == 0 ? "小时:分钟" : "运动距离 (公里)";
        this.mCheckType = type;
        invalidate();
    }

    /**
     * 设置按钮的监听事件
     */
    public void setOnCheckedListener(OnCheckedListener checkedListener) {
        mCheckedListener = checkedListener;
    }

    private int[] getPoiXY(int position) {

        float range = rangeList.get(position);
        //左200，右340
        double x = (mRectF.left + mRectF.right) / 2 + (mRectF.right - mRectF.left) / 2 * Math.cos(range * 3.14 / 180);
        double y = (mRectF.top + mRectF.bottom) / 2 + (mRectF.bottom - mRectF.top) / 2 * Math.sin(range * 3.14 / 180);
        return new int[]{(int) x, (int) y};
    }

    private Paint getPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
        } else {
            mPaint.reset();
        }
        mPaint.setAntiAlias(true);
        return mPaint;
    }

    private int[] downXY = new int[]{0, 0};
    private int[] moveXY = new int[]{0, 0};
    private int drawableRote = 0;//图片偏移量 -1 +1 有逻辑错误，增加初始偏移量
    private float hasRote = 0;

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (animator != null && animator.isRunning()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downXY = new int[]{x, y};
                break;
            case MotionEvent.ACTION_MOVE:
                moveXY = new int[]{x, y};
                float dd = (moveXY[0] - downXY[0]) / (mRectF.right - mRectF.left) * 140;
                dd = Math.min(Math.max(dd, -51.43f), 51.43f);
                //左右滑动限制
                if (drawableRote == 0 - 1 && dd > 0 || drawableRote >= (mCheckType == 0 ? mTimeList.size() : mMileageList.size()) - 1 - 2 + 1 && dd < 0) {
                    downXY = moveXY;
                    return true;
                }
                degree = dd;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                float de = (270 - degree) % 360;

                for (int i = 0; i < rangeList.size(); i++) {
                    if (Math.abs(rangeList.get(i) - de) <= 51.43 / 2) {
                        float start = 270 - de;
                        final float end = (270 - rangeList.get(i)) % 360;
                        hasRote = Math.abs(end - start);
                        animator = ValueAnimator.ofFloat(start, end);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                degree = value;
                                invalidate();
                            }
                        });
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                degree = 0;
                                drawableRote = drawableRote - (int) (end / 51.43f > 0 ? Math.ceil(end / 51.43f) : Math.floor(end / 51.43f));
                                if (mCheckedListener != null && (hasRote > 5 || hasRote == 0 && Math.abs(downXY[0] - event.getX()) < 50 && Math.abs(downXY[1] - event.getY()) < 50)) {
                                    int position = Math.abs(drawableRote + 1) % 3;
                                    mCheckedListener.onSelect(position == 0 ? RunType.Weightlifting : position == 1 ? RunType.Run : RunType.Basketball);
                                }
                            }
                        });
                        //控制动画速度
                        animator.setDuration(Math.abs((long) ((end - start) / 51.43f * 1000)));
                        animator.start();
                    }
                }
                if (Math.abs(downXY[0] - event.getX()) < 50 && Math.abs(downXY[1] - event.getY()) < 50) {
                    //视为点击事件
                    if (hasRote <= 3) {
                        //
                        for (int i = 0; i < rangeList.size(); i++) {
                            int[] poi = getPoiXY(i);
                            if (mCheckedListener != null && Math.abs(poi[0] - event.getX()) < dp2px(33) && Math.abs(poi[1] - event.getY()) < dp2px(33)) {
                                int position = Math.abs((i - 2 + drawableRote)) % 3;
                                mCheckedListener.onCheck(position == 0 ? RunType.Weightlifting : position == 1 ? RunType.Run : RunType.Basketball);
                                break;
                            }
                        }
                        if (mCheckedListener != null) {
                            if (mCheckType == 0 && Math.abs(getWidth() / 2 - event.getX()) < dp2px(24) && Math.abs(getHeight() - dp2px(44) - event.getY()) < dp2px(24)) {
                                mCheckedListener.onCheck(RunType.Button);
                            } else if (Math.abs(getWidth() / 2 - event.getX()) < dp2px(33) && Math.abs(getHeight() - dp2px(51) - event.getY()) < dp2px(18)) {
                                //略微放大了范围
                                mCheckedListener.onCheck(RunType.Button);
                            }
                        }
                        if (Math.abs(mTypeLeftXY[0] - event.getX()) < dp2px(12) && Math.abs(mTypeLeftXY[1] - event.getY()) < dp2px(24)) {
                            if (mCheckType != 0) {
                                mCheckType = 0;
                                drawableRote = 0;
                                mTitleStr = "小时:分钟";
                            }
                        } else if (Math.abs(mTypeRightXY[0] - event.getX()) < dp2px(12) && Math.abs(mTypeRightXY[1] - event.getY()) < dp2px(24)) {
                            if (mCheckType == 0) {
                                mCheckType = 1;
                                drawableRote = 0;
                                mTitleStr = "运动距离 (公里)";
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    public enum RunType {
        Run, Basketball, Weightlifting, Button
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float minH = dp2px(228);//设置最小高度
        if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, (int) minH);
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

    public interface OnCheckedListener {
        //滑动时的监听
        void onSelect(RunType runType);

        //点击时的监听
        void onCheck(RunType runType);
    }
}
