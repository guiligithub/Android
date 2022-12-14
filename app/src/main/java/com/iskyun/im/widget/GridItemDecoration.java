package com.iskyun.im.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    private int spanCount = -1;
    private int width = 5;
    private Paint paint;

    public GridItemDecoration(Context context) {
        init(context, width, android.R.color.black);
    }

    public GridItemDecoration(Context context, int width) {
        init(context, width, android.R.color.black);
    }

    public GridItemDecoration(Context context, int width, int color) {
        init(context, width, color);
    }

    private void init(Context context, int width, int color) {
        this.width = width;
        paint = new Paint();
        paint.setColor(context.getResources().getColor(color));
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int childCount = parent.getAdapter().getItemCount();
        int spanCount = getSpanCount(parent);
        int drawRight = width;
        int drawBottom = width;
        int drawLeft = width;
        int drawTop = width;
        if (isLastRaw(parent, position, spanCount, childCount)) {
            drawBottom = 0;
        }
        if (isFirstRaw(parent, position, spanCount, childCount)) {
            drawTop = 0;
        }
        if (isLastColumn(parent, position, spanCount, childCount)) {
            drawRight = 0;
        }
        if (isFirstColumn(parent, position, spanCount, childCount)) {
            drawLeft = 0;
        }
//        if (isLastItem(parent, position, childCount)) {
//            drawRight = 0;
//        }
        //LogUtils.e("position-->"+position+"drawLeft-->"+drawLeft+",drawTop-->"+drawTop+",drawRight-->"+drawRight+",drawBottom-->"+drawBottom);
        outRect.set(drawLeft, drawTop, drawRight, drawBottom);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        spanCount = getSpanCount(parent);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            //int bottom = top + mDivider.getIntrinsicHeight();
            int bottom = top + width * 2;
            if (isLastRaw(parent, i, spanCount, childCount)) {
                bottom = top;
            }
            if (!isLastColumn(parent, i, spanCount, childCount)) {
                right = child.getRight() + params.rightMargin + width * 2;
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
            c.drawRect(left, top, right, bottom, paint);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        spanCount = getSpanCount(parent);

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            //            int right = left + mDivider.getIntrinsicWidth();
            int right = left + width * 2;
            if (isLastColumn(parent, i, spanCount, childCount)) {
                // ????????????????????????????????????????????????
                right = left;
            }
            if (isLastItem(parent, i, childCount)) {
                right = left;
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
            c.drawRect(left, top, right, bottom, paint);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        // ??????
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (childCount % spanCount == 0) {
                childCount = childCount - spanCount;
            } else {
                childCount = childCount - childCount % spanCount;
            }
            // ????????????????????????????????????????????????
            return pos >= childCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager ???????????????
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // ????????????????????????????????????????????????
                return pos >= childCount;
            } else {
                // ????????????????????????????????????????????????
                return (pos + 1) % spanCount == 0;
            }
        }
        return false;
    }

    private boolean isFirstRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // ?????????????????????????????????????????????
            return pos < spanCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager ???????????????
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // ?????????????????????????????????????????????
                return pos < spanCount;
            } else {
                // ?????????????????????????????????????????????
                return (pos + 1) % spanCount == 1;
            }
        }
        return false;
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {// ????????????????????????????????????????????????
                return true;
            }
            // ????????????????????????????????????????????????
            return (pos + 1) % spanCount == 1 && childCount == 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // ????????????????????????????????????????????????
                return (pos + 1) % spanCount == 0;
            } else {
                childCount = childCount - childCount % spanCount;
                // ????????????????????????????????????????????????
                return pos >= childCount;
            }
        }
        return false;
    }

    private boolean isFirstColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return (pos + 1) % spanCount == 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (pos + 1) % spanCount == 1;
            } else {
                return pos < spanCount;
            }
        }
        return false;
    }

    private boolean isLastItem(RecyclerView parent, int pos, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return (pos + 1) == childCount;
        }
        return false;
    }
}
