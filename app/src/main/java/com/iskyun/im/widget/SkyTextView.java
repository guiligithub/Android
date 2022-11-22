package com.iskyun.im.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iskyun.im.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *图片，文字居中
 * 带有背景shape的  textview
 */
public class SkyTextView extends androidx.appcompat.widget.AppCompatTextView {

    public static final int LINE_BACKGROUND_SHAPE = 0;
    public static final int FILL_BACKGROUND_SHAPE = 1;

    private Paint mPaint;
    private RectF rectF;
    private int backgroundShapeColor;     //周围一圈线颜色
    private int backgroundStrokeWidth;          //周围线宽度 单位转换成px
    private int backgroundRadius;             //圆角大小 单位转换成px
    private int backgroundShape;         //图形样式 0-画线  !=0 填充整个view
    private boolean showBackgroundShape;         //是否画背景

    @IntDef({LINE_BACKGROUND_SHAPE, FILL_BACKGROUND_SHAPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BackgroundShape {}

    public SkyTextView(@NonNull Context context) {
        this(context, null);
    }

    public SkyTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkyTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SkyTextView);

            showBackgroundShape = typedArray.getBoolean(R.styleable.SkyTextView_showBackgroundShape, false);
            backgroundShapeColor = typedArray.getColor(R.styleable.SkyTextView_backgroundShapeColor, Color.GRAY);
            backgroundStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.SkyTextView_backgroundStrokeWidth, 1);
            backgroundRadius = typedArray.getDimensionPixelOffset(R.styleable.SkyTextView_backgroundRadius, 0);
            backgroundShape = typedArray.getInt(R.styleable.SkyTextView_backgroundShape, LINE_BACKGROUND_SHAPE);

            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        if (backgroundShape == LINE_BACKGROUND_SHAPE) {
            mPaint.setStyle(Paint.Style.STROKE);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
        }
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setColor(backgroundShapeColor);
        mPaint.setStrokeWidth(backgroundStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLineRect(canvas);
        super.onDraw(canvas);
        setImageCenter();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int minWidth = Math.min(w, h);
        /**
         * lineWidth
         * 0 < lineWidth < (minWidth)/2
         * 在这个中间才能画出背景
         */
        if (backgroundStrokeWidth >= (minWidth >> 1)) {
            backgroundStrokeWidth = (minWidth >> 1);
            mPaint.setStrokeWidth(backgroundStrokeWidth);
            //此时要画的矩形短边近似为lineWidth
            if (backgroundRadius > (backgroundStrokeWidth >> 1)) {
                backgroundRadius = (backgroundStrokeWidth >> 1);
            }
        } else {

            /**
             *  画笔宽度没有超限 检查圆角是否超过当前允许的最大值
             */
            if (backgroundRadius > ((minWidth - backgroundStrokeWidth) >> 1)) {
                backgroundRadius = ((minWidth - backgroundStrokeWidth) >> 1);
            }
        }

        if (backgroundRadius <= 0) {
            backgroundRadius = 0;
        }
        //画线的矩形
        if (backgroundShape == 0) {
            rectF = new RectF(backgroundStrokeWidth >> 1, backgroundStrokeWidth >> 1,
                    w - (backgroundStrokeWidth >> 1), h - (backgroundStrokeWidth >> 1));
        } else {
            rectF = new RectF(0, 0, w, h);
        }
    }

    /**
     * 画外圈的线
     *
     * @param canvas .
     */
    private void drawLineRect(Canvas canvas) {
        //画形
        if (showBackgroundShape) {
            setBackgroundResource(0);
            if (backgroundRadius == 0) {
                canvas.drawRoundRect(rectF, 0, 0, mPaint);
                /**
                 * 四个角的点
                 */
                canvas.drawPoint(rectF.left, rectF.bottom, mPaint);
                canvas.drawPoint(rectF.left, rectF.top, mPaint);
                canvas.drawPoint(rectF.right, rectF.top, mPaint);
                canvas.drawPoint(rectF.right, rectF.bottom, mPaint);
            } else {
                //画圆角矩形
                canvas.drawRoundRect(rectF,
                        backgroundRadius + (backgroundStrokeWidth >> 1),
                        backgroundRadius + (backgroundStrokeWidth >> 1),
                        mPaint);
            }
        }
    }

    private int dp2Px(float dpValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dpValue * density);
    }

    /**
     * 设置图片居中，解决  match_parent 或固定宽度图片，文字不居中的问题
     */
    private void setImageCenter() {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables == null)
            return;

        float textWidth = getPaint().measureText(getText().toString());
        int drawableLeftWidth = 0;
        int drawableRightWidth = 0;
        if (drawables[0] != null) {
            drawableLeftWidth = drawables[0].getIntrinsicWidth();
        }
        if (drawables[2] != null) {
            drawableRightWidth = drawables[2].getIntrinsicWidth();
        }
        int drawableWidth = drawableLeftWidth + drawableRightWidth;
        float bodyWidth = textWidth + drawableWidth + getCompoundDrawablePadding();
        int dx = (int) ((getWidth() - bodyWidth) * 0.5f);
        setPaddingRelative(dx, 0, dx, 0);
        setGravity(Gravity.CENTER);
    }

    /**
     * 是否显示自定义背景
     *
     * @param showBackgroundShape true 显示，false 不显示
     */
    public void showBackgroundShape(boolean showBackgroundShape) {
        this.showBackgroundShape = showBackgroundShape;
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
    }


    /**
     * 设置背景形状颜色
     *
     * @param resId resId
     */
    public void setBackgroundShapeColor(@ColorInt int resId) {
        this.backgroundShapeColor = resId;
        mPaint.setColor(backgroundShapeColor);
        invalidate();
    }

    /**
     * 设置背景形状
     *
     * @param backgroundShape LINE_BACKGROUND_SHAPE ,FILL_BACKGROUND_SHAPE
     */
    public void setBackgroundShape(@BackgroundShape int backgroundShape) {
        this.backgroundShape = backgroundShape;
        if (backgroundShape == LINE_BACKGROUND_SHAPE) {
            mPaint.setStyle(Paint.Style.STROKE);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
        }
        invalidate();
    }

    /**
     * 设置背景圆角大小
     *
     * @param resId radius
     */
    public void setBackgroundRadius(@DimenRes int resId) {
        this.backgroundRadius = getResources().getDimensionPixelOffset(resId);
        invalidate();
    }

    /**
     * 设置背景边框宽度
     *
     * @param resId strokeWidth
     */
    public void setBackgroundStrokeWidth(@DimenRes int resId) {
        this.backgroundStrokeWidth = getResources().getDimensionPixelOffset(resId);
        mPaint.setStrokeWidth(backgroundStrokeWidth);
        invalidate();
    }

}
