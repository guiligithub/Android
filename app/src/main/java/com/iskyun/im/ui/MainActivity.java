package com.iskyun.im.ui;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.net.impl.OnResourceParseCallback;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.databinding.ActivityMainBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.common.GifPlayActivity;
import com.iskyun.im.ui.dynamic.frag.DynamicFragment;
import com.iskyun.im.ui.frag.VersionFragment;
import com.iskyun.im.ui.home.frag.HomeFragment;
import com.iskyun.im.ui.home.HomeViewModel;
import com.iskyun.im.ui.login.viewmodel.LoginViewModel;
import com.iskyun.im.ui.message.frag.MessageFragment;
import com.iskyun.im.ui.mine.frag.MineFragment;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.AnchorViewModel;
import com.iskyun.im.viewmodel.VersionViewModel;
import com.tbruyelle.rxpermissions3.RxPermissions;

public class MainActivity extends BaseActivity<ActivityMainBinding, HomeViewModel> {
    //显示的最大数
    private static final int MAX_COUNT = 99;
    //menu 消息
    private static final int MENU_MSG_INDEX = 2;
    private final SparseArray<Fragment> fragArrays = new SparseArray<>();
    private SparseArray<TextView> sparseTvBadges;
    private int lastNavId;

    @Override
    public HomeViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(HomeViewModel.class);
    }

    @Override
    public ActivityMainBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("nav_id", lastNavId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int navId = savedInstanceState.getInt("nav_id");
        switchFragment(navId);
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initBase() {
        checkVersion();
        setContentLayoutParams();
        initFragment();
        reqPermissions();
        checkUnreadMsg();
        loginIm();
        getUserInfo();
        initMenuBadge(mViewBinding.navView);
        //订阅登录环信失败信息，登录失败继续登录
        LiveDataBus.get().with(Constant.HX_LOGIN_FAIL, String.class).observe(this, s -> loginIm());
        //订阅环信消息
        LiveDataBus.get().with(Constant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        LiveDataBus.get().with(Constant.CONVERSATION_DELETE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        LiveDataBus.get().with(Constant.CONVERSATION_READ, EaseEvent.class).observe(this, this::checkUnReadMsg);
        LiveDataBus.get().with(Constant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        LiveDataBus.get().with(Constant.RECEIVED_GIFT, Gift.class).observe(this, gift -> {
            LogUtils.e("主播收到礼物消息:" + GsonUtils.toJson(gift));
            if (gift.isSpecialEffects() == 1 && gift.getSpecialPic().endsWith(".gif")) {
                GifPlayActivity.launcher(gift.getSpecialPic());
            }
        });

        mViewModel.unReadObserve().observe(this, this::setUnReadMenuMsgCount);
        mViewBinding.navView.setItemIconTintList(null);//除去底部自带着色效果
    }

    private void reqPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        //LogUtils.e("granted");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        //LogUtils.e("shouldShowRequestPermissionRationale");
                    } else {
                        //LogUtils.e("permission");
                    }
                });
    }

    private void initFragment() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(R.id.navigation_home));
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        DynamicFragment dynamicFragment = (DynamicFragment) getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(R.id.navigation_dynamic));
        if (dynamicFragment == null) {
            dynamicFragment = new DynamicFragment();
        }
        MessageFragment messageFragment = (MessageFragment) getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(R.id.navigation_message));
        if (messageFragment == null) {
            messageFragment = new MessageFragment();
        }
        MineFragment mineFragment = (MineFragment) getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(R.id.navigation_mine));
        if (mineFragment == null) {
            mineFragment = new MineFragment();
        }

        fragArrays.put(R.id.navigation_home, homeFragment);
        fragArrays.put(R.id.navigation_dynamic, dynamicFragment);
        fragArrays.put(R.id.navigation_message, messageFragment);
        fragArrays.put(R.id.navigation_mine, mineFragment);

        lastNavId = R.id.navigation_home;
        switchFragment(0, lastNavId);

        mViewBinding.navView.setOnItemSelectedListener(item -> {
            switchFragment(item.getItemId());
            return true;
        });
    }

    private void switchFragment(int curNavId) {
        if (lastNavId != curNavId) {
            switchFragment(lastNavId, curNavId);
            lastNavId = curNavId;
        }
    }

    private void switchFragment(int lastNavId, int curNavId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = fragArrays.get(lastNavId);
        if (fragment != null) {
            transaction.hide(fragment);//隐藏上个Fragment
        }
        if (fragArrays.get(curNavId) instanceof MineFragment) {
            LiveDataBus.get().with(Constant.CHANGE_MINE).postValue(fragArrays.get(curNavId).getClass().getSimpleName());
        }
        if (!fragArrays.get(curNavId).isAdded()) {
            transaction.add(R.id.main_content, fragArrays.get(curNavId), String.valueOf(curNavId))
                    .show(fragArrays.get(curNavId)).commitAllowingStateLoss();
        } else {
            transaction.show(fragArrays.get(curNavId)).commitAllowingStateLoss();
        }
    }

    /**
     * 手动设置 mainContent marginTop, bottom
     * 不设置有内容显示不全
     */
    private void setContentLayoutParams() {
        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        int actionBarHeight = ImmersionBar.getActionBarHeight(this);
        FrameLayout layout = mViewBinding.mainContent;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(0, 0, 0, actionBarHeight);
        layout.setLayoutParams(params);
    }


    /**
     * BottomNavigationView  badge
     *
     * @param navView
     */
    private void initMenuBadge(BottomNavigationView navView) {
        sparseTvBadges = new SparseArray<>();
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0);
        int childCount = bottomNavigationMenuView.getChildCount();
        for (int index = 0; index < childCount; index++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(index);
            View rootBadge = LayoutInflater.from(navView.getContext()).inflate(R.layout.view_menu_badge,
                    itemView, true);
            TextView tvBadge = rootBadge.findViewById(R.id.message_badge);
            tvBadge.setVisibility(View.GONE);
            sparseTvBadges.put(index, tvBadge);
        }
    }

    /**
     * 未读消息数
     *
     * @param msgCount
     */
    public void setUnReadMenuMsgCount(int msgCount) {
        String desc = getMessageContent(msgCount);
        TextView tvBadge = sparseTvBadges.get(MENU_MSG_INDEX);
        if (!TextUtils.isEmpty(desc)) {
            tvBadge.setText(desc);
            tvBadge.setVisibility(View.VISIBLE);
        } else {
            tvBadge.setVisibility(View.GONE);
        }
    }

    private String getMessageContent(int messageCount) {
        if (messageCount > MAX_COUNT || messageCount == MAX_COUNT) {
            return String.valueOf(MAX_COUNT);
        } else if (messageCount > 0) {
            return String.valueOf(messageCount);
        } else {
            return "";
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    /**
     * 用户信息
     */
    private void getUserInfo() {
        AnchorViewModel anchorViewModel = onCreateViewModel(AnchorViewModel.class);
        anchorViewModel.setShowDialog(false);
        if (user != null && user.getSex() == Sex.WOMAN.ordinal()) {
            anchorViewModel.findAnchorDetailById(user.getId()).observe(this, anchor -> {
                try {
                    int isAnchor = anchor.isAnchor();
                    if (user.isAnchor() == 0) {
                        user.setAnchor(isAnchor);
                        user.setVip(anchor.isVip());
                        user.setVipType(anchor.getVipType());
                    }
                    user.setUserBalance(anchor.getUserBalance());
                    SpManager.getInstance().setCurrentUser(user);
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            });
        }

    }

    /**
     * 环信登录
     */
    private void loginIm() {
        LoginViewModel loginViewModel = onCreateViewModel(LoginViewModel.class);
        if (TextUtils.isEmpty(EMClient.getInstance().getCurrentUser())
                || !EMClient.getInstance().isLoggedIn()) {
            User user = SpManager.getInstance().getCurrentUser();
            //mViewModel.loginInIm("test1", "123456");
            //mViewModel.loginInIm("hxid-3019", "123456");
            String password = user.getPassword();
            if (TextUtils.isEmpty(password)) {
                password = "123456";
            }
            loginViewModel.loginInIm(Constant.HX_ID + user.getId(), "123456");
            loginViewModel.getHxLoginObservable().observe(this, new Observer<Resource<EaseUser>>() {
                @Override
                public void onChanged(Resource<EaseUser> easeUserResource) {

                    parseResource(easeUserResource, new OnResourceParseCallback<EaseUser>(false) {
                        @Override
                        public void onSuccess(@Nullable EaseUser data) {
                            LogUtils.i("登录环信成功：" + GsonUtils.toJson(data));
                        }

                        @Override
                        public void onError(int code, String message) {
                            super.onError(code, message);
                            LogUtils.e(message);
                            LiveDataBus.get().with(Constant.HX_LOGIN_FAIL).postValue(Constant.HX_LOGIN_FAIL);
                        }
                    });
                }
            });
        }
    }

    private void checkUnReadMsg(EaseEvent event) {
        if (event == null) {
            return;
        }
        checkUnreadMsg();
    }

    private void checkUnreadMsg() {
        mViewModel.checkUnreadMsg();
    }

    /**
     * 更新检测
     */
    private void checkVersion() {
        VersionViewModel versionViewModel = onCreateViewModel(VersionViewModel.class);
        versionViewModel.observerNewVersion().observe(this, version -> {
            VersionFragment fragment = VersionFragment.newInstance(version);
            fragment.showNow(getSupportFragmentManager(), "version");
        });
    }
}