package com.iskyun.im.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.MyGiftAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.ActivityMineMygiftBinding;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.UserUtils;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.ui.mine.viewmodel.MyGiftViewModel;
import com.iskyun.im.widget.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MyGiftActivity extends BaseActivity<ActivityMineMygiftBinding, MyGiftViewModel> implements View.OnClickListener{
    private MyGiftAdapter myGiftAdapter;
    private List<Gift> data=new ArrayList<>();
    private int  userId;
    private String  userName;
    private String  HeadUrl;

    @Override
    public MyGiftViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(MyGiftViewModel.class);
    }

    @Override
    public ActivityMineMygiftBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityMineMygiftBinding.inflate(inflater);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).titleBar(mViewBinding.getRoot().findViewById(R.id.tl_header))
                .init();
    }

    public static void launcher(int userId,String userName,String HeadUrl) {
        Bundle args = new Bundle();
        args.putInt(Constant.ANCHOR_ID, userId);
        args.putString(Constant.ANCHOR_NAME, userName);
        args.putString(Constant.ANCHOR_HEADURL, HeadUrl);
        ActivityUtils.launcher(ImLiveApp.get().getTopActivity(),args, MyGiftActivity.class);
    }

    @Override
    public void initBase() {
        User user = SpManager.getInstance().getCurrentUser();

        if (getIntent().getExtras() != null) {
            userId = getIntent().getExtras().getInt(Constant.ANCHOR_ID);
            userName = getIntent().getExtras().getString(Constant.ANCHOR_NAME);
            HeadUrl = getIntent().getExtras().getString(Constant.ANCHOR_HEADURL);
            ImageLoader.get().loadCropCircle(mViewBinding.userInfoIvAvatar, HeadUrl);
            mViewBinding.userTvName.setText(userName);
        }else {
            userId=user.getId();
            UserUtils.setHeaderUrl(mViewBinding.userInfoIvAvatar);
            mViewBinding.userTvName.setText(user.getNickname());
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        RecyclerView.ItemDecoration decoration = new GridItemDecoration(this,
                getResources().getDimensionPixelSize(R.dimen.text_margin), R.color.white);
        mViewBinding.recyclerView.setLayoutManager(layoutManager);
        mViewBinding.recyclerView.addItemDecoration(decoration);
        mViewBinding.recyclerView.setNestedScrollingEnabled(true);
        mViewBinding.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        myGiftAdapter = new MyGiftAdapter(R.layout.view_item_mygift);
        mViewBinding.recyclerView.setAdapter(myGiftAdapter);

        mViewBinding.tlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mViewModel.getFindMyGifts(userId).observe(this, myGift -> {
            if (mViewModel.isRefresh()) {
                data.clear();
            }
            data.addAll(myGift);
            int mathSumInt=0;
            if (data.size()!=0){
              myGiftAdapter.setList(data);

              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                  mathSumInt= myGift.stream().mapToInt(Gift::getGiftNum).sum();
              }
              mViewBinding.mygiftTvGiftNum.setText(String.valueOf("共收到"+mathSumInt+"件礼物"));
            }else {
                mViewBinding.recyclerView.setVisibility(View.GONE);
                mViewBinding.emptyGiftBox.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public int getTitleText() {
         return 0;
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    @Override
    public void onClick(View view) {

    }
}
