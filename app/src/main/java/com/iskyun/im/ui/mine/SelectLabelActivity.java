package com.iskyun.im.ui.mine;

import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.adapter.CursorTagsAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivitySelectLabelBinding;
import com.iskyun.im.ui.mine.viewmodel.SelectLabelModel;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.widget.SkyTextView;
import com.moxun.tagcloudlib.view.TagCloudView;

import java.util.ArrayList;
import java.util.List;

public class SelectLabelActivity extends BaseActivity<ActivitySelectLabelBinding, SelectLabelModel> implements TagCloudView.OnTagClickListener {
    private final List<String> listClick = new ArrayList<>();//点击过的标签云的数据的集合
    private final List<String> labelFirst = new ArrayList<>();//
    private List<CursorTagsAdapter.TextTag> tags;

    @Override
    public SelectLabelModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivitySelectLabelBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivitySelectLabelBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        //设置标签云的点击事件
        mViewBinding.tabcloudView.setOnTagClickListener(this);
        //给标签云设置适配器
        tags = getTags();
        CursorTagsAdapter adapter = new CursorTagsAdapter(tags);
        mViewBinding.tabcloudView.setAdapter(adapter);

        getLabelOnes();


        mViewBinding.btnPost.setOnClickListener(view -> {
            if (!listClick.isEmpty()) {
                labelFirst.addAll(listClick);
                Intent intent = getIntent();
                intent.putExtra("tags", labelFirst.toString());
                setResult(0, intent);
                SelectLabelActivity.this.finish();
            } else {
                ToastUtils.showToast("请选择标签");
            }
        });
    }


    @Override
    public void onItemClick(ViewGroup parent, View view, int position) {
        SkyTextView tv = (SkyTextView) view;
        LogUtils.e(position+"");
        if(!tv.isSelected()){
            if(listClick.size() >= 3){
                ToastUtils.showToast("只能选择四个哦");
            }else {
                listClick.add(tags.get(position).content);
                tv.setSelected(!tv.isSelected());//设置标签的选择状态
                if (!tv.isSelected()) {
                    tv.setBackgroundShapeColor(tags.get(position).colorId);
                } else {
                    tv.setBackgroundShapeColor(getResources().getColor(R.color.colorSubContent));
                }
            }
        }else {
            listClick.remove(tags.get(position).content);
            tv.setSelected(false);
            tv.setBackgroundShapeColor(tags.get(position).colorId);
        }
    }

    private void getLabelOnes() {
        String[] labelOne = getResources().getStringArray(R.array.label_one);
        List<Integer> colorResIds = new ArrayList<>();
        colorResIds.add(Color.parseColor("#ff9d70"));
        colorResIds.add(Color.parseColor("#fe71fb"));
        colorResIds.add(Color.parseColor("#fe6c9d"));
        colorResIds.add(Color.parseColor("#97a1ff"));
        colorResIds.add(Color.parseColor("#35d899"));

        mViewBinding.rg.removeAllViews();
        if (labelOne.length <= colorResIds.size()) {
            for (int i = 0; i < labelOne.length; i++) {
                SkyTextView stv = createRadioButton(colorResIds.get(i), labelOne[i]);
                stv.setTag(colorResIds.get(i));
                stv.setOnClickListener(this::onTagClick);
                mViewBinding.rg.addView(stv);
            }
            labelFirst.add(labelOne[0]);
            SkyTextView stv = (SkyTextView) mViewBinding.rg.getChildAt(0);
            stv.setSelected(true);
            stv.setTextColor(getResources().getColor(R.color.white));
            stv.setBackgroundShapeColor(colorResIds.get(0));
        }

    }

    private void onTagClick(View view) {
        if (!view.isSelected()) {
            view.setSelected(!view.isSelected());
            labelFirst.clear();
            for (int i = 0; i < mViewBinding.rg.getChildCount(); i++) {
                View indexView = mViewBinding.rg.getChildAt(i);
                SkyTextView tv = (SkyTextView) indexView;
                if (!view.getTag().equals(indexView.getTag())) {
                    tv.setSelected(false);
                    tv.setTextColor(getResources().getColor(R.color.colorContent));
                    tv.setBackgroundShapeColor(getResources().getColor(R.color.gray));
                }
            }
            SkyTextView stv = (SkyTextView) view;
            labelFirst.add(stv.getText().toString());
            stv.setTextColor(getResources().getColor(R.color.white));
            stv.setBackgroundShapeColor((Integer) view.getTag());
        }
    }


    private SkyTextView createRadioButton(@ColorInt int color, String content) {
        SkyTextView stv = new SkyTextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, DisplayUtils.getDimensionPixelSize(R.dimen.tag_height));
        params.gravity = Gravity.CENTER;
        int padding = (int) DisplayUtils.getDimension(R.dimen.content_margin);
        int margin = DisplayUtils.getDimensionPixelSize(R.dimen.text_margin);
        params.setMargins(margin, 0, 0, 0);
        stv.setLayoutParams(params);
        stv.setPadding(padding, margin, padding, margin);
        stv.setText(content);
        stv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                DisplayUtils.getDimensionPixelSize(R.dimen.sub_title_size));
        stv.setTextColor(getResources().getColor(R.color.colorContent));
        stv.showBackgroundShape(true);
        stv.setBackgroundShape(SkyTextView.FILL_BACKGROUND_SHAPE);
        stv.setBackgroundRadius(R.dimen.text_margin);
        stv.setBackgroundStrokeWidth(R.dimen.tag_divider);
        stv.setBackgroundShapeColor(getResources().getColor(R.color.gray));
        stv.setSelected(false);
        return stv;
    }

    private List<CursorTagsAdapter.TextTag> getTags() {
        List<CursorTagsAdapter.TextTag> tags = new ArrayList<>();

        int color1= Color.parseColor("#ff9d70");
        int color2= Color.parseColor("#fe71fb");
        int color3= Color.parseColor("#fe6c9d");
        int color4= Color.parseColor("#97a1ff");
        int color5= Color.parseColor("#35d899");
        int color6= Color.parseColor("#f0c759");
        int color7= Color.parseColor("#e1df44");
        int color8= Color.parseColor("#c617e6");

        tags.add(new CursorTagsAdapter.TextTag("高冷", color1));
        tags.add(new CursorTagsAdapter.TextTag("善良", color2));
        tags.add(new CursorTagsAdapter.TextTag("大家闺秀", color3));
        tags.add(new CursorTagsAdapter.TextTag("傻白甜", color4));
        tags.add(new CursorTagsAdapter.TextTag("爱浪漫", color5));
        tags.add(new CursorTagsAdapter.TextTag("温柔", color6));
        tags.add(new CursorTagsAdapter.TextTag("语音通话", color7));
        tags.add(new CursorTagsAdapter.TextTag("文字聊天", color8));

        tags.add(new CursorTagsAdapter.TextTag("女汉子", color8));
        tags.add(new CursorTagsAdapter.TextTag("中二", color7));
        tags.add(new CursorTagsAdapter.TextTag("直爽", color6));
        tags.add(new CursorTagsAdapter.TextTag("逗逼", color5));
        tags.add(new CursorTagsAdapter.TextTag("大龄剩女",color4));
        tags.add(new CursorTagsAdapter.TextTag("见面聊", color3));
        tags.add(new CursorTagsAdapter.TextTag("幽默", color2));
        tags.add(new CursorTagsAdapter.TextTag("颜值高", color1));
        return tags;
    }

    @Override
    public int getTitleText() {
        return R.string.select_label;
    }

}
