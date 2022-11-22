package com.iskyun.im.ui.message.frag;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.modules.chat.interfaces.IChatExtendMenu;
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper;
import com.hyphenate.easeui.modules.menu.MenuItemBean;
import com.hyphenate.easeui.utils.EaseFileUtils;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.DiscernResult;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.helper.EmojiData;
import com.iskyun.im.helper.ImHelper;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.helper.SendMsgHelper;
import com.iskyun.im.ui.common.GifPlayActivity;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.ui.frag.GiftFragment;
import com.iskyun.im.ui.frag.SimpleDialogFragment;
import com.iskyun.im.ui.message.viewmodel.MessageViewModel;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.PermissionsManager;
import com.iskyun.im.viewmodel.FileUploadViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 在线客服
 */
public class OnlineFragment extends EaseChatFragment {
    private MessageViewModel viewModel;
    private FileUploadViewModel uploadViewModel;
    protected ClipboardManager clipboard;
    private Anchor mAnchor;
    private Uri imageUri;

    private static final int REQUEST_CODE_SELECT_AT_USER = 15;

    @Override
    public void initView() {
        super.initView();
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        resetChatExtendMenu();

        chatLayout.getChatInputMenu().getPrimaryMenu().getEditText().setText(getUnSendMsg());
        chatLayout.turnOnTypingMonitor(ImHelper.getInstance().getModel().isShowMsgTyping());

        LiveDataBus.get().with(Constant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(Constant.MESSAGE_CHANGE_CHANGE,
                EaseEvent.TYPE.MESSAGE));

        LiveDataBus.get().with(Constant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }
        });

        LiveDataBus.get().with(Constant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        LiveDataBus.get().with(Constant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshToLatest();
            }
        });
        LiveDataBus.get().with(Constant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                chatLayout.getChatMessageListLayout().refreshMessages();
            }
        });

        //更新用户属性刷新列表
        LiveDataBus.get().with(Constant.CONTACT_ADD, EaseEvent.class).observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }
            chatLayout.getChatMessageListLayout().refreshMessages();
        });
        LiveDataBus.get().with(Constant.CHAT_ANCHOR, Anchor.class).observe(this, anchor -> mAnchor = anchor);

        uploadViewModel = new ViewModelProvider(this).get(FileUploadViewModel.class);

        uploadViewModel.observerPhotoScan().observe(this, photoDiscern -> {
            if(!photoDiscern.getResults().isEmpty()){
                if(DiscernResult.PASS.equals(photoDiscern.getResults().get(0).getSuggestion())){
                    chatLayout.sendImageMessage(imageUri);
                }else {
                    ToastUtils.showToast(R.string.picture_review_failed);
                }
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        chatLayout.addExpireSendMsgCallback(() -> true);
    }

    @Override
    public void onUserAvatarClick(String username) {
        /*if (!TextUtils.equals(username, ImHelper.getInstance().getCurrentUser())) {
            if (mAnchor != null) {
                AnchorInfoActivity.launcher(mAnchor.getId(), mAnchor.getSex());
            }
        } else {
            ActivityUtils.launcher(getActivity(), UserInfoActivity.class);
        }*/
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        super.onChatExtendMenuItemClick(view, itemId);
        switch (itemId) {
            case R.id.chat_extend_album:
                PermissionsManager.getInstance().permissions(this, new PermissionsManager.OnPermissionsCallback() {
                            @Override
                            public void granted() {
                                if (SendMsgHelper.getInstance().isExpireSendMsg(mAnchor)) {
                                    EasyPhotos.createAlbum(getActivity(), true, false, GlideEngine.getInstance())
                                            .setFileProviderAuthority(Constant.AUTHORITY)
                                            .setCount(1)
                                            .start(UploadType.ALBUM.getValue());
                                }
                            }
                        }, Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.chat_extend_gift:
                GiftFragment giftFragment = new GiftFragment(conversationId);
                giftFragment.show(getChildFragmentManager(), "gift");
                giftFragment.setGiftGiveCallback(this::giftGive);
                break;
            default:
                break;
        }
    }

    /**
     * 赠送礼物
     *
     * @param gift
     */
    private void giftGive(Gift gift) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(Constant.GIVE_GIFT_EVENT);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GIVE_GIFT, GsonUtils.toJson(gift));
        body.setParams(params);
        message.setBody(body);
        message.setTo(conversationId);
        chatLayout.sendMessage(message);
        if(gift.isSpecialEffects() == 1 && gift.getSpecialPic().endsWith(".gif")){
            GifPlayActivity.launcher(gift.getSpecialPic());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null)
            return;
        if (requestCode == UploadType.ALBUM.getValue()) {
            ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
            Uri selectedImage = resultPhotos.get(0).uri;
            if (selectedImage != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);
                if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    photoDiscern(Uri.parse(filePath));
                    //chatLayout.sendImageMessage(Uri.parse(filePath));
                } else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data);
                    photoDiscern(selectedImage);
                    //chatLayout.sendImageMessage(selectedImage);
                }
            }
        }
    }

    /**
     * 图片鉴别
     * @param uri
     */
    private void photoDiscern(Uri uri){
        this.imageUri = uri;
        uploadViewModel.photoDiscern(uri.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        //保存未发送的文本消息内容
//        if(mContext != null && mContext.isFinishing()) {
//            if(chatLayout.getChatInputMenu() != null) {
//                saveUnSendMsg(chatLayout.getInputContent());
//                LiveDataBus.get().with(DemoConstant.MESSAGE_NOT_SEND).postValue(true);
//            }
//        }
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, EMMessage message, View v) {
        if(System.currentTimeMillis() - message.getMsgTime() > 2 * 60 * 1000) {
            helper.findItemVisible(com.hyphenate.easeui.R.id.action_chat_recall, false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, EMMessage message) {
        if (item.getItemId() == R.id.action_chat_delete) {
            showDeleteDialog(message);
            return true;
        }
        return false;
    }

    private void resetChatExtendMenu() {
        IChatExtendMenu chatExtendMenu = chatLayout.getChatInputMenu().getChatExtendMenu();
        chatExtendMenu.clear();
        chatExtendMenu.registerMenuItem(R.string.album, R.mipmap.icon_chat_menu_album, R.id.chat_extend_album);
        //chatExtendMenu.registerMenuItem(R.string.voice, R.mipmap.icon_chat_menu_voice, R.id.chat_extend_voice);
        //chatExtendMenu.registerMenuItem(R.string.video, R.mipmap.icon_chat_menu_video, R.id.chat_extend_video);
        chatExtendMenu.registerMenuItem(R.string.gift, R.mipmap.icon_chat_menu_gift, R.id.chat_extend_gift);

        //添加扩展表情
        chatLayout.getChatInputMenu().getEmojiconMenu().addEmojiconGroup(EmojiData.getData());
    }

    private void showDeleteDialog(EMMessage message) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(getString(R.string.confirm_delete))
                .setConfirmColor(R.color.colorAccent)
                .setOnConfirmClickListener(getString(R.string.delete), view -> chatLayout.deleteMessage(message))
                .showCancelButton(true)
                .show();
    }

    /**
     * 保存未发送的文本消息内容
     *
     * @param content
     */
    private void saveUnSendMsg(String content) {
        ImHelper.getInstance().getModel().saveUnSendMsg(conversationId, content);
    }

    private String getUnSendMsg() {
        return ImHelper.getInstance().getModel().getUnSendMsg(conversationId);
    }

}

