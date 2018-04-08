package net.monkeystudio.wx.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.HtmlTagUtil;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.sdk.wx.WxPubHelper;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.chatrbtw.service.bean.chatrobot.resp.ChatRobotInfoResp;
import net.monkeystudio.chatrbtw.service.bean.textmessage.RespStrAndType;
import net.monkeystudio.chatrbtw.service.bean.wxpubarticle.PushArticleListItem;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.wx.vo.thirtparty.AuthorizerInfo;
import net.monkeystudio.wx.vo.thirtparty.PubBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.wx.controller.bean.TextMsgRec;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.vo.thirtparty.WxThirtPartAuthorizationResp;
import net.monkeystudio.wx.vo.thirtparty.WxThirtyPartauthorizerInfo;

/**
 * 微信文本消息处理
 * Created by bint on 2017/12/1.
 */
@Service
public class WxTextMessageHandler {

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

    private final static int REPLY_TYPE_NONE = -1;//不做回复类型
    private final static int REPLY_TYPE_TEST = 0;//测试类型
    private final static int REPLY_TYPE_NEED_TO_FILTER = 1;//需要过滤的类型
    private final static int REPLY_TYPE_WX_PUB_REPLY_KEYWORD = 2;//微信公众号关键字
    private final static int REPLY_TYPE_WX_PUB_CUSTOM_KEYWORDS = 3;//金豆平台上的公众号自定义关键字
    private final static int REPLY_TYPE_WX_MATERIAL = 4;//微信素材回复
    private final static int REPLY_TYPE_BASE_KEYWORDS = 5; //公众关键字回复
    private final static int REPLY_TYPE_TU_LING = 6;  //图灵关键字回复




    @Autowired
    private WxMaterialMgrService wxMaterialMgrService;

    //统计周期
    private final static Integer COUNT_CACHE_PERIOD = 24 * 60 * 60;

    //微信文章推送个数
    private final static Integer WX_PUB_ARTICLE_PUSH_COUNT = 6;

    //图灵机器人名字
    private final static String TU_LING_ROBOT_NAME =  "keendooo";

    private final static String[] IGNORE_TEX_MESSAGE_ARRAY = {
            "【收到不支持的消息类型，暂无法显示】",
            "【收到不支援的訊息類型，無法顯示】"
    };

    public String handleTextMsg(String body, String openId) throws BizException{
        TextMsgRec textMsgRec = XmlUtil.converyToJavaBean(body, TextMsgRec.class);
        return this.textMsgRecHandle(textMsgRec);
    }

    /*public String textMsgRecHandle(TextMsgRec textMsgRec) throws BizException{
        if (textMsgRec == null) {
            return "parse body error";
        } else if (textMsgRec.getContent() == null) {
            return "content empty.";
        }
        String replySrc = null;

        TextMsgRes textMsgRes = new TextMsgRes();//消息回复对象

        String wxUserOpenId = textMsgRec.getFromUserName(); //用户openId
        String wxPubOriginId = textMsgRec.getToUserName(); //公众号 originId
        String content = textMsgRec.getContent();

        //如果需要过滤掉，返回空让上层处理
        if (needFiltered(content)) {
            return null;
        }

        String wxPubOpenId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);//在redis里面获取 appid
        //如果是微信公众号的关键字不用处理
        if (wxPubService.isAuotReplyKeyword(content, wxPubOpenId)) {
            return null;
        }

        Integer chatLogId = chatLogService.saveReceive(textMsgRec.getToUserName(), textMsgRec.getFromUserName(), textMsgRec.getContent());

        //如果是测试账号,做wx要求的回复内容
        if ("gh_3c884a361561".equals(wxPubOriginId)) {
            return this.wxTestReply(textMsgRec, chatLogId);
        }

        //粉丝信息的处理
        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxUserOpenId);
        Integer chatUid = AppConstants.CHAT_UID_DEFAULT;
        Integer wxFanId = null;
        if (wxFan != null) {
            Log.d("wxFan:id=" + wxFan.getId() + ",nickname=" + wxFan.getNickname() + ",openid=" + wxFan.getWxFanOpenId());
            chatUid = wxFan.getId();
            wxFanId = wxFan.getId();
        }

        //公众号定制的关键字回复
        KrResponse krResponse = keywordResponseService.getWxPubResponse(textMsgRec.getContent(), wxPubOriginId);
        String respStr = "";
        if (krResponse == null) {

            respStr = this.metarialHandle(textMsgRec);
            //如果图文消息回复
            if(respStr != null){
                replySrc = "N";
            }else {

                //公共关键字
                krResponse = keywordResponseService.getBaseResponse(textMsgRec.getContent());

                if(krResponse == null){
                    //走图灵
                    replySrc = "T";
                    respStr = tuLingInterService.getResponse(chatUid, textMsgRec.getContent());

                    if(respStr.indexOf(TU_LING_ROBOT_NAME) != -1){
                        ChatRobotInfoResp chatRobotInfoResp = chatRobotService.getChatRobotInfoRespByWxPubOriginId(wxPubOriginId);

                        if(chatRobotInfoResp != null && chatRobotInfoResp.getNickname() != null){
                            respStr = respStr.replace(TU_LING_ROBOT_NAME,chatRobotInfoResp.getNickname());
                        }
                    }
                }else {

                    //公众关键字回复
                    replySrc = "BK";
                    if (krResponse.getType().equals("text")) {
                        //文本消息
                        respStr = krResponse.getResponse();
                    } else if (krResponse.getType().equals("mpnews")) {
                        respStr = "";
                    }

                }
            }

        }else {

            //公众号自定义关键字回复
            replySrc = "PK";
            if (krResponse.getType().equals("text")) {
                //文本消息
                respStr = krResponse.getResponse();
            } else if (krResponse.getType().equals("mpnews")) {
                respStr = "";
            }
        }


        //如果返回的是空白就返回空，让上层做处理
        if ("".equals(respStr)) {
            return null;
        }

        respStr = responseProcessService.responseProecess(wxPubOriginId, respStr);

        textMsgRes.setContent(respStr);

        String countCacheKey = this.getChatLogCountCacheKey(wxPubOriginId, wxUserOpenId);
        Long count = redisCacheTemplate.incr(countCacheKey);

        if (count == 1) {
            redisCacheTemplate.expire(countCacheKey, COUNT_CACHE_PERIOD);
        }

        String chatAdPushCountStr = pushMessageConfigService.getByKey(PushMessageConfigService.CHAT_PUSH_AD_COUNT_KEY);
        if (chatAdPushCountStr != null) {
            Long chatAdPushCount = Long.valueOf(chatAdPushCountStr);
            //如果刚好到达次数，则触发推送广告机制
            if (count.longValue() == chatAdPushCount.longValue()) {

                WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

                if(wxPub != null ){
                    Integer wxPubVerifyTypeInfo = wxPub.getVerifyTypeInfo();

                    if(wxPubVerifyTypeInfo  == null){
                        this.reviseWxPub(wxPubOriginId);
                        wxPub = wxPubService.getByOrginId(wxPubOriginId);

                        wxPubVerifyTypeInfo = wxPub.getVerifyTypeInfo();
                    }

                    if(wxPubVerifyTypeInfo.intValue() == wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY){
                        try {
                            this.sendAd(wxFanId, wxPubOriginId);
                        } catch (Exception e) {
                            Log.e(e);
                        }
                    }
                }

            }
        }

        textMsgRes.setCreateTime(new Date().getTime() / 1000L);
        textMsgRes.setMsgType("text");
        textMsgRes.setToUserName(wxUserOpenId);
        textMsgRes.setFromUserName(wxPubOriginId);

        try {
            String respXml = XmlUtil.convertToXml(textMsgRes);
            chatLogService.saveResponse(textMsgRes.getFromUserName(), textMsgRes.getToUserName(), textMsgRes.getContent(), chatLogId, replySrc);
            return respXml;

        } catch (Exception e) {
            Log.e(e);
            return "error";
        }
    }*/


    public String textMsgRecHandle(TextMsgRec textMsgRec) throws BizException{
        if (textMsgRec == null) {
            return "parse body error";
        } else if (textMsgRec.getContent() == null) {
            return "content empty.";
        }
        String replySrc = null;

        TextMsgRes textMsgRes = new TextMsgRes();//消息回复对象

        String wxFanOpenId = textMsgRec.getFromUserName(); //用户openId
        String wxPubOriginId = textMsgRec.getToUserName(); //公众号 originId
        String content = textMsgRec.getContent();


        Integer responseType = this.getRespType(textMsgRec);

        //粉丝信息的处理
        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);

        String respStr = null;

        Integer chatLogId = null;
        switch (responseType.intValue()){

            //过滤掉的类型
            case REPLY_TYPE_NEED_TO_FILTER :
                return null;

            //微信关键字的类型
            case REPLY_TYPE_WX_PUB_REPLY_KEYWORD:
                return null;

            //测试类型
            case REPLY_TYPE_TEST:
                chatLogId = chatLogService.saveReceive(textMsgRec.getToUserName(), textMsgRec.getFromUserName(), textMsgRec.getContent());
                return this.wxTestReply(textMsgRec, chatLogId);

            //金豆公众号关键字回复
            case REPLY_TYPE_WX_PUB_CUSTOM_KEYWORDS:
                chatLogId = chatLogService.saveReceive(textMsgRec.getToUserName(), textMsgRec.getFromUserName(), textMsgRec.getContent());

                KrResponse krResponse = keywordResponseService.getWxPubResponse(textMsgRec.getContent(), wxPubOriginId);
                replySrc = "BK";
                if (krResponse.getType().equals("text")) {
                    //文本消息
                    respStr = krResponse.getResponse();
                } else if (krResponse.getType().equals("mpnews")) {
                    respStr = "";
                }
                break;

            //素材回复
            case REPLY_TYPE_WX_MATERIAL:
                chatLogId = chatLogService.saveReceive(textMsgRec.getToUserName(), textMsgRec.getFromUserName(), textMsgRec.getContent());

                respStr = this.metarialHandle(textMsgRec);
                replySrc = "N";
                break;

            //金豆公众号公众关键字回复
            case REPLY_TYPE_BASE_KEYWORDS:
                chatLogId = chatLogService.saveReceive(textMsgRec.getToUserName(), textMsgRec.getFromUserName(), textMsgRec.getContent());

                KrResponse baseWxPubKrResponse = keywordResponseService.getBaseResponse(textMsgRec.getContent());
                replySrc = "BK";
                if (baseWxPubKrResponse.getType().equals("text")) {
                    //文本消息
                    respStr = baseWxPubKrResponse.getResponse();
                } else if (baseWxPubKrResponse.getType().equals("mpnews")) {
                    respStr = "";
                }
                break;

            //图灵回复
            case REPLY_TYPE_TU_LING:
                chatLogId = chatLogService.saveReceive(textMsgRec.getToUserName(), textMsgRec.getFromUserName(), content);

                replySrc = "T";

                Integer chatUid = AppConstants.CHAT_UID_DEFAULT;
                if (wxFan != null) {
                    Log.d("wxFan:id=" + wxFan.getId() + ",nickname=" + wxFan.getNickname() + ",openid=" + wxFan.getWxFanOpenId());
                    chatUid = wxFan.getId();
                }

                respStr = tuLingInterService.getResponse(chatUid, textMsgRec.getContent());

                if(respStr.indexOf(TU_LING_ROBOT_NAME) != -1){
                    ChatRobotInfoResp chatRobotInfoResp = chatRobotService.getChatRobotInfoRespByWxPubOriginId(wxPubOriginId);

                    if(chatRobotInfoResp != null && chatRobotInfoResp.getNickname() != null){
                        respStr = respStr.replace(TU_LING_ROBOT_NAME,chatRobotInfoResp.getNickname());
                    }
                }
                break;

            case REPLY_TYPE_NONE:
                return null;
        }


        //如果返回的是空白就返回空，让上层做处理
        if ("".equals(respStr)) {
            return null;
        }

        respStr = responseProcessService.responseProecess(wxPubOriginId, respStr);

        textMsgRes.setContent(respStr);

        smartChatAdProecess(wxPubOriginId,wxFanOpenId);

        textMsgRes.setCreateTime(new Date().getTime() / 1000L);
        textMsgRes.setMsgType("text");
        textMsgRes.setToUserName(wxFanOpenId);
        textMsgRes.setFromUserName(wxPubOriginId);

        try {
            String respXml = XmlUtil.convertToXml(textMsgRes);
            chatLogService.saveResponse(textMsgRes.getFromUserName(), textMsgRes.getToUserName(), respStr , chatLogId, replySrc);
            return respXml;
        } catch (Exception e) {
            Log.e(e);
            return null;
        }
    }

    //TODO

    /*private RespStrAndType getRespAndType(TextMsgRec textMsgRec){
        TextMsgRes textMsgRes = new TextMsgRes();//消息回复对象

        String wxUserOpenId = textMsgRec.getFromUserName(); //用户openId
        String wxPubOriginId = textMsgRec.getToUserName(); //公众号 originId
        String content = textMsgRec.getContent();

        return null;

    }*/

    /**
     * 智能聊广告处理
     * @param wxPubOriginId
     * @param wxFanOpenId
     * @throws BizException
     */
    private void smartChatAdProecess(String wxPubOriginId , String wxFanOpenId) throws BizException {

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

            String chatAdPushCountStr = pushMessageConfigService.getByKey(PushMessageConfigService.CHAT_PUSH_AD_COUNT_KEY);
            if (chatAdPushCountStr != null) {
                Long chatAdPushCount = Long.valueOf(chatAdPushCountStr);
                //如果刚好到达次数，则触发推送广告机制
                if (count.longValue() == chatAdPushCount.longValue()) {

                    if(wxPubVerifyTypeInfo.intValue() == wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY){
                        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
                        if(wxFan != null){
                            this.sendAd(wxFan.getId(), wxPubOriginId);
                        }
                    }
                }
            }
        }
    }

    private Integer getRespType(TextMsgRec textMsgRec) throws BizException {


        TextMsgRes textMsgRes = new TextMsgRes();//消息回复对象

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
            //素材回复
            List<WxPubNews> wxPubNews = wxMaterialMgrService.getWxPubNews(wxPubOriginId, content, WX_PUB_ARTICLE_PUSH_COUNT);
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

    private void sendAd(Integer wxFanId , String wxPubOriginId){
        String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);

        //到达触发广告机制次数的人数+1
        wxChatCountService.incrArrivalCount();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {

                if (adPushService.getPushAdhSwith()) {
                    Log.i("ad time : wxPubOriginId [?] ,WxFanId [?] ", wxPubOriginId, String.valueOf(wxFanId));

                    try {
                        adPushService.randomPush(wxPubAppId, wxFanId);
                    } catch (BizException e) {
                        Log.e(e);
                    }
                }

                Log.d("ad push swicth : " + adPushService.getPushAdhSwith());
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




    public String metarialHandle(TextMsgRec textMsgRec){

        String content = textMsgRec.getContent();
        String wxPubOriginId = textMsgRec.getToUserName();
        String wxfanOpenId = textMsgRec.getFromUserName();
        /*String content = "元太";
        String wxPubOriginId = "gh_902e0d566cd9";
        String wxfanOpenId = "oRQue0zoz-0A1bnum5qc-Iq0cLvw";*/

        List<WxPubNews> wxPubNewsList = wxMaterialMgrService.getWxPubNews(wxPubOriginId, content, WX_PUB_ARTICLE_PUSH_COUNT);


        if(wxPubNewsList == null | wxPubNewsList.size() == 0){
            return null;
        }

        List<PushArticleListItem> pushArticleList = new LinkedList<>();

        for(WxPubNews wxPubNews : wxPubNewsList){
            PushArticleListItem pushArticleListItem = BeanUtils.copyBean(wxPubNews, PushArticleListItem.class);
            pushArticleList.add(pushArticleListItem);

            String realUrl = wxPubNews.getUrl2();

            //如果文章真实地址存在，则用正式的
            if(realUrl != null){
                pushArticleListItem.setArticleUrl(realUrl);
            }else {
                pushArticleListItem.setArticleUrl(wxPubNews.getUrl());
            }
        }

        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);
        //随机获取一条问问搜广告
        Ad ad = adService.getAskSearchAdByWxPub(wxPub);

        if(ad != null){

            PushArticleListItem adItem = new PushArticleListItem();

            WxFan fan = wxFanService.getWxFan(wxPubOriginId,wxfanOpenId);

            String pushUrl = adPushService.getAdPushUrl(ad.getId(),fan.getId(),wxfanOpenId);

            adItem.setArticleUrl(pushUrl);

            adItem.setTitle(ad.getTitle()==null?"":ad.getTitle());
            //问问搜第二条插入广告
            pushArticleList.add(1,adItem);
        }

        StringBuffer contentBuffer = new StringBuffer();

        for(int i=0;i<pushArticleList.size();i++ ){

            PushArticleListItem item = pushArticleList.get(i);
            String title = item.getTitle();
            String url = item.getArticleUrl();

            contentBuffer.append(title);
            contentBuffer.append(" | ");
            contentBuffer.append(HtmlTagUtil.generateATag(url,"点击查看"));
            contentBuffer.append("\n");
            contentBuffer.append("\n");
        }


        String replyContent = contentBuffer.toString();
        replyContent = replyContent.substring(0,replyContent.length()-2);

        return replyContent;
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

        textMsgRes.setCreateTime(new Date().getTime() / 1000L);
        textMsgRes.setMsgType("text");
        textMsgRes.setToUserName(wxFanOpenId);
        textMsgRes.setFromUserName(wxPubOriginId);


        String respXml = XmlUtil.convertToXml(textMsgRes);
        chatLogService.saveResponse(textMsgRes.getFromUserName(), textMsgRes.getToUserName(), textMsgRes.getContent(), chatLogId, textMsgRes.getContent());
        return respXml;
    }
}

