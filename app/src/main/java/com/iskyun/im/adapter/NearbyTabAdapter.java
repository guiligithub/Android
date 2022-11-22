package com.iskyun.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.databinding.ViewItemNearbyTabBinding;
import com.iskyun.im.utils.Pictures;

import java.util.ArrayList;
import java.util.List;

public class NearbyTabAdapter extends BaseQuickAdapter<String, BaseDataBindingHolder<ViewItemNearbyTabBinding>>
        implements LoadMoreModule {

    private NearbyAdapter nearbyadapter;
    private ArrayList<Pictures> selectedPhotoList = new ArrayList<Pictures>();
    private LayoutInflater mInflater;
    private Context context;
    private List<Pictures> mPictures = new ArrayList<>();//数据源
    private View view;

    public NearbyTabAdapter(@LayoutRes int layoutResId, Context context) {
        super(layoutResId);
        this.context=context;
        mInflater = LayoutInflater.from(context);
        inits();
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemNearbyTabBinding>
                                   holder, String item) {

//        ViewItemNearbyTabBinding tabBinding = holder.getDataBinding();
//        tabBinding.imagerecycler.setLayoutManager(new GridLayoutManager(getContext(),3));
//        nearbyadapter = new NearbyAdapter(getContext(),selectedPhotoList);
//        tabBinding.imagerecycler.setAdapter(nearbyadapter);
//        view =mInflater.inflate(R.layout.nearby_item,null);
//        ImageView image =view.findViewById(R.id.photo);

    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }

    @Override
    public int getItemCount() {
        return 9;
    }


    private void inits(){
        for (int i=0;i<3;i++){
            Pictures pictures =new Pictures( R.mipmap.em_empty_photo);
            selectedPhotoList.add(pictures);
        }
    }
}

