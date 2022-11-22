package com.iskyun.im.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.Pair;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMPushManager;
import com.hyphenate.cloud.EMHttpClient;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallEndReason;
import com.hyphenate.easecallkit.base.EaseCallKitConfig;
import com.hyphenate.easecallkit.base.EaseCallKitListener;
import com.hyphenate.easecallkit.base.EaseCallKitTokenCallback;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.base.EaseCallUserInfo;
import com.hyphenate.easecallkit.base.EaseGetUserAccountCallback;
import com.hyphenate.easecallkit.base.EaseUserAccount;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.delegate.EaseCustomAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseExpressionAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseImageAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseTextAdapterDelegate;
import com.hyphenate.easeui.delegate.EaseVoiceAdapterDelegate;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EaseMessageTypeSetManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.provider.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.provider.EaseSettingsProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.push.EMPushConfig;
import com.iskyun.im.BuildConfig;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.delegates.ChatGiftAdapterDelegate;
import com.iskyun.im.adapter.delegates.ChatRecallAdapterDelegate;
import com.iskyun.im.adapter.delegates.ChatVideoCallAdapterDelegate;
import com.iskyun.im.adapter.delegates.ChatVoiceCallAdapterDelegate;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.AppModel;
import com.iskyun.im.data.local.DbHelper;
import com.iskyun.im.receiver.HeadsetReceiver;
import com.iskyun.im.ui.message.ChatActivity;
import com.iskyun.im.ui.message.VideoCallActivity;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ease.FetchUserInfoList;
import com.iskyun.im.utils.ease.FetchUserRunnable;
import com.iskyun.im.utils.event.CallEndEvent;
import com.iskyun.im.utils.manager.ExecutorManager;
import com.iskyun.im.utils.manager.SpManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ImHelper {
    private static final String TAG = ImHelper.class.getSimpleName();
    private AppModel appModel = null;

    private static ImHelper mInstance;
    private Context mainContext;
    public boolean isSDKInit;
    private EaseCallKitListener callKitListener;
    private final String tokenUrl = Constant.API_URL + "common/fileUpload/rtcToken";
    //    private String tokenUrl = "http://a1.easemob.com/token/rtcToken/v1";
    private String uIdUrl = "http://a1.easemob.com/channel/mapper";

    private FetchUserInfoList fetchUserInfoList;
    private Thread fetchUserTread;
    private FetchUserRunnable fetchUserRunnable;

    public static ImHelper getInstance() {
        if (mInstance == null) {
            synchronized (ImHelper.class) {
                if (mInstance == null) {
                    mInstance = new ImHelper();
                }
            }
        }
        return mInstance;
    }


    public AppModel getModel() {
        if (appModel == null) {
            appModel = new AppModel(ImLiveApp.get());
        }
        return appModel;
    }

    /**
     * 获取IM SDK的入口类
     *
     * @return
     */
    public EMClient getEMClient() {
        return EMClient.getInstance();
    }

    public void init(Context context) {
        appModel = new AppModel(context);
        //初始化IM SDK
        if (initSDK(context)) {
            // debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);
            // set Call options
            setCallOptions(context);
            //初始化推送
            initPush(context);
            //注册call Receiver
//            initReceiver(context);
            //初始化ease ui相关
            initEaseUI(context);
            //注册对话类型
            registerConversationType();
            //callKit初始化
            InitCallKit(context);

            //启动获取用户信息线程
            fetchUserInfoList = FetchUserInfoList.getInstance();
            fetchUserRunnable = new FetchUserRunnable();
            fetchUserTread = new Thread(fetchUserRunnable);
            fetchUserTread.start();
        }

    }

    /**
     * get EMChatManager
     *
     * @return
     */
    public EMChatManager getChatManager() {
        return getEMClient().chatManager();
    }

    /**
     * get push manager
     *
     * @return
     */
    public EMPushManager getPushManager() {
        return getEMClient().pushManager();
    }

    /**
     * 判断是否之前登录过
     *
     * @return
     */
    public boolean isLoggedIn() {
        return getEMClient().isLoggedInBefore();
    }

    private void setCallOptions(Context context) {
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        IntentFilter headsetFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(headsetReceiver, headsetFilter);
    }


    /**
     * 初始化SDK
     *
     * @param context
     * @return
     */
    private boolean initSDK(Context context) {
        // 根据项目需求对SDK进行配置
        EMOptions options = initChatOptions(context);
        //配置自定义的rest server和im server
//        options.setRestServer("a1-hsb.easemob.com");
//        options.setIMServer("106.75.100.247");
//        options.setImPort(6717);

//        options.setRestServer("a41.easemob.com");
//        options.setIMServer("msync-im-41-tls-test.easemob.com");
//        options.setImPort(6717);
        // 初始化SDK
        isSDKInit = EaseIM.getInstance().init(context, options);
        //设置删除用户属性数据超时时间
        //appModel.setUserInfoTimeOut(30 * 60 * 1000);
        //更新过期用户属性列表
        updateTimeoutUsers();
        mainContext = context;
        return isSDKInit();
    }

    public boolean isSDKInit() {
        return isSDKInit;
    }

    public void initPush(Context context) {
//        if(EaseIM.getInstance().isMainProcess(context)) {
//            //OPPO SDK升级到2.1.0后需要进行初始化
//            HeytapPushManager.init(context, true);
//            //HMSPushHelper.getInstance().initHMSAgent(DemoApplication.getInstance());
//            EMPushHelper.getInstance().setPushListener(new PushListener() {
//                @Override
//                public void onError(EMPushType pushType, long errorCode) {
//                    // TODO: 返回的errorCode仅9xx为环信内部错误，可从EMError中查询，其他错误请根据pushType去相应第三方推送网站查询。
//                    EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
//                }
//
//                @Override
//                public boolean isSupportPush(EMPushType pushType, EMPushConfig pushConfig) {
//                    // 由外部实现代码判断设备是否支持FCM推送
//                    if(pushType == EMPushType.FCM){
//                        Log.i("FCM", "GooglePlayServiceCode:"+GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context));
//                        return demoModel.isUseFCM() && GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
//                    }
//                    return super.isSupportPush(pushType, pushConfig);
//                }
//            });
//        }
    }

    public String getCurrentUser() {
        return getEMClient().getCurrentUser();
    }

    /**
     * ChatPresenter中添加了网络连接状态监听，多端登录监听，群组监听，联系人监听，聊天室监听
     *
     * @param context
     */
    private void initEaseUI(Context context) {
        //添加ChatPresenter,ChatPresenter中添加了网络连接状态监听，
        EaseIM.getInstance().addChatPresenter(ChatPresenter.getInstance());
        EaseIM.getInstance().setSettingsProvider(new EaseSettingsProvider() {
                    @Override
                    public boolean isMsgNotifyAllowed(EMMessage message) {
                        return true;
                    }

                    @Override
                    public boolean isMsgSoundAllowed(EMMessage message) {
                        return true;
                    }

                    @Override
                    public boolean isMsgVibrateAllowed(EMMessage message) {
                        return true;
                    }

                    @Override
                    public boolean isSpeakerOpened() {
                        return true;
                    }
                }).setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {
                    @Override
                    public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                        EaseEmojiconGroupEntity data = EmojiData.getData();
                        for (EaseEmojicon emojicon : data.getEmojiconList()) {
                            if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                                return emojicon;
                            }
                        }
                        return null;
                    }

                    @Override
                    public Map<String, Object> getTextEmojiconMapping() {
                        return null;
                    }
                })
                .setAvatarOptions(getAvatarOptions())
                .setUserProvider(this::getUserInfo)
                .getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
                    @Override
                    public String getDisplayedText(EMMessage message) {
                        return null;
                    }

                    @Override
                    public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                        return EaseCommonUtils.getMessageDigest(message, context);
                    }

                    @Override
                    public String getTitle(EMMessage message) {
                        EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
                        if (user != null) {
                            return user.getNickname();
                        }
                        return null;
                    }

                    @Override
                    public int getSmallIcon(EMMessage message) {
                        return 0;
                    }

                    @Override
                    public Intent getLaunchIntent(EMMessage message) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, message.getFrom());
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                        return intent;
                    }
                });
    }

    private void InitCallKit(Context context) {
        EaseCallKitConfig callKitConfig = new EaseCallKitConfig();
        //设置呼叫超时时间
        callKitConfig.setCallTimeOut(30 * 1000);
        //设置振铃文件。
//        String ringFile = EaseFileUtils.getModelFilePath(context,"huahai.mp3");
//        callKitConfig.setRingFile(ringFile);
        //设置声网AgoraAppId
        //callKitConfig.setAgoraAppId("15cb0d28b87b425ea613fc46f7c9f974");
        callKitConfig.setAgoraAppId("966065c4220b4337b4f2d2ddf75c99ad");
        callKitConfig.setEnableRTCToken(true);
        EaseCallKit.getInstance().init(context, callKitConfig);
        // Register the activities which you have registered in manifest
        EaseCallKit.getInstance().registerVideoCallClass(VideoCallActivity.class);
        //EaseCallKit.getInstance().registerMultipleVideoClass(MultipleVideoActivity.class);
        addCallkitListener();
    }

    /**
     * 注册对话类型
     */
    private void registerConversationType() {
        EaseMessageTypeSetManager.getInstance()
                .addMessageType(EaseExpressionAdapterDelegate.class)       //自定义表情
//                .addMessageType(EaseFileAdapterDelegate.class)             //文件
                .addMessageType(EaseImageAdapterDelegate.class)            //图片
//                .addMessageType(EaseLocationAdapterDelegate.class)         //定位
//                .addMessageType(EaseVideoAdapterDelegate.class)            //视频
                .addMessageType(EaseVoiceAdapterDelegate.class)            //声音
//                .addMessageType(ChatConferenceInviteAdapterDelegate.class) //语音邀请
                .addMessageType(ChatRecallAdapterDelegate.class)           //消息撤回
                .addMessageType(ChatVideoCallAdapterDelegate.class)        //视频通话
                .addMessageType(ChatVoiceCallAdapterDelegate.class)        //语音通话
//                .addMessageType(ChatUserCardAdapterDelegate.class)         //名片消息
                .addMessageType(ChatGiftAdapterDelegate.class)     //礼物消息
                .addMessageType(EaseCustomAdapterDelegate.class)           //自定义消息
//                .addMessageType(ChatNotificationAdapterDelegate.class)     //入群等通知消息
                .setDefaultMessageType(EaseTextAdapterDelegate.class);       //文本
    }

    /**
     * 根据自己的需要进行配置
     *
     * @param context
     * @return
     */
    private EMOptions initChatOptions(Context context) {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();

        /**
         * NOTE:你需要设置自己申请的账号来使用三方推送功能，详见集成文档
         */
        EMPushConfig.Builder builder = new EMPushConfig.Builder(context);

//        builder
////                .enableVivoPush() // 需要在AndroidManifest.xml中配置appId和appKey
//                .enableMeiZuPush("134952", "f00e7e8499a549e09731a60a4da399e3")
//                .enableMiPush("2882303761517426801", "5381742660801")
//                .enableOppoPush("0bb597c5e9234f3ab9f821adbeceecdb",
//                        "cd93056d03e1418eaa6c3faf10fd7537")
//                .enableHWPush() // 需要在AndroidManifest.xml中配置appId
//                .enableFCM("782795210914");
//        options.setPushConfig(builder.build());

        //set custom servers, commonly used in private deployment


        // 设置退出(主动和被动退出)群组时是否删除聊天消息
        //options.setDeleteMessagesAsExitGroup(appModel.isDeleteMessagesAsExitGroup());
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
        //options.setAutoTransferMessageAttachments(appModel.isSetTransferFileByUser());
        // 是否自动下载缩略图，默认是true为自动下载
        //options.setAutoDownloadThumbnail(appModel.isSetAutodownloadThumbnail());
        return options;
    }

    /**
     * 更新过期的用户属性数据
     */
    public void updateTimeoutUsers() {
        List<String> userIds = appModel.selectTimeOutUsers();
        if (userIds != null && userIds.size() > 0) {
            if (fetchUserInfoList != null) {
                for (int i = 0; i < userIds.size(); i++) {
                    fetchUserInfoList.addUserId(userIds.get(i));
                }
            }
        }
    }

    int uid;

    /**
     * 增加EaseCallkit监听
     */
    public void addCallkitListener() {
        callKitListener = new EaseCallKitListener() {
            @Override
            public void onInviteUsers(Context context, String userId[], JSONObject ext) {
            }

            @Override
            public void onEndCallWithReason(EaseCallType callType, String channelName, EaseCallEndReason reason, long callTime) {
                Log.i(TAG, "onEndCallWithReason" + (callType != null ? callType.name() : " callType is null ") + " reason:" + reason + " time:" + callTime);
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String callString = mainContext.getString(R.string.call_duration);
                callString += formatter.format(callTime);
                //发送通话结束消息
                if (callType != null) {
                    CallEndEvent endEvent = new CallEndEvent(callType.code, callTime, reason.code);
                    LiveDataBus.get().with(Constant.END_CALL_EVENT).postValue(endEvent);
                }
                //Toast.makeText(mainContext, callString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGenerateToken(String userId, String channelName, String appKey, EaseCallKitTokenCallback callback) {
                Log.i(TAG, "onGenerateToken userId:" + userId + " channelName:" + channelName + " appKey:" + appKey);
                String url = tokenUrl;
                uid = EaseCallKitUtils.getRandomInt();
                url += "?";
                url += "userAccount=";
                url += userId;
                url += "&channelName=";
                url += channelName;
                url += "&uid=";
                url += uid;
                /*url += "&appkey=";
                url += appKey;*/
                LogUtils.e("url:" + url);
                //获取声网Token
                getRtcToken(url, callback);
            }

            @Override
            public void onReceivedCall(EaseCallType callType, String fromUserId, JSONObject ext) {
                //收到接听电话
                Log.e(TAG, "onRecivedCall" + callType.name() + " fromUserId:" + fromUserId);
            }

            @Override
            public void onCallError(EaseCallKit.EaseCallError type, int errorCode, String description) {
                LogUtils.e("EaseCallKitListener error code : " + errorCode);
            }

            @Override
            public void onInViteCallMessageSent() {
                LiveDataBus.get().with(Constant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(Constant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
            }

            @Override
            public void onRemoteUserJoinChannel(String channelName, String userName, int uid, EaseGetUserAccountCallback callback) {
                Log.i(TAG, "onRemoteUserJoinChannel:" + userName);
                if (userName == null || userName.equals("")) {
                    String url = uIdUrl;
                    url += "?";
                    url += "channelName=";
                    url += channelName;
                    url += "&userAccount=";
                    url += EMClient.getInstance().getCurrentUser();
                    url += "&appkey=";
                    url += EMClient.getInstance().getOptions().getAppKey();
                    getUserIdAgoraUid(uid, url, callback);
                } else {
                    //设置用户昵称 头像
                    setEaseCallKitUserInfo(userName);
                    EaseUserAccount account = new EaseUserAccount(uid, userName);
                    List<EaseUserAccount> accounts = new ArrayList<>();
                    accounts.add(account);
                    callback.onUserAccount(accounts);
                }
            }
        };
        EaseCallKit.getInstance().setCallKitListener(callKitListener);
    }

    /**
     * 根据channelName和声网uId获取频道内所有人的UserId
     *
     * @param uId
     * @param url
     * @param callback
     */
    private void getUserIdAgoraUid(int uId, String url, EaseGetUserAccountCallback callback) {
        ExecutorManager.getInstance().runOnIOThread(() -> {
            try {
                Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(url, null, EMHttpClient.GET);
                if (response != null) {
                    int resCode = response.first;
                    if (resCode == 200) {
                        String responseInfo = response.second;
                        List<EaseUserAccount> userAccounts = new ArrayList<>();
                        if (responseInfo != null && responseInfo.length() > 0) {
                            try {
                                JSONObject object = new JSONObject(responseInfo);
                                JSONObject resToken = object.getJSONObject("result");
                                Iterator it = resToken.keys();
                                while (it.hasNext()) {
                                    String uIdStr = it.next().toString();
                                    int uid = 0;
                                    uid = Integer.valueOf(uIdStr).intValue();
                                    String username = resToken.optString(uIdStr);
                                    if (uid == uId) {
                                        //获取到当前用户的userName 设置头像昵称等信息
                                        setEaseCallKitUserInfo(username);
                                    }
                                    userAccounts.add(new EaseUserAccount(uid, username));
                                }
                                callback.onUserAccount(userAccounts);
                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                        } else {
                            callback.onSetUserAccountError(response.first, response.second);
                        }
                    } else {
                        callback.onSetUserAccountError(response.first, response.second);
                    }
                } else {
                    callback.onSetUserAccountError(100, "response is null");
                }
            } catch (HyphenateException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * 获取声网Token
     */
    private void getRtcToken(String tokenUrl, EaseCallKitTokenCallback callback) {

        ExecutorManager.getInstance().runOnIOThread(() -> {
            try {
                Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(tokenUrl, null, EMHttpClient.GET);
                if (response != null) {
                    int resCode = response.first;
                    if (resCode == 200) {
                        String responseInfo = response.second;
                        if (responseInfo != null && responseInfo.length() > 0) {
                            try {
                                JSONObject object = new JSONObject(responseInfo);
                                String token = object.getString("data");
                                //int uId = object.getInt("agoraUserId");

                                //设置自己头像昵称
                                setEaseCallKitUserInfo(EMClient.getInstance().getCurrentUser());
                                callback.onSetToken(token, uid);
                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                        } else {
                            callback.onGetTokenError(response.first, response.second);
                        }
                    } else {
                        callback.onGetTokenError(response.first, response.second);
                    }

                } else {
                    callback.onSetToken(null, 0);
                }
            } catch (HyphenateException exception) {
                exception.printStackTrace();
            }
        });

    }

    /**
     * 设置callKit 用户头像昵称
     *
     * @param userName
     */
    private void setEaseCallKitUserInfo(String userName) {
        EaseUser user = getUserInfo(userName);
        EaseCallUserInfo userInfo = new EaseCallUserInfo();
        if (user != null) {
            userInfo.setNickName(user.getNickname());
            userInfo.setHeadImage(user.getAvatar());
        }
        EaseCallKit.getInstance().getCallKitConfig().setUserInfo(userName, userInfo);
    }


    /**
     * 统一配置头像
     *
     * @return
     */
    private EaseAvatarOptions getAvatarOptions() {
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(1);
        return avatarOptions;
    }

    private Map<String, EaseUser> contactList;

    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && (contactList == null || contactList.isEmpty())) {
            updateTimeoutUsers();
            contactList = getModel().getAllUserList();
            //LogUtils.e(GsonUtils.toJson(contactList));
        }

        // return a empty non-null object to avoid app crash
        if (contactList == null) {
            return new Hashtable<String, EaseUser>();
        }
        return contactList;
    }

    public EaseUser getUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return SpManager.getInstance().getCurrentEaseUser();
        /*user = getContactList().get(username);*/
        user = getContactList().get(username);
        if (user == null) {
            //到从服务端异步拉取 然后通知UI刷新列表
            updateContactList();
            user = getContactList().get(username);
            if (user == null && fetchUserInfoList != null) {
                fetchUserInfoList.addUserId(username);
            }
        }
        return user;
    }

    /**
     * update contact list
     */
    public void updateContactList() {
        if (isLoggedIn()) {
            updateTimeoutUsers();
            contactList = appModel.getAllUserList();
        }
    }

    public void logoutSuccess() {
        Log.d(TAG, "logout: onSuccess");
        //setAutoLogin(false);
        DbHelper.getInstance().closeDb();
        //getUserProfileManager().reset();
        EMClient.getInstance().translationManager().logout();
    }

    /**
     * 检查是否是第一次安装登录
     * 默认值是true, 需要在用api拉取完会话列表后，就其置为false.
     *
     * @return
     */
    public boolean isFirstInstall() {
        return getModel().isFirstInstall();
    }

    /**
     * update user list
     *
     * @param users
     */
    public void updateUserList(List<EaseUser> users) {
        getModel().updateContactList(users);
    }
}
