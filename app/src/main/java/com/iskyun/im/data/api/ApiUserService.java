package com.iskyun.im.data.api;

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
import com.iskyun.im.data.bean.PayInfo;
import com.iskyun.im.data.bean.Relation;
import com.iskyun.im.data.bean.Revenue;
import com.iskyun.im.data.bean.Suggest;
import com.iskyun.im.data.bean.SysMessage;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.UserAlbum;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.data.bean.Vip;
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
import com.iskyun.im.data.req.OrderBody;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.data.req.SmsBody;
import com.iskyun.im.data.req.UserBody;
import com.iskyun.im.data.req.WXLoginBody;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.data.resp.ListRecords;
import com.iskyun.im.data.resp.UserModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//用戶接口
public interface ApiUserService {

    /**
     * 一键登录
     *
     * @param token
     * @return
     */
    @GET("common/appUserLogin/oneKeyLogin")
    Call<AppResponse<UserModel>> oneKeyLogin(
            @Query("source") String channel,
            @Query("loginToken") String token);

    /**
     * 微信登录
     *
     * @param loginBody
     * @return
     */
    @POST("common/appUserLogin/loginByWeiXin")
    Call<AppResponse<UserModel>> loginByWeiXin(@Body WXLoginBody loginBody);

    /**
     * 验证码
     *
     * @param smsBody
     * @return
     */
    @POST("common/sms/verificationCode")
    Observable<AppResponse<String>> verificationCode(@Body SmsBody smsBody);

    /**
     * 验证码登录
     *
     * @param loginBody
     * @return
     */
    @POST("common/appUserLogin/login")
    Observable<AppResponse<UserModel>> login(@Body LoginBody loginBody);

    /**
     * 修改用户信息
     *
     * @param userBody
     * @return
     */
    @POST("api/appUser/update")
    Observable<AppResponse<UserBody>> update(@Body UserBody userBody);


    /**
     * 充值VIP列表
     */
    @GET("api/appVipPrice/list")
    Observable<AppResponse<ListRecords<Vip>>> getRechargeVips(@Query("pageNo") int pageNo,
                                                              @Query("pageSize") int pageSize);

    /**
     * 充值钻石列表
     */
    @GET("api/appDiamondPrice/list")
    Observable<AppResponse<ListRecords<Diamond>>> getRechargeDiamonds(@Query("pageNo") int pageNo,
                                                                      @Query("pageSize") int pageSize);

    /**
     * 根据聊天类型扣除钻石
     */
    @POST("api/appUser/deductBalance")
    Call<AppResponse<Consume>> deductBalance(@Body ChatRecordBody body);


    /**
     * 下单
     *
     * @return
     */
    @POST("api/appVipPrice/getOrder")
    Observable<AppResponse<String>> getOrder(@Body OrderBody orderBody);

    /**
     * 微信支付
     *
     * @return
     */
    @GET("api/v3WxPay/appPay")
    Observable<AppResponse<PayInfo>> wxPay(@Query("orderSn") String orderSn);

    /**
     * 发布动态
     *
     * @param body
     * @return
     */
    @POST("api/appUserDynamicState/add")
    Observable<AppResponse<String>> publishDynamic(@Body DynamicBody body);

    /**
     * 动态列表
     *
     * @param userId
     * @return
     */
    @GET("api/appUserDynamicState/getList")
    Observable<AppResponse<ListRecords<Dynamic>>> getDynamics(
            @Query("userId") int userId,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize);

    /**
     * 主播列表
     *
     * @param tag
     * @return
     */
    @GET("api/appUser/listOfAnchors")
    Observable<AppResponse<ListRecords<Anchor>>> getAnchors(
            @Query("tag") int tag,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize);

    /**
     * 轮播图
     */
    @GET("api/ptBanner/list")
    Observable<AppResponse<List<Banner>>> getBanners();

    /**
     * 在线状态修改
     *
     * @return
     */
    @GET("api/appUser/updateUserOnline")
    Observable<AppResponse<User>> updateUserOnline(@Query("tag") int tag);

    @GET("api/appUser/updateUserOnline")
    Call<AppResponse<User>> updateStatusUserOnline(@Query("tag") int tag);


    /**
     * 关注表-添加 relationType：1来访 2关注 3拉黑
     */
    @POST("api/appUserRelation/addUserRelation")
    Observable<AppResponse<String>> addUserRelation(@Body RelationBody body);

    /**
     * 用户关系/关注-解除
     */
    @POST("api/appUserRelation/delAttention")
    Observable<AppResponse<String>> delAttention(@Body RelationBody body);

    /**
     * 点赞
     */
    @POST("api/appUserDynamicState/userInteraction")
    Observable<AppResponse<String>> userInteraction(@Body InteractionBody body);

    /**
     * 查找主播
     */
    @GET("api/appUser/userSearch")
    Observable<AppResponse<Anchor>> findAnchorById(@Query("userId") int userId);

    /**
     * 主播详情
     */
    @GET("api/appUser/userDetails")
    Observable<AppResponse<Anchor>> findAnchorDetailById(@Query("userId") int userId);

    /**
     * 主播详情
     */
    @GET("api/appUser/getWeChatNum")
    Observable<AppResponse<String>> getWeChatNum(@Query("userId") int userId);

    /**
     * 主播认证
     */
    @POST("api/appUserAuth/addUserAuth")
    Observable<AppResponse<String>> anchorAuth(@Body AnchorAuth authBody);

    /**
     * 主播认证详情
     */
    @GET("api/appUserAuth/getMyAuthDetails")
    Observable<AppResponse<AnchorAuth>> getMyAuthDetails();

    /**
     * 来电秀
     *
     * @return
     */
    @POST("api/appUserAuth/saveCallShow")
    Observable<AppResponse<String>> saveCallShow(@Body CallShowBody callShowBody);

    /**
     * 查询用户关系关注
     *
     * @return
     */
    @GET("api/appUserRelation/getRelations")
    Observable<AppResponse<ListRecords<UserRelations>>> getUserRelations(
            @Query("relationType") int relationType,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    /**
     * 我的相册
     *
     * @return
     */
    @POST("api/appUser/userPhotos")
    Observable<AppResponse<List<UserAlbum>>> userPhotos(@Body AlbumBody body);

    /**
     * 绑定手机
     *
     * @return
     */
    @POST("common/appUserLogin/bindPhone")
    Observable<AppResponse<UserModel>> bindPhone(@Body BindPhoneBody body);

    /**
     * 随机用户名
     *
     * @return
     */
    @GET("api/appUser/userRandomName")
    Observable<AppResponse<String>> userRandomName();

    /**
     * 礼物列表
     *
     * @return
     */
    @GET("api/ptGift/list")
    Observable<AppResponse<GiftType>> getGifts();

    /**
     * 赠送礼物
     *
     * @return
     */
    @POST("api/ptGift/getGift")
    Observable<AppResponse<GiveGiftResult>> giveGift(@Body GiveGiftBody body);

    /**
     * 查询我收到的礼物
     *
     * @return
     */
    @GET("api/ptGift/findMyGifts")
    Observable<AppResponse<ListRecords<Gift>>> getFindMyGifts(
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("userId") int userId
    );

    /**
     * 关注数
     */
    @GET("api/appUser/refreshMyAttention")
    Observable<AppResponse<Relation>> refreshMyAttention();

    /**
     * 用户余额查询
     *
     * @return
     */
    @GET("api/appUser/selectBalance")
    Observable<AppResponse<Balance>> selectBalance();

    /**
     * 查询我的通话记录
     *
     * @return
     */
    @GET("api/appCallDuration/list")
    Observable<AppResponse<ListRecords<CallLog>>> getCallLog(
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    /**
     * 添加用户提交问题反馈
     *
     * @param body
     * @return
     */
    @POST("api/appProblemFeedback/add")
    Observable<AppResponse<String>> addComplaint(@Body Complaint body);

    /**
     * 记录
     *
     * @param body
     * @return
     */
    @POST("api/appUser/callRecord")
    Call<AppResponse<String>> callRecord(@Body CallRecordBody body);

    /**
     * 主播评分
     *
     * @param body
     * @return
     */
    @POST("api/appUser/addEvaluate")
    Call<AppResponse<String>> addEvaluate(@Body EvaluateBody body);

    /**
     * 进入进程， 销毁进程时用到
     * <p>
     * 用户登录/初次登录or离线
     *
     * @return
     */
    @GET("api/appUser/userLoginOrLogout")
    Call<AppResponse<String>> userLoginOrLogout(@Query("tag") int tag);


    /**
     * 添加用户建议
     *
     * @param body
     * @return
     */
    @POST("api/appSuggest/addSuggest")
    Observable<AppResponse<String>> addSuggest(@Body Suggest body);


    /**
     * 查询系统消息
     *
     * @return
     */
    @GET("api/sysMessages/list")
    Observable<AppResponse<List<SysMessage>>> getSysMessage();

    /**
     * 黑名单
     *
     * @return
     */
    @GET("api/appUserRelation/blackList")
    Observable<AppResponse<List<Blacklist>>> blacklist();

    /**
     * 收益明细
     *
     * @return
     */
    @GET("api/appUser/getEarningsList")
    Observable<AppResponse<ListRecords<Revenue>>> getRevenueList(
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize
    );

    /**
     * 在线客服
     *
     * @return
     */
    @GET("api/appUser/customerServiceList")
    Observable<AppResponse<ListRecords<OnlineService>>> getOnlineService();

    /**
     * 声网音视频录制
     *
     * @return
     */
    @GET("api/hx/startRecording")
    Call<AppResponse<String>> startRecording(
            @Query("cname") String cname,
            @Query("tag") String tag,
            @Query("fromId") int fromId,
            @Query("toId") int toId
    );

    /**
     * 停止声网音视频录制
     *
     * @return
     */
    @GET("api/hx/stopRecording")
    Call<AppResponse<String>> stopRecording(
            @Query("cname") String cname,
            @Query("tag") String tag,
            @Query("fromId") int fromId,
            @Query("toId") int toId
    );
}
