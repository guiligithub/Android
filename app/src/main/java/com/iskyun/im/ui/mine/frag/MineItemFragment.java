package com.iskyun.im.ui.mine.frag;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.data.bean.MineItemModel;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.FragmentMineItemBinding;
import com.iskyun.im.databinding.ViewItemMineBinding;
import com.iskyun.im.ui.auth.AuthActivity;
import com.iskyun.im.ui.common.FeedbackActivity;
import com.iskyun.im.ui.mine.MineAlbumActivity;
import com.iskyun.im.ui.mine.MineDynamicActivity;
import com.iskyun.im.ui.mine.viewmodel.MineModel;
import com.iskyun.im.ui.mine.MyGiftActivity;
import com.iskyun.im.ui.mine.RevenueCenterActivity;
import com.iskyun.im.ui.setting.BeautySettingActivity;
import com.iskyun.im.ui.setting.SettingActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.manager.SpManager;

import java.util.ArrayList;
import java.util.List;

public class MineItemFragment extends BaseListFragment<FragmentMineItemBinding, MineModel> {
    public MineItemFragment() {
    }

    private static class ItemAdapter extends BaseQuickAdapter<MineItemModel,
            BaseDataBindingHolder<ViewItemMineBinding>> {

        public ItemAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder<ViewItemMineBinding> itemHolder,
                               MineItemModel model) {
            ViewItemMineBinding binding = itemHolder.getDataBinding();
            if (binding == null)
                return;
            binding.mineItemIvIcon.setImageResource(model.getIcon());
            binding.mineItemTvText.setText(model.getTitle());
        }
    }

    @Override
    protected BaseQuickAdapter<MineItemModel,
            BaseDataBindingHolder<ViewItemMineBinding>> onCreateAdapter() {
        return new ItemAdapter(R.layout.view_item_mine);
    }

    @Override
    protected boolean loadMoreEnable() {
        return false;
    }

    @Override
    protected boolean refreshEnable() {
        return false;
    }

    @Override

    public MineModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public void initBase() {
        super.initBase();
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null && Sex.MAN.ordinal() == user.getSex()) {
            ((ItemAdapter) mAdapter).setList(getUserModels());
        } else {
            ((ItemAdapter) mAdapter).setList(getAnchorModels());
        }

        mAdapter.setOnItemClickListener((adapter1, view, position) -> {
            MineItemModel model = (MineItemModel) adapter1.getData().get(position);
            try {
                ActivityUtils.launcher(getActivity(), Class.forName(model.getClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public FragmentMineItemBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentMineItemBinding.inflate(inflater);
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    /**
     * 用户item
     */
    private List<MineItemModel> getUserModels() {
        List<MineItemModel> items = new ArrayList<>();

//        items.add(new MineItemModel(getString(R.string.member_center),
//                R.mipmap.icon_vip_center, ""));
//        items.add(new MineItemModel(getString(R.string.grades),
//                R.mipmap.icon_my_level_man, ""));
        items.add(new MineItemModel(getString(R.string.mine_dynamic),
                R.mipmap.icon_my_dynamic, MineDynamicActivity.class.getName()));
      /*  items.add(new MineItemModel(getString(R.string.beauty_effects),
                R.mipmap.icon_beauty_effects, ""));*/
      /*  items.add(new MineItemModel(getString(R.string.mine_gift),
                R.mipmap.icon_my_gift, MyGiftActivity.class.getName()));*/
        items.add(new MineItemModel(getString(R.string.mine_album),
                R.mipmap.icon_my_album, MineAlbumActivity.class.getName()));
        /*items.add(new MineItemModel(getString(R.string.verified),
                R.mipmap.icon_anchor_setting, AuthActivity.class.getName()));*/

        //items.add(new MineItemModel(getString(R.string.my_share), R.mipmap.icon_my_share,  ""));

        items.add(new MineItemModel(getString(R.string.feedback_and_complaints),
                R.mipmap.icon_feed_back,  FeedbackActivity.class.getName()));
//        items.add(new MineItemModel(getString(R.string.secrets),
//                R.mipmap.icon_girl_strategy, ""));
        items.add(new MineItemModel(getString(R.string.mine_setting),
                R.mipmap.icon_setting, SettingActivity.class.getName()));
        return items;
    }

    /**
     * 主播item
     */
    private List<MineItemModel> getAnchorModels() {
        List<MineItemModel> items = new ArrayList<>();

        items.add(new MineItemModel(getString(R.string.mine_dynamic),
                R.mipmap.icon_my_dynamic, MineDynamicActivity.class.getName()));

        items.add(new MineItemModel(getString(R.string.beauty_effects),
                R.mipmap.icon_beauty_effects, BeautySettingActivity.class.getName()));
        items.add(new MineItemModel(getString(R.string.anchor_cert),
                R.mipmap.icon_anchor_setting, AuthActivity.class.getName()));
        items.add(new MineItemModel(getString(R.string.revenue_center),
                R.mipmap.icon_revenue_center, RevenueCenterActivity.class.getName()));
//        items.add(new MineItemModel(getString(R.string.anchor_strategy),
//                R.mipmap.icon_anchor_strategy, ""));
//        items.add(new MineItemModel(getString(R.string.disturb),
//                R.mipmap.icon_disturb, ""));
        items.add(new MineItemModel(getString(R.string.mine_gift),
                R.mipmap.icon_my_gift, MyGiftActivity.class.getName()));
     /*   items.add(new MineItemModel(getString(R.string.mine_album),
                R.mipmap.icon_my_album, MineAlbumActivity.class.getName()));*/
       // items.add(new MineItemModel(getString(R.string.my_share), R.mipmap.icon_my_share,  ""));
        items.add(new MineItemModel(getString(R.string.feedback_and_complaints), R.mipmap.icon_feed_back,  FeedbackActivity.class.getName()));
//        items.add(new MineItemModel(getString(R.string.invite),
//                R.mipmap.icon_invite, SettingActivity.class.getName()));
        items.add(new MineItemModel(getString(R.string.mine_setting),
                R.mipmap.icon_setting, SettingActivity.class.getName()));
        return items;
    }
}
