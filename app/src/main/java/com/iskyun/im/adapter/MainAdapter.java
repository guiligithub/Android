package com.iskyun.im.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.AuthStatus;
import com.iskyun.im.databinding.ViewItemAlbumBinding;
import com.iskyun.im.ui.mine.MineAlbumActivity;
import com.iskyun.im.utils.FileUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.glide.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回图片的列表适配器
 * Created by huan on 2017/10/30.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private OnItemClickListener mItemClickListener;
    private OnItemDelListener mOnItemDelListener;
    public static final int TYPE_ADD = 1;
    public static final int TYPE_PICTURE = 2;

    private int selectMax = 6;

    private List<Photo> list = new ArrayList<>();


    public MainAdapter() {
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewItemAlbumBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        ViewItemAlbumBinding binding = holder.binding;
        if (getItemViewType(position) == TYPE_ADD) {
            binding.image.setImageResource(R.mipmap.icon_add_album);
            binding.image.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onPictureSelect();
                }
            });
            binding.imagePause.setVisibility(View.GONE);
            binding.imageDel.setVisibility(View.GONE);
        } else {
            Photo photo = list.get(position);
            binding.imageDel.setVisibility(View.VISIBLE);
            binding.imageDel.setOnClickListener(view -> {
                int index = holder.getAbsoluteAdapterPosition();
                if (photo.name.endsWith(MineAlbumActivity.nameFlag)) {
                    deleteOfNet(position);
                } else {
                    delete(index);
                }
            });

            if (photo.type.startsWith("video")) {
                binding.imagePause.setVisibility(View.VISIBLE);
                binding.image.setImageBitmap(FileUtils.getFrameAtTime(photo.uri));
            } else {
                binding.imagePause.setVisibility(View.GONE);
                ImageLoader.get().load(binding.image, photo.path);
            }
            if (mItemClickListener != null) {
                binding.image.setOnClickListener(v -> {
                    int adapterPosition = holder.getAbsoluteAdapterPosition();
                    mItemClickListener.onItemClick(v, adapterPosition);
                });
            }

            if (photo.status == AuthStatus.NO_APPROVAL.getAuthStatus()) {
                binding.imageDel.setVisibility(View.GONE);
                binding.imageAuditStatus.setVisibility(View.VISIBLE);
                binding.imageAuditStatus.setImageResource(R.mipmap.icon_auditing);
            } else if (photo.status == AuthStatus.UN_PASS.getAuthStatus()) {
                binding.imageDel.setVisibility(View.GONE);
                binding.imageAuditStatus.setVisibility(View.VISIBLE);
                binding.imageAuditStatus.setImageResource(R.mipmap.icon_un_pass);
            } else {
                binding.imageDel.setVisibility(View.VISIBLE);
                binding.imageAuditStatus.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_ADD;
        } else {
            return TYPE_PICTURE;
        }
    }

    public void delete(int position) {
        try {
            if (position != RecyclerView.NO_POSITION && list.size() > position) {
                if (mOnItemDelListener != null) {
                    mOnItemDelListener.onDeleteLocalPhoto(position);
                }
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    public void deleteOfNet(int position) {
        if (mOnItemDelListener != null) {
            mOnItemDelListener.onDeleteNetPhoto(list.get(position), position);
        }
    }


    public void setList(List<Photo> photos) {
        this.list = photos;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    private boolean isShowAddItem(int position) {
        return position == list.size();
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public void setOnItemDelListener(OnItemDelListener l) {
        this.mOnItemDelListener = l;
    }

    public interface OnItemClickListener {
        /**
         * Item click event
         */
        void onItemClick(View v, int position);

        /**
         * add选择
         */
        void onPictureSelect();
    }

    public interface OnItemDelListener {
        void onDeleteNetPhoto(Photo photo, int position);

        void onDeleteLocalPhoto(int position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ViewItemAlbumBinding binding;

        public ViewHolder(ViewItemAlbumBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
