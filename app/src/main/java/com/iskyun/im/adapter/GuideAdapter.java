package com.iskyun.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Guide;
import com.iskyun.im.ui.MainActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.glide.ImageLoader;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class GuideAdapter extends BannerAdapter<Guide, GuideAdapter.ImageBannerHolder> {
    Context context;

    /**
     * 自定义构造方法
     * @param context 上下文对象
     * @param datas 传入数据
     */
    public GuideAdapter(Context context, List<Guide> datas) {
        super(datas);
        this.context = context;
    }


    @Override
    public GuideAdapter.ImageBannerHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new GuideAdapter.ImageBannerHolder(LayoutInflater.from(context).inflate(R.layout.view_item_guide, parent, false));
    }

    @Override
    public void onBindView(GuideAdapter.ImageBannerHolder holder, Guide data, int position, int size) {
        ImageLoader.get().load(holder.imageView, data.getImgPath());
        holder.textView.setText(data.getImgName());

        if (position == mDatas.size()-1){
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setOnClickListener(view -> ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), MainActivity.class));
        }
    }

    public class ImageBannerHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        Button button;
        public ImageBannerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.introducttory_iv);
            textView=itemView.findViewById(R.id.introducttory_tv);
            button=itemView.findViewById(R.id.introducttory_btn);
        }
    }

}