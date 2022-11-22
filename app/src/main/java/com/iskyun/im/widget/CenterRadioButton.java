package com.iskyun.im.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class CenterRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {

    public CenterRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable[] drawable = getCompoundDrawables();
        if(drawable != null){
            Drawable leftDrawable = drawable[0];
            if(leftDrawable != null){
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = leftDrawable.getIntrinsicWidth();
                float bodyWidth = textWidth+drawablePadding+drawableWidth;
                canvas.translate((getWidth()-bodyWidth)/2,0);
            }
        }
        super.onDraw(canvas);
    }
}
