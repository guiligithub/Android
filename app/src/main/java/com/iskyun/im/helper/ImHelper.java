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
     * ??????IM SDK????????????
     *
     * @return
     */
    public EMClient getEMClient() {
        return EMClient.getInstance();
    }

    public void init(Context context) {
        appModel = new AppModel(context);
        //?????????IM SDK
        if (initSDK(context)) {
            // debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);
            // set Call options
            setCallOptions(context);
            //???????????????
            initPush(context);
            //??????call Receiver
//            initReceiver(context);
            //?????????ease ui??????
            initEaseUI(context);
            //??????????????????
            registerConversationType();
            //callKit?????????
            InitCallKit(context);

            //??????????????????????????????
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
     * ???????????????????????????
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
     * ?????????SDK
     *
     * @param context
     * @return
     */
    private boolean initSDK(Context context) {
        // ?????????????????????SDK????????????
        EMOptions options = initChatOptions(context);
        //??????????????????rest server???im server
//        options.setRestServer("a1-hsb.easemob.com");
//        options.setIMServer("106.75.100.247");
//        options.setImPort(6717);

//        options.setRestServer("a41.easemob.com");
//        options.setIMServer("msync-im-41-tls-test.easemob.com");
//        options.setImPort(6717);
        // ?????????SDK
        isSDKInit = EaseIM.getInstance().init(context, options);
        //??????????????????????????????????????????
        //appModel.setUserInfoTimeOut(30 * 60 * 1000);
        //??????????????????????????????
        updateTimeoutUsers();
        mainContext = context;
        return isSDKInit();
    }

    public boolean isSDKInit() {
        return isSDKInit;
    }

    public void initPush(Context context) {
//        if(EaseIM.getInstance().isMainProcess(context)) {
//            //OPPO SDK?????????2.1.0????????????????????????
//            HeytapPushManager.init(context, true);
//            //HMSPushHelper.getInstance().initHMSAgent(DemoApplication.getInstance());
//            EMPushHelper.getInstance().setPushListener(new PushListener() {
//                @Override
//                public void onError(EMPushType pushType, long errorCode) {
//                    // TODO: ?????????errorCode???9xx??????????????????????????????EMError?????????????????????????????????pushType???????????????????????????????????????
//                    EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
//                }
//
//                @Override
//                public boolean isSupportPush(EMPushType pushType, EMPushConfig pushConfig) {
//                    // ?????????????????????????????????????????????FCM??????
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
     * ChatPresenter????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param context
     */
    private void initEaseUI(Context context) {
        //??????ChatPresenter,ChatPresenter???????????????????????????????????????
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
        //????????????????????????
        callKitConfig.setCallTimeOut(30 * 1000);
        //?????????????????????
//        String ringFile = EaseFileUtils.getModelFilePath(context,"huahai.mp3");
//        callKitConfig.setRingFile(ringFile);
        //????????????AgoraAppId
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
     * ??????????????????
     */
    private void registerConversationType() {
        EaseMessageTypeSetManager.getInstance()
                .addMessageType(EaseExpressionAdapterDelegate.class)       //???????????????
//                .addMessageType(EaseFileAdapterDelegate.class)             //??????
                .addMessageType(EaseImageAdapterDelegate.class)            //??????
//                .addMessageType(EaseLocationAdapterDelegate.class)         //??????
//                .addMessageType(EaseVideoAdapterDelegate.class)            //??????
                .addMessageType(EaseVoiceAdapterDelegate.class)            //??????
//                .addMessageType(ChatConferenceInviteAdapterDelegate.class) //????????????
                .addMessageType(ChatRecallAdapterDelegate.class)           //????????????
                .addMessageType(ChatVideoCallAdapterDelegate.class)        //????????????
                .addMessageType(ChatVoiceCallAdapterDelegate.class)        //????????????
//                .addMessageType(ChatUserCardAdapterDelegate.class)         //????????????
                .addMessageType(ChatGiftAdapterDelegate.class)     //????????????
                .addMessageType(EaseCustomAdapterDelegate.class)           //???????????????
//                .addMessageType(ChatNotificationAdapterDelegate.class)     //?????????????????????
                .setDefaultMessageType(EaseTextAdapterDelegate.class);       //??????
    }

    /**
     * ?????????????????????????????????
     *
     * @param context
     * @return
     */
    private EMOptions initChatOptions(Context context) {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();

        /**
         * NOTE:????????????????????????????????????????????????????????????????????????????????????
         */
        EMPushConfig.Builder builder = new EMPushConfig.Builder(context);

//        builder
////                .enableVivoPush() // ?????????AndroidManifest.xml?????????appId???appKey
//                .enableMeiZuPush("134952", "f00e7e8499a549e09731a60a4da399e3")
//                .enableMiPush("2882303761517426801", "5381742660801")
//                .enableOppoPush("0bb597c5e9234f3ab9f821adbeceecdb",
//                        "cd93056d03e1418eaa6c3faf10fd7537")
//                .enableHWPush() // ?????????AndroidManifest.xml?????????appId
//                .enableFCM("782795210914");
//        options.setPushConfig(builder.build());

        //set custom servers, commonly used in private deployment


        // ????????????(?????????????????????)?????????????????????????????????
        //options.setDeleteMessagesAsExitGroup(appModel.isDeleteMessagesAsExitGroup());
        // ???????????????????????????????????????????????????????????????True????????????????????????????????????
        //options.setAutoTransferMessageAttachments(appModel.isSetTransferFileByUser());
        // ???????????????????????????????????????true???????????????
        //options.setAutoDownloadThumbnail(appModel.isSetAutodownloadThumbnail());
        return options;
    }

    /**
     * ?????????????????????????????????
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
     * ??????EaseCallkit??????
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
                //????????????????????????
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
                //????????????Token
                getRtcToken(url, callback);
            }

            @Override
            public void onReceivedCall(EaseCallType callType, String fromUserId, JSONObject ext) {
                //??????????????????
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
                    //?????????????????? ??????
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
     * ??????channelName?????????uId???????????????????????????UserId
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
                                        //????????????????????????userName ???????????????????????????
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
     * ????????????Token
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

                                //????????????????????????
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
     * ??????callKit ??????????????????
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
     * ??????????????????
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
            //??????????????????????????? ????????????UI????????????
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
     * ????????????????????????????????????
     * ????????????true, ????????????api???????????????????????????????????????false.
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
