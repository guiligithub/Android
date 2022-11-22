package com.iskyun.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.R;
import com.iskyun.im.data.bean.ImageBannerBean;
import com.iskyun.im.utils.glide.ImageLoader;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ImageBannerAdapter extends BannerAdapter<ImageBannerBean, ImageBannerAdapter.ImageBannerHolder> {
    Context context;

    /**
     * 自定义构造方法
     * @param context 上下文对象
     * @param datas 传入数据
     */
    public ImageBannerAdapter(Context context, List datas) {
        super(datas);
        this.context = context;
    }


    @Override
    public ImageBannerHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ImageBannerHolder(LayoutInflater.from(context).inflate(R.layout.view_item_image_banner_tab, parent, false));
    }

    @Override
    public void onBindView(ImageBannerHolder holder, ImageBannerBean data, int position, int size) {
        ImageLoader.get().load(holder.imageView, data.getPicture());
        //设置监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context,"第"+(position+1)+"张",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ImageBannerHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageBannerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_banner);
        }
    }

}

