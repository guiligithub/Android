package com.iskyun.im.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Dynamic;
import com.iskyun.im.data.req.FileUploadType;
import com.iskyun.im.ui.common.PreviewPicturesActivity;
import com.iskyun.im.ui.common.VideoPlayActivity;
import com.iskyun.im.ui.common.frag.VideoPlayFragment;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.glide.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.MainVH> {

    private List<Dynamic.FileType> mPictures;
    private int mFileType;

    public NearbyAdapter() {}

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Dynamic.FileType> pictures, int fileType){
        this.mPictures = pictures;
        this.mFileType = fileType;
        if(pictures == null){
            this.mPictures = new ArrayList<>();
        }
        //notifyItemRangeChanged(0, mPictures.size());//size 没有更新
        notifyDataSetChanged();

    }


    @Override
    public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_nearby, parent, false));
    }


    @Override
    public void onBindViewHolder(MainVH holder, int position) {
        ImageLoader.get().load(holder.imageView, mPictures.get(position).getFileUrl());
        if(mFileType == FileUploadType.IMAGE.getUploadType()){
            holder.pauseImage.setVisibility(View.GONE);
        }else {
            holder.pauseImage.setVisibility(View.VISIBLE);
        }
        holder.imageView.setTag(position);
        holder.imageView.setOnClickListener(view -> {
            int mPosition = (int) view.getTag();
            if(mFileType == FileUploadType.VIDEO.getUploadType()){
                Bundle args = new Bundle();
                args.putString(VideoPlayFragment.PATH,mPictures.get(mPosition).getFileUrl());
                ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), args, VideoPlayActivity.class);
            }else {
                ArrayList<String> urls = new ArrayList<>();
                for (Dynamic.FileType type: mPictures){
                    urls.add(type.getFileUrl());
                }
                PreviewPicturesActivity.start(ImLiveApp.get().getTopActivity(), 0, position, urls);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class MainVH extends RecyclerView.ViewHolder {
        ImageView imageView,pauseImage;

        MainVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo);
            pauseImage = itemView.findViewById(R.id.photo_pause);
        }
    }
}

