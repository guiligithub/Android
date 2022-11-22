package com.iskyun.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.R;
import com.iskyun.im.data.bean.Banner;
import com.iskyun.im.utils.glide.ImageLoader;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class MineBannerAdapter extends BannerAdapter<Banner, MineBannerAdapter.MineBannerHolder> {
    Context context;
    /**
     * 自定义构造方法
     * @param context 上下文对象
     * @param banners 传入数据
     */
    public MineBannerAdapter(Context context, List<Banner> banners) {
        super(banners);
        this.context = context;
    }


    @Override
    public MineBannerHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new MineBannerHolder(LayoutInflater.from(context).inflate(R.layout.view_item_banner_tab, parent, false));
    }


    @Override
    public void onBindView(MineBannerHolder holder, Banner data, int position, int size) {
        ImageLoader.get().load(holder.imageView, data.getBannerImg());
    }

    public static class MineBannerHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MineBannerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_banner);
        }
    }

}

