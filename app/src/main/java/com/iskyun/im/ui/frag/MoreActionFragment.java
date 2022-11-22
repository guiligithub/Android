package com.iskyun.im.ui.frag;

import static com.iskyun.im.ui.frag.MoreActionFragment.MoreType.ATTENTION;
import static com.iskyun.im.ui.frag.MoreActionFragment.MoreType.UN_ATTENTION;
import static com.iskyun.im.ui.frag.MoreActionFragment.MoreType.UN_BLOCKLIST;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.MoreActionAdapter;
import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.databinding.FragmentMoreActionBinding;
import com.iskyun.im.ui.common.ComplaintActivity;
import com.iskyun.im.ui.home.AnchorInfoActivity;
import com.iskyun.im.viewmodel.RelationViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * bottom 弹出选择
 */
public class MoreActionFragment extends BaseDialogFragment<FragmentMoreActionBinding> {

    public static final String ANCHOR = "anchor";

    private static final String MORE_KEY = "MORE_KEY";
    public static final int FROM_CHAT = 0x101;//聊天会话
    public static final int FROM_INFO = 0x102;//主播资料详情
    public static final int FROM_DYNAMIC = 0x103;//动态tab
    public static final int FROM_DYNAMIC_RELEASE = 0x104;//发布动态
    public static final int FROM_DYNAMIC_ALBUM = 0x105;//选择相册
    public static final int FROM_AVATAR = 0x106;//头像
    public static final int FROM_USERINFO_ALBUM = 0x107;//选择相册，拍照


    private OnItemClickListener listener;
    private MoreActionAdapter adapter;
    private RelationViewModel relationViewModel;
    private Anchor anchor;


    private MoreActionFragment() {
    }

    public static MoreActionFragment newInstance(int forFrom) {
        MoreActionFragment fragment = new MoreActionFragment();
        Bundle args = new Bundle();
        args.putInt(MORE_KEY, forFrom);
        fragment.setArguments(args);
        return fragment;
    }

    public static MoreActionFragment newInstance(int forFrom, Anchor anchor) {
        MoreActionFragment fragment = new MoreActionFragment();
        Bundle args = new Bundle();
        args.putInt(MORE_KEY, forFrom);
        args.putParcelable(ANCHOR, anchor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogParams();
    }

    @Override
    public FragmentMoreActionBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentMoreActionBinding.inflate(inflater);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        DividerItemDecoration divider = new DividerItemDecoration(ImLiveApp.get(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.divider));
        mViewBinding.recyclerView.addItemDecoration(divider);
        mViewBinding.recyclerView.setNestedScrollingEnabled(true);
        mViewBinding.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        adapter = new MoreActionAdapter();
        adapter.setOnItemClickListener((adapter, view, position) -> itemClick((MoreType) adapter.getData().get(position)));
        mViewBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        super.initData();
        if (getArguments() == null) {
            return;
        }
        List<MoreType> moreTypes = new ArrayList<>();
        int forFrom = getArguments().getInt(MORE_KEY);
        anchor = getArguments().getParcelable(ANCHOR);
        if (forFrom == FROM_CHAT) {
            moreTypes.addAll(getChatMoreTypes());
        } else if (forFrom == FROM_INFO) {
            moreTypes.addAll(getInfoMoreTypes());
        } else if (forFrom == FROM_DYNAMIC) {
            moreTypes.addAll(getDynamicMoreTypes());
        } else if (forFrom == FROM_DYNAMIC_RELEASE) {
            moreTypes.addAll(getDynamicReleaseMoreTypes());
        } else if (forFrom == FROM_DYNAMIC_ALBUM) {
            moreTypes.addAll(getDynamicAlbumMoreTypes());
        } else if (forFrom == FROM_AVATAR) {
            moreTypes.addAll(getAvatarMoreTypes());
        } else if (forFrom == FROM_USERINFO_ALBUM) {
            moreTypes.addAll(getUserInfoAlbumMoreTypes());
        }
        adapter.addData(moreTypes);
    }

    private void itemClick(MoreType moreType) {
        switch (moreType) {
            case REPORT:
                if (anchor != null)
                    ComplaintActivity.launcher(anchor.getId());
                //ActivityUtils.launcher(getActivity(), ReportActivity.class);
                break;
            case MODIFY_REMARK:
                break;
            case LOOK_TA_INFO:
                if (anchor != null)
                    AnchorInfoActivity.launcher(anchor.getId(), anchor.getSex());
                break;
            case CLEAN_CHAT_RECORD:
            case ALBUM:
            case VIDEO:
            case PICTURE:
            case MY_FANS:
            case PRIVACY:
            case VIEW_PERMISSION:
            case BLOCKLIST:
            case UN_BLOCKLIST:
            case MY_ATTENTION:
            case ATTENTION:
            case UN_ATTENTION:
                if (listener != null) {
                    listener.onItemClick(moreType);
                }
                break;
        }
        dismiss();
    }

    /**
     * 把用户加入到黑名单
     */
    private void addToBlocklist() {
    }

    /**
     * 把用户从黑名单中移除
     */
    private void removeFromBlackList(){
    }

    private void attention() {
    }

    private void cleanChatRecord() {
    }

    private List<MoreType> getChatMoreTypes() {
        List<MoreType> moreTypes = new ArrayList<>();
        moreTypes.add(MoreType.LOOK_TA_INFO);
        moreTypes.add(MoreType.CLEAN_CHAT_RECORD);

        if (anchor != null) {
            if (anchor.isFocus() == 1) {
                moreTypes.add(UN_ATTENTION);
            } else {
                moreTypes.add(ATTENTION);
            }
            if (anchor.isBlack() == 1) {
                moreTypes.add(UN_BLOCKLIST);
            } else {
                moreTypes.add(MoreType.BLOCKLIST);
            }
        }

        moreTypes.add(MoreType.REPORT);
        moreTypes.add(MoreType.CANCEL);
        return moreTypes;
    }

    /**
     * 用户详情
     *
     * @return
     */
    private List<MoreType> getInfoMoreTypes() {
        List<MoreType> moreTypes = new ArrayList<>();
        //moreTypes.add(MoreType.MODIFY_REMARK);
        //关注 拉黑
        if (anchor != null) {
            if (anchor.isFocus() == 1) {
                moreTypes.add(UN_ATTENTION);
            } else {
                moreTypes.add(ATTENTION);
            }
            if (anchor.isBlack() == 1) {
                moreTypes.add(UN_BLOCKLIST);
            } else {
                moreTypes.add(MoreType.BLOCKLIST);
            }
        }

        moreTypes.add(MoreType.REPORT);
        moreTypes.add(MoreType.CANCEL);
        return moreTypes;
    }

    private List<MoreType> getDynamicMoreTypes() {
        List<MoreType> moreTypes = new ArrayList<>();
        moreTypes.add(MoreType.REPORT);
        //拉黑
        moreTypes.add(MoreType.BLOCKLIST);
        moreTypes.add(MoreType.CANCEL);
        return moreTypes;
    }

    /**
     * 动态  谁可以看
     *
     * @return
     */
    private List<MoreType> getDynamicReleaseMoreTypes() {
        List<MoreType> moreTypes = new ArrayList<>();
        moreTypes.add(MoreType.VIEW_PERMISSION);
        moreTypes.add(MoreType.MY_ATTENTION);
        moreTypes.add(MoreType.MY_FANS);
        moreTypes.add(MoreType.CANCEL);
        return moreTypes;
    }

    /**
     * 动态  添加图片选择
     *
     * @return
     */
    private List<MoreType> getDynamicAlbumMoreTypes() {
        List<MoreType> moreTypes = new ArrayList<>();
        moreTypes.add(MoreType.VIDEO);
        moreTypes.add(MoreType.PICTURE);
        moreTypes.add(MoreType.ALBUM);
        moreTypes.add(MoreType.CANCEL);
        return moreTypes;
    }

    /**
     * 头像
     *
     * @return
     */
    private List<MoreType> getAvatarMoreTypes() {
        List<MoreType> moreTypes = new ArrayList<>();
        moreTypes.add(MoreType.PICTURE);
        moreTypes.add(MoreType.ALBUM);
        moreTypes.add(MoreType.CANCEL);
        return moreTypes;
    }

    /**
     * 选择头像
     *
     * @return
     */
    private List<MoreType> getUserInfoAlbumMoreTypes() {
        List<MoreType> moreTypes = new ArrayList<>();
        moreTypes.add(MoreType.PICTURE);
        moreTypes.add(MoreType.ALBUM);
        moreTypes.add(MoreType.CANCEL);
        return moreTypes;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MoreType type);
    }

    public enum MoreType {

        CANCEL(0, ImLiveApp.get().getString(R.string.cancel)),
        BLOCKLIST(1, ImLiveApp.get().getString(R.string.blocklist)),
        ATTENTION(2, ImLiveApp.get().getString(R.string.attention)),
        CLEAN_CHAT_RECORD(3, ImLiveApp.get().getString(R.string.clean_chat_record)),
        LOOK_TA_INFO(4, ImLiveApp.get().getString(R.string.look_ta_info)),
        REPORT(5, ImLiveApp.get().getString(R.string.report)),
        MODIFY_REMARK(6, ImLiveApp.get().getString(R.string.modify_remark)),
        VIEW_PERMISSION(7, ImLiveApp.get().getString(R.string.view_permission)),
        MY_ATTENTION(8, ImLiveApp.get().getString(R.string.my_attention)),
        MY_FANS(9, ImLiveApp.get().getString(R.string.my_fans)),
        PRIVACY(15, ImLiveApp.get().getString(R.string.privacy)),

        UN_ATTENTION(10, ImLiveApp.get().getString(R.string.un_attention)),
        UN_BLOCKLIST(11, ImLiveApp.get().getString(R.string.un_blocklist)),

        ALBUM(12, ImLiveApp.get().getString(R.string.album)),
        PICTURE(13, ImLiveApp.get().getString(R.string.take_picture)),
        VIDEO(14, ImLiveApp.get().getString(R.string.dynamic_select_video));

        private int type;
        private String content;

        MoreType(int type, String content) {
            this.type = type;
            this.content = content;
        }

        public int getType() {
            return type;
        }

        public String getContent() {
            return content;
        }
    }
}