package com.iskyun.im.data;

import com.iskyun.im.data.api.ApiUserService;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.AnchorAuth;
import com.iskyun.im.data.bean.Balance;
import com.iskyun.im.data.bean.Banner;
import com.iskyun.im.data.bean.Blacklist;
import com.iskyun.im.data.bean.CallLog;
import com.iskyun.im.data.bean.Complaint;
import com.iskyun.im.data.bean.Consume;
import com.iskyun.im.data.bean.Diamond;
import com.iskyun.im.data.bean.Dynamic;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.bean.GiftType;
import com.iskyun.im.data.bean.GiveGiftResult;
import com.iskyun.im.data.bean.OnlineService;
import com.iskyun.im.data.bean.Relation;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.Revenue;
import com.iskyun.im.data.bean.Suggest;
import com.iskyun.im.data.bean.SysMessage;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.UserAlbum;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.data.bean.Vip;
import com.iskyun.im.data.net.RetrofitManager;
import com.iskyun.im.data.req.AlbumBody;
import com.iskyun.im.data.req.BindPhoneBody;
import com.iskyun.im.data.req.CallRecordBody;
import com.iskyun.im.data.req.CallShowBody;
import com.iskyun.im.data.req.ChatRecordBody;
import com.iskyun.im.data.req.DynamicBody;
import com.iskyun.im.data.req.EvaluateBody;
import com.iskyun.im.data.req.GiveGiftBody;
import com.iskyun.im.data.req.InteractionBody;
import com.iskyun.im.data.req.LoginBody;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.data.req.SmsBody;
import com.iskyun.im.data.req.UserBody;
import com.iskyun.im.data.req.WXLoginBody;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.data.resp.ListRecords;
import com.iskyun.im.data.resp.UserModel;
import com.iskyun.im.utils.manager.SpManager;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class UserRepository {

    private ApiUserService userService;
    private volatile static UserRepository INSTANCE;

    private UserRepository() {
        userService = RetrofitManager.get().getApiUserService();
    }

    public static UserRepository get() {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository();
                }
            }
        }
        return INSTANCE;
    }

    public Call<AppResponse<UserModel>> oneKeyLogin(String token) {
        return userService.oneKeyLogin(SpManager.getInstance().getShareTraceChannel(), token);
    }

    public Call<AppResponse<UserModel>> loginByWeiXin(WXLoginBody loginBody) {
        return userService.loginByWeiXin(loginBody);
    }

    public Observable<AppResponse<UserBody>> update(UserBody userBody) {
        return userService.update(userBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<UserModel>> login(LoginBody loginBody) {
        loginBody.setSource(SpManager.getInstance().getShareTraceChannel());
        return userService.login(loginBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<String>> verificationCode(String phone) {
        SmsBody smsBody = new SmsBody();
        smsBody.setPhone(phone);
        smsBody.setType(1);
        return userService.verificationCode(smsBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<ListRecords<Diamond>>> getRechargeDiamonds() {
        return userService.getRechargeDiamonds(1, Integer.MAX_VALUE).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<ListRecords<Vip>>> getRechargeVips() {
        return userService.getRechargeVips(1, Integer.MAX_VALUE).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<AppResponse<ListRecords<Gift>>> getFindMyGifts(int pageNo, int pageSize, int userId) {
        return userService.getFindMyGifts(pageNo, pageSize, userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<String>> publishDynamic(DynamicBody body) {
        return userService.publishDynamic(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<ListRecords<UserRelations>>> getUserRelations(RelationType relationType, int pageNo, int pageSize) {
        return userService.getUserRelations(relationType.getRelationType(), pageNo, pageSize).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<ListRecords<Dynamic>>> getDynamics(int userId, int pageNo, int pageSize) {
        return userService.getDynamics(userId, pageNo, pageSize).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<ListRecords<Anchor>>> getAnchors(int tag, int pageNo, int pageSize) {
        return userService.getAnchors(tag, pageNo, pageSize).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 在线状态修改
     *
     * @param tag
     * @return
     */
    public Observable<AppResponse<User>> updateUserOnline(int tag) {
        return userService.updateUserOnline(tag).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<String>> addUserRelation(RelationBody body) {
        return userService.addUserRelation(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<AppResponse<String>> delAttention(RelationBody body) {
        return userService.delAttention(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<String>> userInteraction(InteractionBody body) {
        return userService.userInteraction(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 查找主播
     *
     * @param userId
     * @return
     */
    public Observable<AppResponse<Anchor>> findAnchorById(int userId) {
        return userService.findAnchorById(userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 主播详情
     *
     * @param userId
     * @return
     */
    public Observable<AppResponse<Anchor>> findAnchorDetailById(int userId) {
        return userService.findAnchorDetailById(userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<AppResponse<String>> anchorAuth(AnchorAuth anchorAuth) {
        return userService.anchorAuth(anchorAuth).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<AnchorAuth>> getMyAuthDetails() {
        return userService.getMyAuthDetails().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 来电秀
     *
     * @return
     */
    public Observable<AppResponse<String>> saveCallShow(CallShowBody callShowBody) {
        return userService.saveCallShow(callShowBody).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<List<UserAlbum>>> userPhotos(AlbumBody body) {
        return userService.userPhotos(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 绑定手机
     *
     * @param body
     * @return
     */
    public Observable<AppResponse<UserModel>> bindPhone(BindPhoneBody body) {
        return userService.bindPhone(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 随机用户名
     *
     * @return
     */
    public Observable<AppResponse<String>> userRandomName() {
        return userService.userRandomName().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 礼物
     */
    public Observable<AppResponse<GiftType>> getGifts() {
        return userService.getGifts().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<GiveGiftResult>> giveGift(GiveGiftBody body) {
        return userService.giveGift(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<String>> getWeChatNum(int userId) {
        return userService.getWeChatNum(userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 修改该用户在线状态
     *
     * @return
     */
    public Call<AppResponse<User>> updateStatusUserOnline(int tag) {
        return userService.updateStatusUserOnline(tag);
    }

    /**
     * 根据聊天类型扣除钻石
     *
     * @param body
     * @return
     */
    public Call<AppResponse<Consume>> deductBalance(ChatRecordBody body) {
        return userService.deductBalance(body);
    }

    /**
     * 关注  粉丝等数据
     *
     * @return
     */
    public Observable<AppResponse<Relation>> refreshMyAttention() {
        return userService.refreshMyAttention().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用户余额
     *
     * @return
     */
    public Observable<AppResponse<Balance>> selectBalance() {
        return userService.selectBalance().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 查询通话记录
     *
     * @return
     */
    public Observable<AppResponse<ListRecords<CallLog>>> getCallLog(int pageNo, int pageSize) {
        return userService.getCallLog(pageNo, pageSize).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 添加用户提交问题反馈
     *
     * @return
     */
    public Observable<AppResponse<String>> addComplaint(Complaint body) {
        return userService.addComplaint(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AppResponse<List<Banner>>> getBanners() {
        return userService.getBanners().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 记录 发送
     *
     * @param body
     * @return
     */
    public Call<AppResponse<String>> callRecord(CallRecordBody body) {
        return userService.callRecord(body);
    }

    /**
     * @return
     */
    public Call<AppResponse<String>> userLoginOrLogout(int tag) {
        return userService.userLoginOrLogout(tag);
    }

    /**
     * 主播评分
     *
     * @return
     */
    public Call<AppResponse<String>> addEvaluate(String recordId, int userId, int score) {
        EvaluateBody body = new EvaluateBody();
        body.setUserId(userId);
        body.setRecordId(recordId);
        body.setScore(score);
        return userService.addEvaluate(body);
    }

    /**
     * 添加用户建议
     *
     * @return
     */
    public Observable<AppResponse<String>> addSuggest(Suggest body) {
        return userService.addSuggest(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 查询系统消息
     *
     * @return
     */
    public Observable<AppResponse<List<SysMessage>>> getSysMessage() {
        return userService.getSysMessage().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 黑名单
     *
     * @return
     */
    public Observable<AppResponse<List<Blacklist>>> blacklist() {
        return userService.blacklist().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收益明细
     *
     * @return
     */
    public Observable<AppResponse<ListRecords<Revenue>>> getRevenueList(int pageNum, int pageSize) {
        return userService.getRevenueList(pageNum, pageSize).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 在线客服
     *
     * @return
     */
    public Observable<AppResponse<ListRecords<OnlineService>>> getOnlineService() {
        return userService.getOnlineService().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 声网音视频录制
     *
     * @return
     */
    public Call<AppResponse<String>> startRecording(String cname, String tag, int fromId, int toId) {
        return userService.startRecording(cname, tag, fromId, toId);
    }

    /**
     * 声网音视频录制
     *
     * @return
     */
    public Call<AppResponse<String>> stopRecording(String cname, String tag, int fromId, int toId) {
        return userService.stopRecording(cname, tag, fromId, toId);
    }

}
