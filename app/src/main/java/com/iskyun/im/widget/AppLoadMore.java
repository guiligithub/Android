package com.iskyun.im.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.loadmore.LoadMoreStatus;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.iskyun.im.R;

public class AppLoadMore extends BaseLoadMoreView {

    @NonNull
    @Override
    public View getLoadComplete(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.view_load_more_complete);
    }

    @NonNull
    @Override
    public View getLoadEndView(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.view_load_more_end);
    }

    @NonNull
    @Override
    public View getLoadFailView(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.view_load_more_fail);
    }

    @NonNull
    @Override
    public View getLoadingView(@NonNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.view_load_more_loading);
    }

    @NonNull
    @Override
    public View getRootView(@NonNull ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_load_mroe_loading,viewGroup,false);
    }

    @Override
    public void convert(@NonNull BaseViewHolder holder, int position, @NonNull LoadMoreStatus loadMoreStatus) {
        super.convert(holder, position, loadMoreStatus);
    }
}
