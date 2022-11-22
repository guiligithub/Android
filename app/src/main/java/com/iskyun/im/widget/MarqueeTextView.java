package com.iskyun.im.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.iskyun.im.R;

/**
 * 自定义跑马灯
 */

public class MarqueeTextView extends View {
    /**
     * 界面刷新时间(ms)
     */
    public static final int INVALIDATE_TIME = 12;
    /**
     * 每次移动的像素点(px)
     */
    public static final int INVALIDATE_STEP = 2;

    private String drawingText;
    private TextPaint paint;
    public boolean exitFlag;
    private float textWidth;
    private int posX = 0;
    private float posY;
    private int width;
    private RectF rf;
    private boolean hasInit;

    private final Handler mHandler = new Handler();

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        paint = new TextPaint();
        rf = new RectF(0, 0, 0, 0);
        paint.setAntiAlias(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        setTextSize(typedArray.getDimension(R.styleable.MarqueeTextView_mtvTextSize, 12f));
        setTextColor(typedArray.getColor(R.styleable.MarqueeTextView_mtvTextColor, Color.WHITE));
        setText(typedArray.getString(R.styleable.MarqueeTextView_mtvText));
    }

    public void setText(String text) {
        this.drawingText = text;
        posX = 0;
    }

    public void setTextSize(float dp) {
        if (this.paint == null)
            return;
        this.paint.setTextSize(dp);
    }

    public void setTextColor(int color) {
        if (this.paint == null)
            return;
        this.paint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        if (drawingText != null) {
            textWidth = paint.measureText(drawingText, 0, drawingText.length());
        }
        if (posX == 0 && !hasInit) {
            rf.right = 0;
            rf.bottom = MeasureSpec.getSize(heightMeasureSpec);
            posX = 0;
            posY = getTextDrawingBaseline(paint, rf);
            hasInit = true;
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (getVisibility() != View.VISIBLE || TextUtils.isEmpty(drawingText)) {
            return;
        }
        canvas.save();
        canvas.drawText(drawingText, 0, drawingText.length(), posX, posY, paint);
        canvas.restore();
    }

    private final Runnable moveRun = new Runnable() {

        @Override
        public void run() {
            //控制文本宽度大于控件宽度才进行滚动
            if (width >= textWidth) {
                posX = 0;
                invalidate();
                return;
            }
            //左移
            posX -= INVALIDATE_STEP;
            //当文字和空格完全移出屏幕，x值从1开始移动
            if (posX < -1 * textWidth - paint.measureText("", 0, "".length())) {
                posX = getWidth();
            }
            invalidate();
            if (!exitFlag) {
                mHandler.postDelayed(this, INVALIDATE_TIME);
                return;
            }
            posX = 0;

        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMove();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            startMove();
        } else {
            stopMove();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            startMove();
        } else {
            stopMove();
        }
    }


    private void stopMove() {
        exitFlag = true;
        if (mHandler == null)
            return;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void startMove() {
        exitFlag = false;
        if (mHandler == null)
            return;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(moveRun, 0);
    }

    public void startMove(long delay) {
        exitFlag = false;
        if (mHandler == null)
            return;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(moveRun, delay);
    }

    /**
     * 获取绘制文字的baseline
     *
     * @param paint
     * @param targetRect
     * @return
     */
    public static float getTextDrawingBaseline(Paint paint, RectF targetRect) {
        if (paint == null || targetRect == null) {
            return 0;
        }
        Paint.FontMetrics fontMetric = paint.getFontMetrics();
        return targetRect.top + (targetRect.height() - fontMetric.bottom + fontMetric.top) / 2.0f - fontMetric.top;
    }
}