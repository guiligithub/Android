package com.iskyun.im.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.iskyun.im.R;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.widget.SkyTextView;
import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.List;

public class CursorTagsAdapter extends TagsAdapter {
    private List<TextTag> mList;

    public CursorTagsAdapter(List<TextTag> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        SkyTextView tv = new SkyTextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, DisplayUtils.dp2px(24));
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);

        int padding = (int) DisplayUtils.getDimension(R.dimen.content_margin);
        tv.setPadding(padding, 0, 0, padding);
        tv.showBackgroundShape(true);
        tv.setBackgroundRadius(R.dimen.text_margin);
        tv.setBackgroundShape(SkyTextView.FILL_BACKGROUND_SHAPE);
        tv.setBackgroundShapeColor(mList.get(position).colorId);//#fff6f7
        tv.setBackgroundStrokeWidth(R.dimen.tag_divider);

        tv.setGravity(Gravity.CENTER);//文字的位置
        //tv.setTextColor(mList.get(position).colorId);
        tv.setTextColor(Color.WHITE);
        tv.setText(getItem(position).content);
        tv.setSelected(false);
        return tv;
    }

    @Override
    public TextTag getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return 1;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }

    public static class TextTag {
        public int colorId;
        public String content;

        public TextTag(String content, int colorId) {
            this.content = content;
            this.colorId = colorId;
        }
    }
}
