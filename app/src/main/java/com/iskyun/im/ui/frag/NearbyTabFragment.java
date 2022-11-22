package com.iskyun.im.ui.frag;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.NearbyTabAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.databinding.FragmentHomeTabBinding;
import com.iskyun.im.databinding.ViewItemNearbyTabBinding;
import com.iskyun.im.viewmodel.NearbyViewModel;

import java.util.ArrayList;
import java.util.List;

public class NearbyTabFragment extends BaseListFragment<FragmentHomeTabBinding, NearbyViewModel> {

    private NearbyTabAdapter nearbyTabAdapter;
    private List<String> data;
    private RecyclerView recyclerview;


    public NearbyTabFragment() {
        // Required empty public constructor
    }


    @Override
    public NearbyViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(NearbyViewModel.class);
    }

    @Override
    public FragmentHomeTabBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentHomeTabBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        super.initBase();
        data = new ArrayList<>();
        nearbyTabAdapter.setEmptyView(R.layout.view_empty);
//        mViewModel.getTest().observe(getActivity(), articleBean -> {
//            nearbyTabAdapter.setEmptyView(R.layout.view_empty);
//            if (mViewModel.isRefresh()) {
//                data.clear();
//            }
//            List<ArticleBean.DatasBean> list = articleBean.getDatas();
//            if (list == null || list.isEmpty()) {
//                return;
//            }
//            for (ArticleBean.DatasBean bean : list) {
//                data.add(bean.getAuthor());
//            }
//            nearbyTabAdapter.setList(data);
//            LogUtils.e("data-->" + data.size() + ",adapter-->" + nearbyTabAdapter.getData().size() + "");
//        });
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    protected BaseQuickAdapter<String, BaseDataBindingHolder<ViewItemNearbyTabBinding>> onCreateAdapter() {
        nearbyTabAdapter = new NearbyTabAdapter(R.layout.view_item_nearby_tab,getContext());
        return nearbyTabAdapter;
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }
}

