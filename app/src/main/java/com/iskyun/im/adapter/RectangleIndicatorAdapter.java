package com.iskyun.im.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.R;

public class RectangleIndicatorAdapter extends RecyclerView.Adapter<RectangleIndicatorAdapter.ViewHolder> {
    private int page_count;
    private int selectedPosition;

    @NonNull
    @Override
    public RectangleIndicatorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_rectangle_indicator, parent, false);
        return new RectangleIndicatorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RectangleIndicatorAdapter.ViewHolder holder, int position) {
        holder.indicator.setChecked(selectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return (page_count == 1) ? 0 : page_count;
    }

    public void setPageCount(int pageCount) {
        this.page_count = pageCount;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox indicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            indicator = (CheckBox) itemView;
        }
    }
}

