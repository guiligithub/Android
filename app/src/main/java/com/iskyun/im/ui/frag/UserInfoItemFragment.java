package com.iskyun.im.ui.frag;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.databinding.FragmentUserInfoItemBinding;
import com.iskyun.im.databinding.ViewUserInfoListItemBinding;
import com.iskyun.im.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 年龄，职业 等选择
 */
public class UserInfoItemFragment extends BaseDialogFragment<FragmentUserInfoItemBinding> {

    public static final String TYPE = "TYPE";
    public static final int AGE = 0x01;

    private static final String DEFAULT_VALUE = "DEFAULT_VALUE";

    private ListItemAdapter adapter;
    private int type;
    private String defaultValue;
    private int selectIndex;
    private UserInfoItemClickListener listener;

    private UserInfoItemFragment() {
    }

    public static UserInfoItemFragment newInstance(int type, String defaultValue) {
        UserInfoItemFragment fragment = new UserInfoItemFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        args.putString(DEFAULT_VALUE, defaultValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.setDialogParams();
    }

    @Override
    public void setDialogParams() {
        try {
            Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = (int) (DisplayUtils.getHeightPixels() * 0.55f);
            lp.gravity = Gravity.BOTTOM;
            setDialogParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FragmentUserInfoItemBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentUserInfoItemBinding.inflate(inflater);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        DividerItemDecoration divider = new DividerItemDecoration(ImLiveApp.get(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(DisplayUtils.getDrawable(R.drawable.divider));
        mViewBinding.recyclerView.addItemDecoration(divider);
        mViewBinding.recyclerView.setNestedScrollingEnabled(true);
        mViewBinding.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        adapter = new ListItemAdapter();
        mViewBinding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (listener != null) {
                listener.onItemClick((ListItemModel) adapter.getItem(position));
            }
            dismiss();
        });

        mViewBinding.itemTvCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void initData() {
        super.initData();
        if (getArguments() == null)
            return;
        type = getArguments().getInt(TYPE, AGE);
        defaultValue = getArguments().getString(DEFAULT_VALUE);

        switch (type) {
            case AGE:

                adapter.setList(getAges());

                mViewBinding.itemTvTitle.setText(R.string.age);
                break;
            default:
                break;
        }
        if (selectIndex > 8) {
            mViewBinding.recyclerView.scrollToPosition(selectIndex - 2);
        }else {
            mViewBinding.recyclerView.scrollToPosition(selectIndex);
        }
    }

    public static class ListItemAdapter extends BaseQuickAdapter<ListItemModel, BaseDataBindingHolder<ViewUserInfoListItemBinding>> {

        public ListItemAdapter() {
            super(R.layout.view_user_info_list_item);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder<ViewUserInfoListItemBinding> holderBinding, ListItemModel listItemModel) {
            if (holderBinding.getDataBinding() != null) {
                holderBinding.getDataBinding().setListItemModel(listItemModel);
                holderBinding.getDataBinding().viewUserInfoItemTv.setSelected(listItemModel.selected);
            }
        }
    }

    public List<ListItemModel> getJobs() {
        List<ListItemModel> jobs = new ArrayList<>();
        return jobs;
    }

    public List<ListItemModel> getAges() {
        List<ListItemModel> ages = new ArrayList<>();
        for (int age = 16; age <= 60; age++) {
            ListItemModel listItemModel = new ListItemModel("" + age);
            if (Objects.equals(String.valueOf(age), defaultValue)) {
                selectIndex = age - 16;
                listItemModel.setSelected(true);
            }
            ages.add(listItemModel);
        }
        return ages;
    }

    public static class ListItemModel {
        private String content;
        private boolean selected = false;

        public ListItemModel(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isSelected() {
            return selected;
        }


        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public interface UserInfoItemClickListener {
        void onItemClick(ListItemModel itemModel);
    }

    public void setUserInfoItemClickListener(UserInfoItemClickListener listener) {
        this.listener = listener;
    }
}
