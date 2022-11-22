package com.hyphenate.easeui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;

public class CenterImageSpan extends DynamicDrawableSpan {

    private Drawable mDrawable;
    private Uri mContentUri;
    private int mResourceId;
    @Nullable
    private Context mContext;

    public CenterImageSpan(@NonNull Context context, @NonNull Bitmap bitmap) {
        super();
        mContext = context;
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*2/3,
                bitmap.getHeight()*2/3, true);
        mDrawable = context != null ? new BitmapDrawable(context.getResources(),scaleBitmap)
                : new BitmapDrawable(scaleBitmap);
        int width = mDrawable.getIntrinsicWidth();
        int height = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, Math.max(width, 0), Math.max(height, 0));
    }

    public CenterImageSpan(@NonNull Context context, @NonNull int resourceId) {
        this(context, BitmapFactory.decodeResource(context.getResources(), resourceId));
        mResourceId = resourceId;
    }

    public CenterImageSpan(@NonNull Context context, @NonNull Uri uri) {
        super();
        mContext = context;
        mContentUri = uri;
    }



    @Override
    public Drawable getDrawable() {
        Drawable drawable = null;

        if (mDrawable != null) {
            drawable = mDrawable;
        } else if (mContentUri != null) {
            Bitmap bitmap = null;
            try {
                InputStream is = mContext.getContentResolver().openInputStream(
                        mContentUri);
                bitmap = BitmapFactory.decodeStream(is);
                drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                is.close();
            } catch (Exception e) {
                Log.e("ImageSpan", "Failed to loaded content " + mContentUri, e);
            }
        } else {
            try {
                drawable = mContext.getDrawable(mResourceId);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
            } catch (Exception e) {
                Log.e("ImageSpan", "Unable to find resource: " + mResourceId);
            }
        }

        return drawable;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();

        float drawableHeight = rect.height();
        Paint.FontMetrics paintFm = paint.getFontMetrics();
        float textHeight = paintFm.descent - paintFm.ascent;

        if (fm != null) {
            float textCenter = (paintFm.descent + paintFm.ascent) / 2;
            fm.ascent = fm.top = (int) (textCenter - drawableHeight / 2);
            fm.descent = fm.bottom = (int) (drawableHeight + fm.ascent);
        }

        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();

        Paint.FontMetrics fm = paint.getFontMetrics();
        float drawableHeight = b.getBounds().height();
        float textHeight = fm.descent - fm.ascent;
        float transY = y + (fm.descent + fm.ascent) / 2 - drawableHeight / 2;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
