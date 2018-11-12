package com.example.admin.test.baseView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;


/**
 * 说明：手环的步数返现view，对line的位置计算
 * Created by jjs on 2018/8/29.
 */

public class CashBackView extends View {
    private TextPaint mTextPaint;
    private Paint mPaint;
    private float mProgressHeight = 10;//dp
    private int mProgressBgColor = Color.parseColor("#F2F2F2");//背景
    private int mProgressStartColor = Color.parseColor("#B7B5FD");//前景渐变开始色
    private int mProgressEndColor = Color.parseColor("#81ADFF");//前景渐变结束色
    private String mPriceTop = "￥56";
    private String mAllAmount = "￥924 可返";
    private float mPaintWidth = 3;

    private int mMax = 10000;
    private int mValue = 0;
    private int mPoiLine = 5;//箭头高度
    private int mPoiPadding = 5;//箭头与进度条的间隔
    private int mWidthPadding = 22 + 4 + mPoiLine;//圆弧+直线+箭头斜线

    public CashBackView(Context context) {
        super(context);
        init();
    }

    public CashBackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CashBackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mProgressHeight = dp2px(mProgressHeight);
        mWidthPadding = (int) dp2px(mWidthPadding);//顶部空余（用于箭头位置）
        mPoiLine = (int) dp2px(mPoiLine);
        mPoiPadding = (int) dp2px(mPoiPadding);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(sp2px(12));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(2);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);

        this.setBackgroundColor(Color.GRAY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            Paint.FontMetricsInt f = mTextPaint.getFontMetricsInt();
            float h = mProgressHeight + (mPoiPadding + mPoiLine + dp2px(22)) * 2 + mPaintWidth + 5 * 2;
            setMeasuredDimension(width, (int) h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制进度条
        //背景
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mProgressBgColor);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(mProgressHeight / 2);
        int top = (int) ((getHeight() - mProgressHeight) / 2);
        int bottom = (int) (top + mProgressHeight);
        int left = getMinLeft();
        int right = getMaxRight();
        Rect rect = new Rect(left, top, right, bottom);
        gradientDrawable.setBounds(rect);
        gradientDrawable.draw(canvas);
        //前景
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            gradientDrawable.setColors(new int[]{mProgressStartColor, mProgressEndColor});
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        } else {
            gradientDrawable.setColor(mProgressStartColor);
        }
        double sc = (double) mValue / (double) mMax;
        rect.right = (int) (rect.left + (rect.right - rect.left) * sc);//计算进度条位置
        gradientDrawable.setBounds(rect);
        gradientDrawable.draw(canvas);
        //上文本
        drawTop(canvas);
        //下文本
        drawBottom(canvas);

    }

    private void drawTop(Canvas canvas) {
        double sc = (double) mValue / (double) mMax;//头部
        int poiX = (int) (getMinLeft() + (getMaxRight() - getMinLeft()) * sc);//进度条位置，箭头位置
        int poiY = (int) (((getHeight() - mProgressHeight) / 2) - mPoiPadding);
        int bottomLineY = poiY - mPoiLine;//底部线条高度
        int topLineY = (int) (bottomLineY - dp2px(22));//顶部线条高度

        int textWidth = (int) mTextPaint.measureText("已兑换" + "  " + mValue + "  " + mPriceTop);//文本宽度
        int circleWidth = (int) (textWidth + dp2px(11));//顶部框的宽度
        int poiCanMoveX = (int) (circleWidth - dp2px(33) - dp2px(4 * 2) - mPoiLine * 2);//箭头可移动距离
        int poiLeftOffset = (int) (poiCanMoveX * sc) - mPoiLine;//箭头左偏移距离
        int leftArcX = (int) (poiX - mPoiLine - poiLeftOffset - dp2px(4));//线的左边位置，也是圆弧的起始位置
        int rightArcX = (int) (leftArcX + circleWidth - dp2px(22 * 2));//右侧圆弧起始位置

        int circleHeight = (int) dp2px(22);//顶部框高度，不包括箭头（箭头高度暂定：4  宽度暂定4*2）
        Path path = new Path();
        path.moveTo(poiX, poiY);
        path.lineTo(poiX - mPoiLine, bottomLineY);//左尖角
        path.lineTo(leftArcX, bottomLineY);//左线
        RectF rectF = new RectF(leftArcX - dp2px(22), bottomLineY - dp2px(22), leftArcX, bottomLineY);
        path.arcTo(rectF, 90, 180);//左圆弧
        path.lineTo(rightArcX, topLineY);
        rectF = new RectF(rightArcX, topLineY, rightArcX + dp2px(22), topLineY + dp2px(22));
        path.arcTo(rectF, 270, 180);
        path.lineTo(poiX + mPoiLine, bottomLineY);
        path.close();
        canvas.drawPath(path, mPaint);
        int bottom = (int) mTextPaint.getFontMetrics().bottom;
        int t13height = (int) (Math.abs(mTextPaint.getFontMetrics().ascent) + bottom);//字号13sp的文字高度
        int textBaseLine = (bottomLineY + topLineY) / 2 + t13height / 2 - bottom;
        mTextPaint.setTextSize(sp2px(9));
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("已兑换  ", leftArcX - dp2px(22 / 2), textBaseLine, mTextPaint);
        int t9width = (int) mTextPaint.measureText("已兑换 ");
        mTextPaint.setTextSize(sp2px(13));
        canvas.drawText(mValue + "  " + mPriceTop, leftArcX - dp2px(22 / 2) + t9width, textBaseLine, mTextPaint);
    }

    private void drawBottom(Canvas canvas) {
        int progressBottom = getHeight() / 2;
        mTextPaint.setTextSize(sp2px(12));
        canvas.drawText(mAllAmount, getMinLeft(), progressBottom + mPoiPadding + Math.abs(mTextPaint.getFontMetrics().ascent), mTextPaint);

        int lineTopY = progressBottom + mPoiPadding + mPoiLine;
        int lineBottomY = (int) (lineTopY + dp2px(22));
        int rightArcX = (int) (getMaxRight() + mPoiLine);

        int textWidth = (int) mTextPaint.measureText("总步数 " + mMax);
        int leftArcX = (int) (rightArcX - textWidth + dp2px(22));

        Path path = new Path();
        path.moveTo(getMaxRight(), progressBottom + mPoiPadding);
        path.lineTo(getMaxRight() + mPoiLine, lineTopY);
        RectF rectF = new RectF(rightArcX, lineTopY, rightArcX + dp2px(22), lineBottomY);
        path.arcTo(rectF, 270, 180);
        path.lineTo(leftArcX, lineBottomY);
        RectF rectF2 = new RectF(leftArcX - dp2px(22), lineTopY, leftArcX, lineBottomY);
        path.arcTo(rectF2, 90, 180);//左圆弧
        path.lineTo(getMaxRight() - mPoiLine, lineTopY);
        path.close();
        canvas.drawPath(path, mPaint);
        int textBottom = (int) mTextPaint.getFontMetrics().bottom;
        mTextPaint.setTextSize(sp2px(13));
        int t13height = (int) (Math.abs(mTextPaint.getFontMetrics().ascent) + textBottom);//字号13sp的文字高度
        mTextPaint.setTextSize(sp2px(9));
        int t9Width = (int) mTextPaint.measureText("总步数  ");//字号9sp的文字高度
        int textBaseLine = (lineBottomY + lineTopY) / 2 + t13height / 2 - textBottom;
        int textStart = (int) (leftArcX - dp2px(22 / 2));
        canvas.drawText("总步数  ", textStart, textBaseLine, mTextPaint);
        mTextPaint.setTextSize(sp2px(13));
        canvas.drawText("" + mMax, textStart + t9Width, textBaseLine, mTextPaint);
    }

    //设置当前值
    public void setValue(int value) {
        mValue = value > mMax ? mMax : value < 0 ? 0 : value;
        invalidate();
    }

    public int getValue() {
        return mValue;
    }

    //设置最大值
    public void setMax(int max) {
        mMax = max;
        invalidate();
    }

    //设置顶部金额
    public void setPriceTop(String priceTop) {
        mPriceTop = priceTop;
        invalidate();
    }

    //设置底部总价格
    public void setAllAmount(String allAmount) {
        mAllAmount = allAmount;
        invalidate();
    }

    private int getMinLeft() {
        return getPaddingLeft() + mWidthPadding + 2;//2为线条位置
    }

    private int getMaxRight() {
        return getWidth() - getPaddingRight() - mWidthPadding - 2;
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
