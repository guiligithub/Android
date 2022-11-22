package com.iskyun.im.adapter;

import android.content.Context;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.iskyun.im.R;
import com.iskyun.im.utils.glide.ImageLoader;

import java.util.ArrayList;

public class PreviewPhotosAdapter extends RecyclerView.Adapter<PreviewPhotosAdapter.PreviewPhotosViewHolder> {
    private ArrayList<String> photos;
    private PreviewPhotosAdapter.OnClickListener listener;
    private LayoutInflater inflater;

    public interface OnClickListener {
        void onPhotoClick();

        void onPhotoScaleChanged();
    }

    public PreviewPhotosAdapter(Context cxt, ArrayList<String> photos, PreviewPhotosAdapter.OnClickListener listener) {
        this.photos = photos;
        this.inflater = LayoutInflater.from(cxt);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PreviewPhotosAdapter.PreviewPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreviewPhotosAdapter.PreviewPhotosViewHolder(inflater.inflate(R.layout.view_item_preview_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PreviewPhotosAdapter.PreviewPhotosViewHolder holder, int position) {

        holder.ivLongPhoto.setVisibility(View.GONE);
        ImageLoader.get().load(holder.ivPhotoView, photos.get(position));


        holder.ivLongPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick();
            }
        });
        holder.ivPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick();
            }
        });
        holder.ivLongPhoto.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                listener.onPhotoScaleChanged();

            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PreviewPhotosViewHolder extends RecyclerView.ViewHolder {
        public SubsamplingScaleImageView ivLongPhoto;
        private PhotoView ivPhotoView;

        PreviewPhotosViewHolder(View itemView) {
            super(itemView);
            ivLongPhoto = itemView.findViewById(R.id.iv_long_photo);
            ivPhotoView = itemView.findViewById(R.id.iv_photo_view);
        }
    }
}
