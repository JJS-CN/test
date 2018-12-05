package com.example.admin.test.gradient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 说明：星系view
 * Created by jjs on 2018/12/3
 */

@SuppressLint("AppCompatCustomView")
public class GalaxyView extends ImageView {
    private int mCenterDrawableId;//中心图片
    private int mCenterColorForBg;//中心背景颜色
    private int mTrajectoryWidth;//轨迹线宽度
    private int mTrajectoryColors;//轨迹线颜色

    private RadialGradient mGradient;
    private Paint mPaint = null;
    private int[] mColors = new int[]{0xCCDCDDDC, 0xCCDCDDDC, 0x00000000, 0x80DCDDDC, 0xCCDCDDDC, 0xCCDCDDDC, 0x00000000, 0x80DCDDDC, 0xCCDCDDDC, 0xCCDCDDDC};
    private float[] mWidgets = new float[]{0f, 0.33F, 0.33F, 0.65f, 0.65f, 0.66f, 0.66f, 0.99F, 0.99f, 1f};

    public GalaxyView(Context context) {
        super(context);
        init();
    }

    public GalaxyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalaxyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        // mPaint.setStyle(Paint.Style.STROKE);
        //mPaint.setStrokeWidth(130);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else {
            setMeasuredDimension(Math.max(width, height), Math.max(width, height));
        }

    }

    Bitmap mBitmap;
    Canvas mCanvas;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        }
        mCanvas.drawColor(
                Color.BLACK);
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        //  canvas.drawRect(0,0,getWidth(),getHeight(),mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mGradient = new RadialGradient(getWidth() / 2, getWidth() / 2, getWidth() / 2,
                mColors, mWidgets, Shader.TileMode.REPEAT);
        mPaint.setShader(mGradient);
    }

}
