package net.monkeystudio.wx.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.chatrbtw.sdk.wx.WxPubHelper;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.chatrbtw.service.bean.asksearch.AskSearchVo;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.CompleteMissionParam;
import net.monkeystudio.chatrbtw.service.bean.chatrobot.resp.ChatRobotInfoResp;
import net.monkeystudio.chatrbtw.service.bean.wxmessage.ReplyMessage;
import net.monkeystudio.wx.controller.bean.TextMsgRec;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
import net.monkeystudio.wx.vo.thirtparty.AuthorizerInfo;
import net.monkeystudio.wx.vo.thirtparty.PubBaseInfo;
import net.monkeystudio.wx.vo.thirtparty.WxThirtPartAuthorizationResp;
import net.monkeystudio.wx.vo.thirtparty.WxThirtyPartauthorizerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信文本消息处理
 * Created by bint on 2017/12/1.
 */
@Service
public class WxTextMessageHandler extends WxBaseMessageHandler{
    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private AdService adService;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private ChatLogService chatLogService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private KeywordResponseService keywordResponseService;

    @Autowired
    private WxCustomerHelper wxCustomerHelper;

    @Autowired
    private ResponseProcessService responseProcessService;

    @Autowired
    private TuLingInterService tuLingInterService;

    @Autowired
    private WxAuthApiService wxAuthApiService;

    @Autowired
    private AdPushService adPushService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private PushMessageConfigService pushMessageConfigService;

    @Autowired
    private WxChatCountService wxChatCountService;

    @Autowired
    private ChatRobotService chatRobotService;

    @Autowired
    private WxPubHelper wxPubHelper;

    @Autowired
    private RWxPubProductService rWxPubProductService;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private CfgService cfgService;

    @Autowired
    private AskSearchService askSearchService;

    private final static int REPLY_TYPE_NONE = -1;//不做回复类型
    private final static int REPLY_TYPE_TEST = 0;//测试类型
    private final static int REPLY_TYPE_NEED_TO_FILTER = 1;//需要过滤的类型
    private final static int REPLY_TYPE_WX_PUB_REPLY_KEYWORD = 2;//微信公众号关键字
    private final static int REPLY_TYPE_WX_PUB_CUSTOM_KEYWORDS = 3;//金豆平台上的公众号自定义关键字
    private final static int REPLY_TYPE_WX_MATERIAL = 4;//微信素材回复
    private final static int REPLY_TYPE_BASE_KEYWORDS = 5; //公众关键字回复
    private final static int REPLY_TYPE_TU_LING = 6;  //图灵关键字回复

    private final static int REPLY_MSG_TYPE_TEXT = 1;//文本回复类型
    private final static int REPLY_MSG_TYPE_ARTICLES = 2;//图文回复类型


    private final static Integer  PUSH_TASK_AD_CHAT_COUNT = 3;//聊天次数到达推送陪聊宠任务广告


    @Autowired
    private WxMaterialMgrService wxMaterialMgrService;

    //统计周期
    private final static Integer COUNT_CACHE_PERIOD = 24 * 60 * 60;


    //图灵机器人名字
    private final static String TU_LING_ROBOT_NAME =  "keendooo";

    private final static String[] IGNORE_TEX_MESSAGE_ARRAY = {
            "【收到不支持的消息类型，暂无法显示】",
            "【收到不支援的訊息類型，無法顯示】"
    };

    public String handleTextMsg(String body) throws BizException{
        TextMsgRec textMsgRec = XmlUtil.converyToJavaBean(body, TextMsgRec.class);
        return this.textMsgRecHandle(textMsgRec);
    }

    /**
     * 文本消息接收处理
     * @param textMsgRec
     * @return
     * @throws BizException
     */
    public String textMsgRecHandle(TextMsgRec textMsgRec) throws BizException{
        if (textMsgRec == null) {
            return "parse body error";
        } else if (textMsgRec.getContent() == null) {
            return "content empty.";
        }

        String wxFanOpenId = textMsgRec.getFromUserName(); //用户openId
        String wxPubOriginId = textMsgRec.getToUserName(); //公众号 originId

        Integer responseType = this.getRespType(textMsgRec);

        //粉丝信息的处理
        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);

        String respStr = null;

        ReplyMessage replyMessage = this.reply(responseType, textMsgRec);


        //如果返回的是空白就返回空，让上层做处理
        if (replyMessage == null || "".equals(replyMessage.getObject()) ) {
            return null;
        }

        //开通宠物陪聊,不走智能聊
        if(rWxPubProductService.isEnable(ProductService.CHAT_PET, wxPubOriginId)){
            if(chatPetService.isFansOwnChatPet(wxPubOriginId,wxFanOpenId)){

                //第一次聊天填充任务池
                chatPetMissionPoolService.createMissionWhenFirstChatOrComeH5(wxPubOriginId,wxFanOpenId);

                //完成陪聊宠每日签到任务
                ChatPet chatPet = chatPetService.getChatPetByFans(wxPubOriginId, wxFanOpenId);

                CompleteMissionParam param = new CompleteMissionParam();
                param.setMissionCode(ChatPetMissionEnumService.DAILY_CHAT_MISSION_CODE);
                param.setChatPetId(chatPet.getId());

                chatPetMissionPoolService.completeChatPetMission(param);


                //资讯任务广告推送
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            petChatAdProcess(chatPet.getId(),wxPubOriginId,wxFanOpenId);
                        } catch (BizException e){
                            Log.e(e);
                        }
                    }
                });
            }

        }else{
            this.smartChatAdProecess(wxPubOriginId,wxFanOpenId);
        }

        Integer replyMsgType  = replyMessage.getReplyMsgType();

        Integer chatLogId = replyMessage.getChatLogId();

        String replySource = replyMessage.getReplySource();

        switch (replyMsgType){
            //文本消息回复
            case REPLY_MSG_TYPE_TEXT:
                respStr = (String) replyMessage.getObject();
                respStr = responseProcessService.responseProecess(wxPubOriginId, respStr);

                String respXml = this.replyTextStr(wxPubOriginId, wxFanOpenId, respStr);

                try {
                    chatLogService.saveResponse(wxPubOriginId, wxFanOpenId, respStr , chatLogId, replySource);
                    return respXml;
                } catch (Exception e) {
                    Log.e(e);
                    return null;
                }
            //图文消息回复
            case REPLY_MSG_TYPE_ARTICLES:
                List<CustomerNewsItem> customerNewsList = (List<CustomerNewsItem>) replyMessage.getObject();
                String respNewsXml = this.replyNewsStr(wxPubOriginId, wxFanOpenId, customerNewsList);

                try {
                    this.saveCustomerNewsLog(wxPubOriginId, wxFanOpenId, customerNewsList , chatLogId, replySource);
                    return respNewsXml;
                } catch (Exception e) {
                    Log.e(e);
                    return null;
                }
        }

        return null;

    }


    private ReplyMessage reply(Integer responseType , TextMsgRec textMsgRec){

        String wxPubOriginId = textMsgRec.getToUserName();
        String wxFanOpenId = textMsgRec.getFromUserName();

        ReplyMessage replyMessage = new ReplyMessage();

        //日志记录的id
        Integer chatLogId = null;
        //回复的来源
        String replySrc = null;
        //回复的样式
        Integer replyMsgType = REPLY_MSG_TYPE_TEXT;

        //回复的内容
        Object reployCotent = null;

        switch (responseType.intValue()){

            //过滤掉的类型
            case REPLY_TYPE_NEED_TO_FILTER :
                return null;

            //微信关键字的类型
            case REPLY_TYPE_WX_PUB_REPLY_KEYWORD:
                return null;

            //测试类型
            case REPLY_TYPE_TEST:
                chatLogId = chatLogService.saveReceive(wxPubOriginId, textMsgRec.getFromUserName(), textMsgRec.getContent());
                reployCotent = this.wxTestReply(textMsgRec, chatLogId);

            //金豆公众号关键字回复
            case REPLY_TYPE_WX_PUB_CUSTOM_KEYWORDS:
                chatLogId = chatLogService.saveReceive(wxPubOriginId, textMsgRec.getFromUserName(), textMsgRec.getContent());

                KrResponse krResponse = keywordResponseService.getWxPubResponse(textMsgRec.getContent(), wxPubOriginId);
                replySrc = "BK";
                if (krResponse.getType().equals("text")) {
                    //文本消息
                    reployCotent = krResponse.getResponse();
                } else if (krResponse.getType().equals("mpnews")) {
                    reployCotent = "";
                }
                break;

            //问问搜回复
            case REPLY_TYPE_WX_MATERIAL:
                chatLogId = chatLogService.saveReceive(wxPubOriginId, textMsgRec.getFromUserName(), textMsgRec.getContent());

                replySrc = "N";
                reployCotent = askSearchService.getAskSearchNews(textMsgRec);
                replyMsgType = REPLY_MSG_TYPE_ARTICLES;//问问搜回复图文消息
                break;

            //金豆公众号公众关键字回复
            case REPLY_TYPE_BASE_KEYWORDS:
                chatLogId = chatLogService.saveReceive(wxPubOriginId, textMsgRec.getFromUserName(), textMsgRec.getContent());

                KrResponse baseWxPubKrResponse = keywordResponseService.getBaseResponse(textMsgRec.getContent());
                replySrc = "BK";
                if (baseWxPubKrResponse.getType().equals("text")) {
                    //文本消息
                    reployCotent = baseWxPubKrResponse.getResponse();
                } else if (baseWxPubKrResponse.getType().equals("mpnews")) {
                    reployCotent = "";
                }
                break;

            //图灵回复
            case REPLY_TYPE_TU_LING:
                chatLogId = chatLogService.saveReceive(wxPubOriginId, textMsgRec.getFromUserName(), textMsgRec.getContent());

                replySrc = "T";

                Integer chatUid = AppConstants.CHAT_UID_DEFAULT;

                WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
                if (wxFan != null) {
                    Log.d("wxFan:id=" + wxFan.getId() + ",openid=" + wxFan.getWxFanOpenId());
                    chatUid = wxFan.getId();
                }

                String respStr = tuLingInterService.getResponse(chatUid, textMsgRec.getContent());

                if(respStr.indexOf(TU_LING_ROBOT_NAME) != -1){
                    ChatRobotInfoResp chatRobotInfoResp = chatRobotService.getChatRobotInfoRespByWxPubOriginId(wxPubOriginId);

                    if(chatRobotInfoResp != null && chatRobotInfoResp.getNickname() != null){
                        respStr = respStr.replace(TU_LING_ROBOT_NAME,chatRobotInfoResp.getNickname());
                    }
                }

                reployCotent = respStr;
                break;

            case REPLY_TYPE_NONE:
                return null;
        }

        replyMessage.setChatLogId(chatLogId);
        replyMessage.setReplyMsgType(replyMsgType);
        replyMessage.setReplySource(replySrc);

        replyMessage.setObject(reployCotent);

        return replyMessage;
    }

    //推送任务广告
    public String pushTaskAdChatCountKey(String wxPubOriginId,String wxFanOpenId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "PushTaskAdChatCount:" + wxPubOriginId + ":" + wxFanOpenId;
    }

    /**
     * 陪聊宠广告推送
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @throws BizException
     */
    /*private void petChatAdProcess(String wxPubOriginId,String wxFanOpenId) throws BizException {
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        if(wxPub != null ){
            Integer wxPubVerifyTypeInfo = wxPub.getVerifyTypeInfo();
            if(wxPubVerifyTypeInfo  == null){
                this.reviseWxPub(wxPubOriginId);
                wxPub = wxPubService.getByOrginId(wxPubOriginId);
            }

            if( rWxPubProductService.isUnable(ProductService.CHAT_PET,wxPubOriginId)){
                return ;
            }

            String pushTaskAdChatCountKey = this.pushTaskAdChatCountKey(wxPubOriginId,wxFanOpenId);
            Long count = redisCacheTemplate.incr(pushTaskAdChatCountKey);

            if (count == 1) {
                //缓存时间:当前时间距离次日0点的时间
                redisCacheTemplate.expire(pushTaskAdChatCountKey, DateUtils.getCacheSeconds());
            }

            if(wxPubVerifyTypeInfo.intValue() == wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY){
                WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
                if(wxFan != null){
                    Log.d("============= smart chat ad has been pushed !! ============");
                    this.sendAd(wxFan.getId(), wxPubOriginId,adService.AD_PUSH_TYPE_CHAT_PET);
                }
            }

            *//*if(PUSH_TASK_AD_CHAT_COUNT.equals(count.intValue())){
                WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
                if(wxFan != null){
                    Log.d("============= smart chat ad has been pushed !! ============");
                    this.sendAd(wxFan.getId(), wxPubOriginId,adService.AD_PUSH_TYPE_CHAT_PET);
                }
            }*//*

        }
    }*/

    /**
     * 陪聊宠:获取当日指定公众号下指定粉丝的聊天次数
     * @param wxPubOriginId
     * @param wxFanId
     * @return
     */
    public Integer getChatCount4ChatPetAd(String wxPubOriginId,Integer wxFanId){
        WxFan wxFan = wxFanService.getById(wxFanId);

        String pushTaskAdChatCountKey = this.pushTaskAdChatCountKey(wxPubOriginId,wxFan.getWxFanOpenId());

        String count = redisCacheTemplate.getString(pushTaskAdChatCountKey);

        return Integer.valueOf(count);
    }


    /**
     * 智能聊广告处理
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @throws BizException
     */
    private void smartChatAdProecess(String wxPubOriginId , String wxFanOpenId) throws BizException {
        Log.d("===================method :' smartChatAdProcess ' has been used ! ====================");
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        if(wxPub != null ){
            Integer wxPubVerifyTypeInfo = wxPub.getVerifyTypeInfo();
            if(wxPubVerifyTypeInfo  == null){
                this.reviseWxPub(wxPubOriginId);
                wxPub = wxPubService.getByOrginId(wxPubOriginId);
            }

            if( rWxPubProductService.isUnable(ProductService.SMART_CHAT,wxPubOriginId)){
                return ;
            }

            String countCacheKey = this.getChatLogCountCacheKey(wxPubOriginId, wxFanOpenId);
            Long count = redisCacheTemplate.incr(countCacheKey);


            if (count == 1) {
                redisCacheTemplate.expire(countCacheKey, COUNT_CACHE_PERIOD);
            }

            if(wxPubVerifyTypeInfo.intValue() == wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY){
                WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
                if(wxFan != null){
                    Log.d("============= smart chat ad has been pushed !! ============");
                    this.sendAd(wxFan.getId(), wxPubOriginId,adService.AD_PUSH_TYPE_SMART_CHAT);
                }
            }

            /*String chatAdPushCountStr = pushMessageConfigService.getByKey(PushMessageConfigService.CHAT_PUSH_AD_COUNT_KEY);
            if (chatAdPushCountStr != null) {
                Long chatAdPushCount = Long.valueOf(chatAdPushCountStr);
                //如果刚好到达次数，则触发推送广告机制
                if (count.longValue() == chatAdPushCount.longValue()) {
                    if(wxPubVerifyTypeInfo.intValue() == wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY){
                        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
                        if(wxFan != null){
                            Log.d("============= smart chat ad has been pushed !! ============");
                            this.sendAd(wxFan.getId(), wxPubOriginId,adService.AD_PUSH_TYPE_SMART_CHAT);
                        }
                    }
                }
            }*/
        }
    }

    /**
     * 陪聊宠广告推送
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @throws BizException
     */
    private void petChatAdProcess(Integer chatPetId,String wxPubOriginId,String wxFanOpenId) throws BizException {
        Log.d("============= chatPetMissionAdProcess =============");
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        if(wxPub == null){
            return;
        }

        Integer wxPubVerifyTypeInfo = wxPub.getVerifyTypeInfo();
        if(wxPubVerifyTypeInfo  == null){
            this.reviseWxPub(wxPubOriginId);
            wxPub = wxPubService.getByOrginId(wxPubOriginId);
        }

        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
        if(wxFan == null){
            return;
        }

        if(wxPubVerifyTypeInfo.intValue() != wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY){
            return ;
        }

        //获取给粉丝派发的资讯任务
        ChatPetPersonalMission ongoingSearchNewMission = chatPetMissionPoolService.getOngoingMissionByMissionCodeAndChatPetId(chatPetId, ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);
        if(ongoingSearchNewMission == null){
            return;
        }
        //获取资讯任务广告id
        Integer adId = ongoingSearchNewMission.getAdId();
        Log.d("===============资讯任务广告id = {?}============",adId.toString());
        Ad ad = adService.getAdById(adId);

        //若未完成任务继续挖矿会获取到同一条咨询广告,此时不再推送
        Boolean isPushed = adPushService.isAdHasPushedWxFanId(wxFan.getId(), adId);
        if(isPushed){
            return;
        }

        //根据触发策略判断是否触发推送
        boolean need2Push = adPushService.isAdNeed2PushByStrategyType(ad.getPushStrategyType(), wxFan.getId(), ad.getPushType());
        if(!need2Push){
            return;
        }

        //到达触发广告机制次数的人数+1
        wxChatCountService.incrArrivalCount();

        //推送广告
        adPushService.pushAdHandle(ad,wxFan.getId());
    }

    //同一条广告推送给一个用户的
    //private String getCount

    /**
     * 智能聊:获取粉丝聊天次数
     * @param wxPubOriginId
     * @param wxFanId
     * @return
     */
    public Integer getChatCount4SmartChatAd(String wxPubOriginId,Integer wxFanId){
        WxFan wxFan = wxFanService.getById(wxFanId);
        String countCacheKey = this.getChatLogCountCacheKey(wxPubOriginId, wxFan.getWxFanOpenId());
        String count = redisCacheTemplate.getString(countCacheKey);
        return Integer.valueOf(count);
    }



    private Integer getRespType(TextMsgRec textMsgRec) throws BizException {

        String wxFanOpenId = textMsgRec.getFromUserName(); //用户openId
        String wxPubOriginId = textMsgRec.getToUserName(); //公众号 originId
        String content = textMsgRec.getContent();

        //测试回复类型
        if ("gh_3c884a361561".equals(wxPubOriginId)) {
            return REPLY_TYPE_TEST;
        }

        //微信自动回复类型
        String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);//在redis里面获取 appid
        if(wxPubService.isAuotReplyKeyword(content, wxPubAppId)){
            return REPLY_TYPE_WX_PUB_REPLY_KEYWORD;
        }

        Boolean smartChatEnable = rWxPubProductService.isEnable(ProductService.SMART_CHAT, wxPubOriginId);
        if(smartChatEnable){
            //过滤忽略类型
            if(needFiltered(content)){
                return REPLY_TYPE_NEED_TO_FILTER;
            }

            //金豆上的公众号自定义关键字回复
            KrResponse krResponse = keywordResponseService.getWxPubResponse(textMsgRec.getContent(), wxPubOriginId);
            if(krResponse != null){
                return REPLY_TYPE_WX_PUB_CUSTOM_KEYWORDS;
            }
        }

        //问问搜启用
        if(rWxPubProductService.isEnable(ProductService.ASK_SEARCH,wxPubOriginId)){

            //回复更多,推下一批素材
            if("更多".equals(StringUtils.trimWhitespace(content))){

                Log.d("############# chat type = wenwensou -> more news  ###############");

                return REPLY_TYPE_WX_MATERIAL;
            }
            //素材回复
            List<WxPubNews> wxPubNews = wxMaterialMgrService.getWxPubNews(wxPubOriginId, content, 1);
            if(ListUtil.isNotEmpty(wxPubNews)){
                return REPLY_TYPE_WX_MATERIAL;
            }
        }


        if(smartChatEnable){
            //公众关键字回复
            KrResponse baseKrResponse = keywordResponseService.getBaseResponse(textMsgRec.getContent());
            if(baseKrResponse != null){
                return REPLY_TYPE_BASE_KEYWORDS;
            }

            //图灵回复
            return REPLY_TYPE_TU_LING;

        }

        Boolean chatPetEnable = rWxPubProductService.isEnable(ProductService.CHAT_PET, wxPubOriginId);
        if(chatPetEnable){
            //图灵回复
            return REPLY_TYPE_TU_LING;
        }

        return REPLY_TYPE_NONE;

    }


    /**
     * 修正微信公众号信息
     * @param wxPubOriginId
     */
    public void reviseWxPub(String wxPubOriginId) throws BizException {

        PubBaseInfo pubBaseInfo = wxPubHelper.fetchPubBaseInfo(wxPubOriginId);
        AuthorizerInfo authorizerInfo = pubBaseInfo.getAuthorizerInfo();

        String headImageUrl = authorizerInfo.getHeadImg();
        String verifyTypeInfo = authorizerInfo.getVerifyTypeInfo().getId();

        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        wxPub.setVerifyTypeInfo(Integer.parseInt(verifyTypeInfo));
        wxPub.setHeadImgUrl(headImageUrl);

        //如果机器人昵称为空，则设置为微信公众号昵称
        if(wxPub.getChatbotName() == null){
            wxPub.setChatbotName(authorizerInfo.getNickName());
        }

        wxPubService.update(wxPub);
    }


    private void sendAd(Integer wxFanId , String wxPubOriginId,Integer pushType){

        //到达触发广告机制次数的人数+1
        wxChatCountService.incrArrivalCount();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {

                Log.i("ad time : wxPubOriginId [?] ,WxFanId [?] ", wxPubOriginId, String.valueOf(wxFanId));

                try {
                    adPushService.randomPush(wxPubOriginId, wxFanId,pushType);
                } catch (BizException e) {
                    Log.e(e);
                }
                /*if (adPushService.getPushAdhSwith()) {
                    Log.i("ad time : wxPubOriginId [?] ,WxFanId [?] ", wxPubOriginId, String.valueOf(wxFanId));

                    try {
                        adPushService.randomPush(wxPubAppId, wxFanId,pushType);
                    } catch (BizException e) {
                        Log.e(e);
                    }
                }*/
            }
        });
    }

    /**
     * 判断是否需要被过滤
     * @param text
     * @return
     */
    private Boolean needFiltered(String text) {

        //是否为忽略的语句
        for (int i = 0; i < IGNORE_TEX_MESSAGE_ARRAY.length; i++) {

            String messageItem = IGNORE_TEX_MESSAGE_ARRAY[i];

            if (messageItem.equals(text)) {
                return true;
            }
        }

        if (text.length() == 1) {

            //如果是数字
            if (text.matches("[0-9]{1,}")) {
                return true;
            }

            //如果是字母
            char c = text.charAt(0);
            if (Character.isUpperCase(c)) {
                return true;
            }
            if (Character.isLowerCase(c)) {
                return true;
            }
        }

        return false;
    }


    /*private ReplyMessage askSearchHandle(TextMsgRec textMsgRec ,Integer requestChatLogId){

        String wxPubOriginId = textMsgRec.getToUserName();
        String wxfanOpenId = textMsgRec.getFromUserName();
        String requestContent = textMsgRec.getContent();

        List<AskSearchVo> askSearchVoList = askSearchService.getAskSearchContent(textMsgRec);


        if(askSearchVoList == null){
            return null;
        }

        //记录日志
        StringBuffer chatLogSb = new StringBuffer();
        for(int i = 0; i < askSearchVoList.size(); i++){
            if(i == 1){
                chatLogSb.append(askSearchVoList.get(i).getTitle());
            }else{
                chatLogSb.append("\n");
                chatLogSb.append(askSearchVoList.get(i).getTitle());
            }
        }
        Integer responseChatLogId = chatLogService.saveResponse(wxPubOriginId, wxfanOpenId, chatLogSb.toString(), requestChatLogId, "N");

        if(askSearchService.newsAnble()){
            ReplyMessage replyMessage = new ReplyMessage();

            replyMessage.setReplySource("N");
            replyMessage.setChatLogId(responseChatLogId);
            replyMessage.setReplyMsgType(REPLY_MSG_TYPE_ARTICLES);

            List<CustomerNewsItem> customerNewsList = this.convertCustomerNews(askSearchVoList);
            replyMessage.setObject(customerNewsList);
            return replyMessage;
        }

        return null;
    }*/


    private Integer saveCustomerNewsLog(String wxPubOriginId,String wxfanOpenId, List<CustomerNewsItem> customerNewsList ,Integer requestChatLogId ,String replySource){

        StringBuffer chatLogSb = new StringBuffer();
        for(int i = 0; i < customerNewsList.size(); i++){
            //排除首尾
            if(i == 0 || i == customerNewsList.size()-1){
                continue;
            }else if(i == 1){
                chatLogSb.append(customerNewsList.get(i).getTitle());
            }else{
                chatLogSb.append("\n");
                chatLogSb.append(customerNewsList.get(i).getTitle());
            }
        }

        Integer responseChatLogId = chatLogService.saveResponse(wxPubOriginId, wxfanOpenId, chatLogSb.toString(), requestChatLogId, replySource);
        return responseChatLogId;
    }

    /**
     * 获取问问搜的回复的内容
     * @param textMsgRec
     * @return
     */
    public  List<AskSearchVo> getAskSearchContent(TextMsgRec textMsgRec){
        return null;

        /*String content = textMsgRec.getContent();
        String wxPubOriginId = textMsgRec.getToUserName();
        String wxfanOpenId = textMsgRec.getFromUserName();


        List<WxPubNews> wxPubNewsList = null;
        Integer currentPage = null;

        //获取素材的内容
        if(isMoreWord(content)){

            //TODO 用getMoreNews方法获取
            // "更多"从缓存中获取
            String lastContent = this.getLastContentFromCache(wxPubOriginId, wxfanOpenId);

            if(lastContent == null){
               return null;
            }

            Long currentPageLong = this.incrWordCacheCount(wxPubOriginId,wxfanOpenId);
            currentPage = currentPageLong.intValue();

            QueryWxPubNewsList qo = new QueryWxPubNewsList();
            qo.setTitle(lastContent);
            qo.setWxPubOriginId(wxPubOriginId);
            qo.setPage(currentPage.intValue());
            qo.setPageSize(WX_PUB_ARTICLE_PUSH_COUNT);
            wxPubNewsList = wxMaterialMgrService.getWxPubNewsList(qo.getMap());



        }else {

            //非更多的处理
            this.resetFirstPage(wxPubOriginId,wxfanOpenId);
            this.setWordCache(wxPubOriginId,wxfanOpenId,content);
            currentPage = 1;

            //从第一页查询
            QueryWxPubNewsList qo = new QueryWxPubNewsList();
            qo.setTitle(content);
            qo.setWxPubOriginId(wxPubOriginId);
            qo.setPage(currentPage);
            qo.setPageSize(WX_PUB_ARTICLE_PUSH_COUNT);
            wxPubNewsList = wxMaterialMgrService.getWxPubNewsList(qo.getMap());

        }

        List<AskSearchVo> askSearchVoList = new ArrayList<>();

        //第一条为"XXX关键字"的提醒
        String lastContent = this.getLastContentFromCache(wxPubOriginId, wxfanOpenId);
        AskSearchVo firstItem = this.getFirstItem(lastContent);
        askSearchVoList.add(firstItem);

        List<AskSearchVo> askSearchVoFromNews = convert2AskSearchList(wxPubNewsList);
        askSearchVoList.addAll(askSearchVoFromNews);

        //最后一条为提示是否还有"更多"
        AskSearchVo finalItem = this.getFinalItem(wxPubOriginId, wxfanOpenId);
        if(finalItem != null){
            askSearchVoList.add(finalItem);
        }

        //获取问问搜广告
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);
        Ad ad = adService.getAskSearchPushAd(wxPub);

        //当前页数为1且为空
        if(currentPage == 1 && ad != null){
            //插入广告
            for(int i=0;i<askSearchVoList.size();i++){

                AskSearchVo askSearchVo = askSearchVoList.get(i);

                if(i==1){
                    AskSearchVo adAaskSearchVo = new AskSearchVo();

                    adAaskSearchVo.setTitle(ad.getTitle());
                    adAaskSearchVo.setUrl(ad.getUrl());
                    adAaskSearchVo.setPicUrl(ad.getPicUrl());

                    askSearchVoList.add(adAaskSearchVo);
                }

                askSearchVoList.add(askSearchVo);
            }
        }

        return askSearchVoList;*/

        /*StringBuffer chatLogSb = new StringBuffer();
        for(int i = 0; i < wxPubNewsListResult.size(); i++){
            if(i == 1){
                chatLogSb.append(wxPubNewsListResult.get(i).getTitle());
            }else{
                chatLogSb.append("\n");
                chatLogSb.append(wxPubNewsListResult.get(i).getTitle());
            }
        }*/


        //chatLogService.saveResponse(wxPubOriginId, wxfanOpenId, chatLogSb.toString() , chatLogId);


        /*ReplyMessage replyMessage = new ReplyMessage();
        //是否需要使用卡片
        if(true){

            replyMessage.setReplySource("N");
            replyMessage.setChatLogId();


            List<CustomerNewsItem> customerNewsList = this.convertCustomerNews(wxPubNewsListResult,content);
            replyMessage.setObject(customerNewsList);
        }*/



        /*String lastContent = this.getLastContentFromCache(wxPubOriginId, wxfanOpenId);
        //"更多"时间失效
        if(lastContent == null){
            redisCacheTemplate.del(moreNewsCountCacheKey);
            return "";
        }


        Ad ad = null;//问问搜广告




        if(!ASK_SEARCH_REPLY_TIP.equals(StringUtils.trimWhitespace(content))){
            //获取问问搜广告
            WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);
            ad = adService.getAskSearchPushAd(wxPub);

            moreNewsCountCacheKey = this.getMoreNewsCountCacheKey(wxfanOpenId,content,wxPubOriginId);
            redisCacheTemplate.del(moreNewsCountCacheKey);//关键字搜索,每次应从第一页开始
            redisCacheTemplate.setString(askSearchKeywordCacheKey,content);
            redisCacheTemplate.expire(askSearchKeywordCacheKey,MORE_NEWS_VALID_TIME);
        }else{
            // "更多"从缓存中获取
            Log.d("=============更多 step1==========");
            content = redisCacheTemplate.getString(askSearchKeywordCacheKey);
            Log.d("==============此次更多搜索的关键字为 {?} ===========",content);
            moreNewsCountCacheKey = this.getMoreNewsCountCacheKey(wxfanOpenId,content,wxPubOriginId);
        }
        //"更多"时间失效
        if(content == null){
            redisCacheTemplate.del(moreNewsCountCacheKey);
            return "";
        }

        Long count = redisCacheTemplate.incr(moreNewsCountCacheKey);//用于获取分页素材,关键字:从1开始;"更多":累加
        Log.d("================= remark 用于判断素材页码 count = {?}===========",count.toString());

        //素材总条数
        Integer totalCount = wxMaterialMgrService.getWxPubNewsCount(wxPubOriginId,content);

        //查询本次"更多"素材分页数据,只有"更多"触发分页,关键字总是获取第一页
        QueryWxPubNewsList qo = new QueryWxPubNewsList();
        qo.setTitle(content);
        qo.setWxPubOriginId(wxPubOriginId);
        qo.setPage(count.intValue());
        qo.setPageSize(WX_PUB_ARTICLE_PUSH_COUNT);
        Log.d("=============查询素材分页数据 page = {?} , startIndex = {?} ,  pageSize = {?} ============",qo.getPage().toString(),qo.getStartIndex().toString(),qo.getPageSize().toString());
        List<WxPubNews> wxPubNewsList = wxMaterialMgrService.getWxPubNewsList(qo.getMap());


        Log.d("=============== 素材总条数为  = {?} ,此次分页素材总条数 = {?} ===================",totalCount.toString(),Integer.toString(wxPubNewsList.size()));
        //共有多少次"更多"
        Integer moreCount =  totalCount%WX_PUB_ARTICLE_PUSH_COUNT == 0 ? totalCount / WX_PUB_ARTICLE_PUSH_COUNT - 1 : totalCount / WX_PUB_ARTICLE_PUSH_COUNT ;
        Log.d("================ 该关键字素材回复共会出现 {?} 次\"更多\" ===============",moreCount.toString());
        //即将的5条问问搜素材


        List<CustomerNewsItem> pushArticleList = new ArrayList<>();

        //问问搜开头
        *//*Article startItem = new Article();
        startItem.setTitle(content+ASK_SEARCH_FIRST_ITEM_TITLE);
        startItem.setDescription(ASK_SEARCH_FIRST_ITEM_DESC);
        startItem.setUrl(null);
        startItem.setPicUrl(null);*//*

        CustomerNewsItem startItem = new CustomerNewsItem();
        startItem.setTitle(content+ASK_SEARCH_FIRST_ITEM_TITLE);
        startItem.setDescription(ASK_SEARCH_FIRST_ITEM_DESC);
        startItem.setUrl(null);
        startItem.setPicUrl(null);


        pushArticleList.add(startItem);

        Long askSearchCount = redisCacheTemplate.incr(askSearchCountCacheKey);

        if(askSearchCount == 1){
            redisCacheTemplate.expire(askSearchCountCacheKey,COUNT_CACHE_PERIOD);
        }
        //1,公众号接入问问搜广告  2,问问搜聊天有效期(24小时)内,第一次会插入问问搜广告
        if(askSearchCount == 1 && ad != null){

            CustomerNewsItem article = new CustomerNewsItem();
            article.setPicUrl(ad.getPicUrl());
            article.setTitle(ad.getTitle());
            article.setUrl(ad.getUrl());
            article.setDescription("");

            //问问搜第二条插入广告
            pushArticleList.add(article);
        }

        this.handleNewsMsgList(wxPubNewsList,pushArticleList);

        //当前素材总数
        Integer nowCount = qo.getStartIndex() + qo.getPageSize();
        //判断是否有"更多"图文消息
        if(totalCount.intValue() > nowCount.intValue()){
            CustomerNewsItem lastItem = new CustomerNewsItem();
            lastItem.setTitle(ASK_SEARCH_LAST_ITEM_TITLE);
            lastItem.setUrl(null);
            lastItem.setPicUrl(null);
            lastItem.setDescription("");
            pushArticleList.add(lastItem);

        }else{
            //如果没有"更多",再回复更多无响应
            redisCacheTemplate.del(askSearchKeywordCacheKey);
            redisCacheTemplate.del(moreNewsCountCacheKey);
        }*/

        //图文消息回复日志
        /*StringBuffer chatLogSb = new StringBuffer();
        for(int i = 0; i < pushArticleList.size(); i++){
            //排除首尾
            if(i == 0 || i == pushArticleList.size()-1){
                continue;
            }else if(i == 1){
                chatLogSb.append(pushArticleList.get(i).getTitle());
            }else{
                chatLogSb.append("\n");
                chatLogSb.append(pushArticleList.get(i).getTitle());
            }
        }*/



        //chatLogService.saveResponse(wxPubOriginId, wxfanOpenId, chatLogSb.toString() , chatLogId, "N");

        //return resXml;

    }




    private List<CustomerNewsItem> convertCustomerNews(List<AskSearchVo> askSearchVoList){
        ArrayList<CustomerNewsItem> customerNewsItemList = new ArrayList<>();

        /*CustomerNewsItem startItem = new CustomerNewsItem();
        startItem.setTitle(keyword+ASK_SEARCH_FIRST_ITEM_TITLE);
        startItem.setDescription(ASK_SEARCH_FIRST_ITEM_DESC);
        startItem.setUrl(null);
        startItem.setPicUrl(null);*/

        for (AskSearchVo askSearchVo:askSearchVoList){
            CustomerNewsItem article = new CustomerNewsItem();
            article.setDescription("");
            article.setTitle(askSearchVo.getTitle());
            article.setPicUrl(askSearchVo.getPicUrl());
            customerNewsItemList.add(article);
        }
        return customerNewsItemList;
    }



    private List<WxPubNews> getMoreNews(String wxPubOriginId ,String wxfanOpenId){
         return null;
    }

    private String getChatLogCountCacheKey(String wxPubOpenId, String wxUserOpenId) {
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "ChatLogCount:" + wxPubOpenId + ":" + wxUserOpenId;
    }


    public TuLingInterService getTuLingInterService() {
        return tuLingInterService;
    }

    public void setTuLingInterService(TuLingInterService tuLingInterService) {
        this.tuLingInterService = tuLingInterService;
    }

    private String wxTestReply(TextMsgRec textMsgRec ,Integer chatLogId){

        Log.d("ToUserName gh_3c884a361561 ");

        String wxFanOpenId = textMsgRec.getFromUserName();//用户openId
        String wxPubOriginId = textMsgRec.getToUserName();//公众号 originId
        String content = textMsgRec.getContent();
        TextMsgRes textMsgRes = new TextMsgRes();


        if ("TESTCOMPONENT_MSG_TYPE_TEXT".equals(content)) {

            String respContent = "TESTCOMPONENT_MSG_TYPE_TEXT_callback";
            Log.d("content is " + respContent);
            textMsgRes.setContent(respContent);
        } else {
            textMsgRes.setContent("");

            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    String queryAuthCode = content.replace("QUERY_AUTH_CODE:", "");

                    WxThirtPartAuthorizationResp wxThirtPartAuthorizationResp = wxAuthApiService.fecthAuthorizationInfo(queryAuthCode);

                    WxThirtyPartauthorizerInfo wxThirtyPartauthorizerInfo = wxThirtPartAuthorizationResp.getAuthorizationInfo();
                    String accessToken = wxThirtyPartauthorizerInfo.getAuthorizerAccessToken();
                    String authorizerAppId = wxThirtyPartauthorizerInfo.getAuthorizerAppId();

                    String respContent = queryAuthCode + "_from_api";

                    String result = wxCustomerHelper.sendTextMessage(wxFanOpenId, respContent, accessToken);

                    Log.d("response of wxCustomerHelper send Message : " + result);
                }
            });

        }

        /*textMsgRes.setCreateTime(new Date().getTime() / 1000L);
        textMsgRes.setMsgType("text");
        textMsgRes.setToUserName(wxFanOpenId);
        textMsgRes.setFromUserName(wxPubOriginId);*/


        //String respXml = XmlUtil.convertToXml(textMsgRes);
        //chatLogService.saveResponse(textMsgRes.getFromUserName(), textMsgRes.getToUserName(), textMsgRes.getContent(), chatLogId, textMsgRes.getContent());
        return content;
    }


}

