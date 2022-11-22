package com.iskyun.im.helper;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.databinding.ViewItemPopupBinding;
import com.iskyun.im.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

public class PopupWindowHelper {
    private PopupAdapter popupAdapter;
    private PopupWindow popupWindow;
    private OnPopupItemClickListener listener;

    public PopupWindowHelper() {
        View view = LayoutInflater.from(ImLiveApp.get()).inflate(R.layout.view_popup, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        DividerItemDecoration divider = new DividerItemDecoration(ImLiveApp.get(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(DisplayUtils.getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(divider);
        popupAdapter = new PopupAdapter();
        recyclerView.setAdapter(popupAdapter);

        popupAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if(listener != null){
                listener.onItemClickListener((PopupItemModel) adapter.getItem(position));
            }
            popupWindow.dismiss();
        });

        popupWindow = new PopupWindow(view, DisplayUtils.dp2px(120),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
    }

    public void setBackGround(@DrawableRes int resId) {
        setBackGround(DisplayUtils.getDrawable(resId));
    }

    public void setBackGround(Drawable drawable) {
        popupWindow.getContentView().setBackground(drawable);
    }

    public void create() {
        popupAdapter.setList(itemModels);
    }

    public void setLayoutParams(int width, int height) {
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
    }

    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, xoff, yoff, Gravity.TOP | Gravity.START);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        popupWindow.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        popupWindow.showAtLocation(parent, gravity, x, y);
    }


    List<PopupItemModel> itemModels = new ArrayList<>();

    public void addPopupItem(PopupItemModel itemModel) {
        if (!itemModels.contains(itemModel)) {
            itemModels.add(itemModel);
        }
    }

    public void addPopupItem(String title,int number) {
        PopupItemModel itemModel = new PopupItemModel(number,title);
        if (!itemModels.contains(itemModel)) {
            itemModels.add(itemModel);
        }
    }

    public void addOnPopupItemClickListener(OnPopupItemClickListener listener){
        this.listener = listener;
    }

    public interface OnPopupItemClickListener{
        void onItemClickListener(PopupItemModel itemModel);
    }

    public class PopupAdapter extends BaseQuickAdapter<PopupItemModel,
            BaseDataBindingHolder<ViewItemPopupBinding>> {

        public PopupAdapter() {
            super(R.layout.view_item_popup);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder<ViewItemPopupBinding> holderBinding,
                               PopupItemModel itemModel) {

            ViewItemPopupBinding binding = holderBinding.getDataBinding();
            if(binding != null){
                if(itemModel.getNumber() == 0){
                    binding.viewPopTvNum.setVisibility(View.INVISIBLE);
                }else {
                    binding.viewPopTvNum.setVisibility(View.VISIBLE);
                    binding.viewPopTvNum.setText(String.valueOf(itemModel.getNumber()));
                }
                binding.viewPopTvTitle.setText(itemModel.getTitle());
            }
        }
    }

    public static class PopupItemModel {
        private String title;
        private int number;

        public PopupItemModel(String title){
            this.title = title;
        }

        public PopupItemModel(int number,String title) {
            this.title = title;
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }


}
