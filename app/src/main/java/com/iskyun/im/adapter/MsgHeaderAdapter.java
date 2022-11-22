package com.iskyun.im.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.databinding.ViewMsgHeaderBinding;

import java.util.List;

public class MsgHeaderAdapter extends BaseQuickAdapter<MsgHeaderAdapter.MsgHeaderItem, BaseDataBindingHolder<ViewMsgHeaderBinding>> {


    public MsgHeaderAdapter(int layoutResId, @Nullable List<MsgHeaderItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewMsgHeaderBinding> holderBinding, MsgHeaderItem msgHeaderItem) {
        ViewMsgHeaderBinding binding = holderBinding.getDataBinding();
        if(binding != null){
            binding.msgHeaderIv.setImageResource(msgHeaderItem.getResId());
            binding.msgHeaderTitle.setText(msgHeaderItem.getTitle());
            if(!TextUtils.isEmpty(msgHeaderItem.getContent())){
                binding.msgHeaderContent.setVisibility(View.VISIBLE);
                binding.msgHeaderContent.setText(msgHeaderItem.getContent());
            }else {
                binding.msgHeaderContent.setVisibility(View.GONE);
            }
        }
    }


    public static class MsgHeaderItem {

        private int type;
        private int resId;
        private String title;
        private String content;

        public MsgHeaderItem(int type, @DrawableRes int resId, String title, String content) {
            this.type = type;
            this.resId = resId;
            this.title = title;
            this.content = content;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
