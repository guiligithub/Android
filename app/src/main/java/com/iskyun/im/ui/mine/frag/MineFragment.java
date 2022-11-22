package com.iskyun.im.ui.mine.frag;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.R;
import com.iskyun.im.adapter.MineImageGridAdapter;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.bean.Relation;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.VipType;
import com.iskyun.im.databinding.FragmentMineBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.helper.SendMsgHelper;
import com.iskyun.im.ui.common.AristocraticPrivilegeActivity;
import com.iskyun.im.ui.common.RechargeActivity;
import com.iskyun.im.ui.mine.BrowseActivity;
import com.iskyun.im.ui.mine.MineAlbumActivity;
import com.iskyun.im.ui.mine.viewmodel.MineModel;
import com.iskyun.im.ui.mine.UserInfoActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DateUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.UserUtils;
import com.iskyun.im.utils.event.DiamondChangeEvent;
import com.iskyun.im.utils.event.PayEvent;
import com.iskyun.im.utils.event.RelationEvent;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.AnchorViewModel;
import com.iskyun.im.widget.GridItemDecoration;

import java.util.List;

public class MineFragment extends BaseFragment<FragmentMineBinding, MineModel> implements View.OnClickListener {
    private User user;
    private Relation mRelation;
    private AnchorViewModel anchorViewModel;

    public MineFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public MineModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(MineModel.class);
    }

    @Override
    public FragmentMineBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentMineBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        mRelation = new Relation();
        mViewBinding.mineHeader.setRelation(mRelation);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.findFragmentById(R.id.mine_item_container) == null) {
            MineItemFragment itemFragment = new MineItemFragment();
            ft.add(R.id.mine_item_container, itemFragment);
            ft.commit();
        }
        getRelationInfo();
        LiveDataBus.get().with(Constant.UPDATE_USER_SUCCESS, User.class).observe(this, new Observer<User>() {

            @Override
            public void onChanged(User user) {
                setUserInfo(user);
            }
        });

        //主播收到礼物消息
        LiveDataBus.get().with(Constant.RECEIVED_GIFT, Gift.class).observe(this, gift -> {
            LogUtils.e("主播收到礼物消息:" + GsonUtils.toJson(gift));
            double userBalance = user.getUserBalance() + gift.getGiftPrice() * gift.getGiftNum();
            user.setUserBalance(userBalance);
            SpManager.getInstance().setCurrentUser(user);
        });

        //钻石 元宝变化
        LiveDataBus.get().with(Constant.DIAMOND_CHANGE, DiamondChangeEvent.class).observe(this,
                changeEvent -> setDiamondBalance());

        LiveDataBus.get().with(Constant.VIP_CHANGE, PayEvent.class).observe(this,
                changeEvent -> {
                    user = SpManager.getInstance().getCurrentUser();
                    setVipInfo();
                });

        LiveDataBus.get().with(Constant.CHANGE_MINE, String.class).observe(this, s -> getRelationInfo());

        setUserInfo(SpManager.getInstance().getCurrentUser());

        mViewBinding.mineHeader.mineIvAvatar.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvSignature.setOnClickListener(this);
        mViewBinding.mineHeader.tvBuddhists.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvRecharge.setOnClickListener(this);
        mViewBinding.mineHeader.mineIvEdit.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvFkNum.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvFk.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvFs.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvGz.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvFsNum.setOnClickListener(this);
        mViewBinding.mineHeader.mineTvGzNum.setOnClickListener(this);
        mViewBinding.mineHeader.mineIvVipUp.setOnClickListener(this);
        mViewBinding.mineHeader.mineItemClMyAlbum.setOnClickListener(this);
        mViewBinding.mineHeader.mineAlbumRecyclerview.setOnClickListener(this);

        LiveDataBus.get().with(Constant.RELATION_FOLLOW, RelationEvent.class).observe(this, this::setRelation);
        LiveDataBus.get().with(Constant.RELATION_VISITOR, RelationEvent.class).observe(this, this::setRelation);
        LiveDataBus.get().with(Constant.RELATION_FANS, RelationEvent.class).observe(this, this::setRelation);


        RecyclerView.ItemDecoration decoration = new GridItemDecoration(getActivity(),
                getResources().getDimensionPixelSize(R.dimen.grid_decoration_width), R.color.white);
        mViewBinding.mineHeader.mineAlbumRecyclerview.addItemDecoration(decoration);
        mViewBinding.mineHeader.mineAlbumRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);

        MineImageGridAdapter imageGridAdapter = new MineImageGridAdapter();
        mViewBinding.mineHeader.mineAlbumRecyclerview.setAdapter(imageGridAdapter);

        anchorViewModel = new ViewModelProvider(this).get(AnchorViewModel.class);
        anchorViewModel.findAnchorDetailById(user.getId()).observe(this, anchor -> {
            //发送消息
            LiveDataBus.get().with(Constant.ANCHOR).postValue(anchor);
            if (anchor.getPhotoUrls().size() >= 5) {
                List<String> mPhotoUrlList = anchor.getPhotoUrls().subList(0, 5);
                imageGridAdapter.setList(mPhotoUrlList);
            } else {
                List<String> mPhotoUrlList = anchor.getPhotoUrls().subList(0, anchor.getPhotoUrls().size());
                imageGridAdapter.setList(mPhotoUrlList);
            }
        });
    }

    /**
     * 访客等
     */
    private void getRelationInfo() {
        mViewModel.refreshMyAttention().observe(this,
                relation -> {
                    mRelation.setAddFollowNum(relation.getAddFollowNum());
                    mRelation.setAddFansNum(relation.getAddFansNum());
                    mRelation.setAddVisitorNum(relation.getAddVisitorNum());
                    mRelation.setFansNum(relation.getFansNum());
                    mRelation.setFollowNum(relation.getFollowNum());
                    mRelation.setVisitorNum(relation.getVisitorNum());
                });

        mViewModel.selectBalance().observe(this, balance -> {
            if (user.getSex() == Sex.MAN.ordinal()) {
                user.setUserDiamond((int) balance.getDiamondNum());
            } else {
                if(balance.getBalanceNum() != 0){
                    user.setUserBalance(balance.getBalanceNum());
                }
            }
            SpManager.getInstance().setCurrentUser(user);
        });
    }

    private void setRelation(RelationEvent relationEvent) {
        switch (relationEvent.getRelation()) {
            case 0:
                mRelation.setAddVisitorNum(0);
                break;
            case 1:
                mRelation.setAddFansNum(0);
                break;
            case 2:
                mRelation.setAddFollowNum(0);
                break;
        }
    }

    private void setUserInfo(User user) {
        if (user != null) {
            this.user = user;
            mViewBinding.mineHeader.setUser(user);

            UserUtils.setHeaderUrl(mViewBinding.mineHeader.mineIvAvatar);

            int iconSexResId, bgSexResId;
            if (user.getSex() == Sex.WOMAN.ordinal()) {
                iconSexResId = R.mipmap.icon_sex_woman;
                bgSexResId = R.mipmap.sex_bg_woman;
                mViewBinding.mineHeader.mineItemClMyAlbum.setVisibility(View.VISIBLE);
                mViewBinding.mineHeader.mineIvVipIcon.setVisibility(View.INVISIBLE);
                mViewBinding.mineHeader.mineIvAuthStatus.setVisibility(View.VISIBLE);
                mViewBinding.mineHeader.mineIvVipUp.setVisibility(View.GONE);
                if (user.isAnchor() == 1) {
                    mViewBinding.mineHeader.mineIvAuthStatus.setImageResource(R.mipmap.icon_anchor_authentication);
                } else {
                    mViewBinding.mineHeader.mineIvAuthStatus.setImageResource(R.mipmap.icon_anchor_no_authentication);
                }
            } else {
                iconSexResId = R.mipmap.icon_sex_man;
                bgSexResId = R.mipmap.sex_bg_man;
                mViewBinding.mineHeader.mineVRechargeBg.setVisibility(View.VISIBLE);
                mViewBinding.mineHeader.tvBuddhists.setText("自由者");
                mViewBinding.mineHeader.mineCharacter.setImageResource(R.mipmap.boy_student);
                mViewBinding.mineHeader.mineIvAuthStatus.setVisibility(View.GONE);
                mViewBinding.mineHeader.mineIvVipUp.setVisibility(View.VISIBLE);
            }
            mViewBinding.mineHeader.mineTvAge.setCompoundDrawablesWithIntrinsicBounds(
                    DisplayUtils.getDrawable(iconSexResId), null, null, null);
            mViewBinding.mineHeader.mineTvAge.setBackground(DisplayUtils.getDrawable(bgSexResId));
            mViewBinding.mineHeader.mineTvAge.setText(String.valueOf(user.getAge()));

            checkVipExpire();
            setVipInfo();
        }
    }

    private void setDiamondBalance() {
        User user = SpManager.getInstance().getCurrentUser();
        if (user.getSex() == Sex.MAN.ordinal()) {
            mViewBinding.mineHeader.mineTvBalance.setText(String.valueOf(user.getUserDiamond()));
        } else {
            mViewBinding.mineHeader.mineTvBalance.setText(String.valueOf(user.getUserBalance()));
        }
    }

    /**
     * vip 有效期检测
     */
    private void checkVipExpire() {
        boolean isExpire = SendMsgHelper.getInstance().vipIsExpire();
        if (user.getVipType() != 0 && !isExpire) {
            user.setVipType(0);
            user.setVip(0);
            SpManager.getInstance().setCurrentUser(user);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setVipInfo() {
        if (user.getVipType() == 1) {
            mViewBinding.mineHeader.mineIvVipIcon.setVisibility(View.VISIBLE);
            mViewBinding.mineHeader.mineIvVipIcon.setImageResource(R.mipmap.icon_vip_an_crown);
            mViewBinding.mineHeader.mineTvNickName.setTextColor(getResources().getColor(R.color.orange));
            mViewBinding.mineHeader.ivVip.setImageResource(R.mipmap.icon_vip_2);
            mViewBinding.mineHeader.mineIvVipUp.setImageResource(R.mipmap.icon_vip_renewal);
            if(!TextUtils.isEmpty(user.getVipExpireTime())){
                mViewBinding.mineHeader.mineTvVipExpirationTime.setVisibility(View.VISIBLE);
                mViewBinding.mineHeader.mineTvVipExpirationTime.setText(
                        DateUtils.getDateNoYear(user.getVipExpireTime()) + getString(R.string.expire));
            }else {
                mViewBinding.mineHeader.mineTvVipExpirationTime.setVisibility(View.GONE);
            }
        } else if (user.getVipType() == 2) {
            mViewBinding.mineHeader.mineIvVipIcon.setVisibility(View.VISIBLE);
            mViewBinding.mineHeader.mineIvVipIcon.setImageResource(R.mipmap.icon_svip_an_crown);
            mViewBinding.mineHeader.mineTvNickName.setTextColor(getResources().getColor(R.color.svip_golden));
            mViewBinding.mineHeader.ivVip.setImageResource(R.mipmap.icon_svip_3);
            mViewBinding.mineHeader.mineIvVipUp.setImageResource(R.mipmap.icon_svip_renewal);
            if(!TextUtils.isEmpty(user.getVipExpireTime())){
                mViewBinding.mineHeader.mineTvVipExpirationTime.setVisibility(View.VISIBLE);
                mViewBinding.mineHeader.mineTvVipExpirationTime.setText(
                        DateUtils.getDateNoYear(user.getVipExpireTime()) + getString(R.string.expire));
            }else {
                mViewBinding.mineHeader.mineTvVipExpirationTime.setVisibility(View.GONE);
            }
        } else {
            mViewBinding.mineHeader.mineIvVipIcon.setImageDrawable(null);
            mViewBinding.mineHeader.mineIvVipIcon.setVisibility(View.INVISIBLE);
            mViewBinding.mineHeader.ivVip.setVisibility(View.GONE);
            mViewBinding.mineHeader.mineIvVipUp.setImageResource(R.mipmap.icon_open_vip);
            mViewBinding.mineHeader.mineTvVipExpirationTime.setVisibility(View.GONE);
        }
    }


    @Override
    public int getTitleText() {
        return 0;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_tv_recharge:
                ActivityUtils.launcher(getActivity(), RechargeActivity.class);
                break;
            case R.id.mine_iv_edit:
            case R.id.mine_iv_avatar:
            case R.id.mine_tv_signature:
            case R.id.tv_buddhists:
                ActivityUtils.launcher(getActivity(), UserInfoActivity.class);
                break;
            case R.id.mine_tv_fk_num:
            case R.id.mine_tv_fk:
                launcherBrowse(0);
                break;
            case R.id.mine_tv_fs_num:
            case R.id.mine_tv_fs:
                launcherBrowse(1);
                break;
            case R.id.mine_tv_gz_num:
            case R.id.mine_tv_gz:
                launcherBrowse(2);
                break;
            case R.id.mine_item_cl_my_album:
            case R.id.mine_album_recyclerview:
                ActivityUtils.launcher(getActivity(), MineAlbumActivity.class);
                break;
            case R.id.mine_iv_vip_up:
                rechargeVip();
                break;
        }
    }

    private void launcherBrowse(int id) {
        Intent intent = new Intent(getActivity(), BrowseActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void rechargeVip() {
        User user = SpManager.getInstance().getCurrentUser();
        AristocraticPrivilegeActivity.launcher(VipType.VIP_OF_C.getType());
        /*if (user != null && user.getVipType() == 0) {
        } else {
            VipRechargeActivity.launcher(VipType.VIP_OF_S.getType());
        }*/
    }


}