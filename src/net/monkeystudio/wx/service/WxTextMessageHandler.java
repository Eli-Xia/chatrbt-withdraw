package net.monkeystudio.wx.service;

import net.monkeystudio.admin.controller.req.wxpubmaterial.QueryWxPubNewsList;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.chatrbtw.AppConstants;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.chatrbtw.sdk.wx.WxPubHelper;
import net.monkeystudio.chatrbtw.service.*;
import net.monkeystudio.chatrbtw.service.bean.chatrobot.resp.ChatRobotInfoResp;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.wx.controller.bean.Article;
import net.monkeystudio.wx.controller.bean.NewsMsgRes;
import net.monkeystudio.wx.controller.bean.TextMsgRec;
import net.monkeystudio.wx.controller.bean.TextMsgRes;
import net.monkeystudio.wx.vo.customerservice.CustomerNews;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
import net.monkeystudio.wx.vo.thirtparty.AuthorizerInfo;
import net.monkeystudio.wx.vo.thirtparty.PubBaseInfo;
import net.monkeystudio.wx.vo.thirtparty.WxThirtPartAuthorizationResp;
import net.monkeystudio.wx.vo.thirtparty.WxThirtyPartauthorizerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 微信文本消息处理
 * Created by bint on 2017/12/1.
 */
@Service
public class WxTextMessageHandler extends WxBaseMessageHandler{

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
    private CfgService cfgService;

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

    private final static String ASK_SEARCH_FIRST_ITEM_TITLE = " 搜索结果";
    private final static String ASK_SEARCH_FIRST_ITEM_DESC = "Powered by keendo.com.cn";
    private final static String ASK_SEARCH_LAST_ITEM_TITLE = "在下面↓回复\"更多\"即可获取更多结果";
    private final static String ASK_SEARCH_REPLY_TIP = "更多";




    @Autowired
    private WxMaterialMgrService wxMaterialMgrService;

    //统计周期
    private final static Integer COUNT_CACHE_PERIOD = 24 * 60 * 60;

    //问问搜"更多"有效时长为半小时
    private final static Integer MORE_NEWS_VALID_TIME = 60 * 30;

    //微信文章推送个数
    private final static Integer WX_PUB_ARTICLE_PUSH_COUNT = 5;

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

        Integer replyMsgType = REPLY_MSG_TYPE_TEXT;//当前默认消息回复类型为文本类型

        String wxFanOpenId = textMsgRec.getFromUserName(); //用户openId
        String wxPubOriginId = textMsgRec.getToUserName(); //公众号 originId
        String content = textMsgRec.getContent();


        Integer responseType = this.getRespType(textMsgRec);
        Log.d("############responseType = [?]#############",responseType.toString());

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

                replySrc = "N";
                respStr = this.metarialHandle(textMsgRec,chatLogId,replySrc);
                replyMsgType = REPLY_MSG_TYPE_ARTICLES;//问问搜回复图文消息
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


        smartChatAdProecess(wxPubOriginId,wxFanOpenId);

        switch (replyMsgType){
            //文本消息回复
            case REPLY_MSG_TYPE_TEXT:
                respStr = responseProcessService.responseProecess(wxPubOriginId, respStr);

                String respXml = this.replyTextStr(wxPubOriginId, wxFanOpenId, replySrc);

                try {
                    chatLogService.saveResponse(wxPubOriginId, wxFanOpenId, respStr , chatLogId, replySrc);
                    return respXml;
                } catch (Exception e) {
                    Log.e(e);
                    return null;
                }

            case REPLY_MSG_TYPE_ARTICLES:

                return respStr;

        }

        return null;

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

            String chatAdPushCountStr = pushMessageConfigService.getByKey(PushMessageConfigService.CHAT_PUSH_AD_COUNT_KEY);
            if (chatAdPushCountStr != null) {
                Long chatAdPushCount = Long.valueOf(chatAdPushCountStr);
                //如果刚好到达次数，则触发推送广告机制
                if (count.longValue() == chatAdPushCount.longValue()) {
                    if(wxPubVerifyTypeInfo.intValue() == wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY){
                        WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
                        if(wxFan != null){
                            Log.d("============= smart chat ad has been pushed !! ============");
                            this.sendAd(wxFan.getId(), wxPubOriginId);
                        }
                    }
                }
            }
        }
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
    public String metarialHandle(TextMsgRec textMsgRec,Integer chatLogId,String replySrc){

        String content = textMsgRec.getContent();
        String wxPubOriginId = textMsgRec.getToUserName();
        String wxfanOpenId = textMsgRec.getFromUserName();

        String askSearchCountCacheKey = this.getAskSearchCountCacheKey(wxPubOriginId, wxfanOpenId);
        String moreNewsCountCacheKey = null;
        String askSearchKeywordCacheKey = this.getAskSearchKeywordCacheKey(wxPubOriginId,wxfanOpenId);


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
            content = redisCacheTemplate.getString(askSearchKeywordCacheKey);
            moreNewsCountCacheKey = this.getMoreNewsCountCacheKey(wxfanOpenId,content,wxPubOriginId);
        }
        //"更多"时间失效
        if(content == null){
            redisCacheTemplate.del(moreNewsCountCacheKey);
            return "";
        }

        Long count = redisCacheTemplate.incr(moreNewsCountCacheKey);//用于获取分页素材,关键字:从1开始;"更多":累加

        //素材总条数
        Integer totalCount = wxMaterialMgrService.getWxPubNewsCount(wxPubOriginId,content);

        //查询本次"更多"素材分页数据,只有"更多"触发分页,关键字总是获取第一页
        QueryWxPubNewsList qo = new QueryWxPubNewsList();
        qo.setTitle(content);
        qo.setWxPubOriginId(wxPubOriginId);
        qo.setPage((count.intValue()-1) * WX_PUB_ARTICLE_PUSH_COUNT);
        qo.setPageSize(WX_PUB_ARTICLE_PUSH_COUNT);
        Log.d("=============查询素材分页数据 page = {?} ,  pageSize = {?} ============",qo.getPage().toString(),qo.getPageSize().toString());
        List<WxPubNews> wxPubNewsList = wxMaterialMgrService.getWxPubNewsList(qo.getMap());


        Log.i("=============== 素材总条数为  = {?} ===================",totalCount.toString());
        //共有多少次"更多"
        Integer moreCount =  totalCount%WX_PUB_ARTICLE_PUSH_COUNT == 0 ? totalCount / WX_PUB_ARTICLE_PUSH_COUNT - 1 : totalCount / WX_PUB_ARTICLE_PUSH_COUNT ;
        Log.i("================ 该关键字素材回复共会出现 {?} 次\"更多\" ===============",moreCount.toString());
        //即将的5条问问搜素材


        List<CustomerNewsItem> pushArticleList = new ArrayList<>();

        //问问搜开头
        /*Article startItem = new Article();
        startItem.setTitle(content+ASK_SEARCH_FIRST_ITEM_TITLE);
        startItem.setDescription(ASK_SEARCH_FIRST_ITEM_DESC);
        startItem.setUrl(null);
        startItem.setPicUrl(null);*/

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

        //素材总条数
        Integer newsCount = wxMaterialMgrService.getWxPubNewsCount(wxPubOriginId,content);
        //当前素材总数
        Integer nowCount = qo.getPage() + qo.getPageSize();
        //判断是否有"更多"图文消息
        if(newsCount.intValue() > nowCount.intValue()){
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
        }

        //图文消息回复日志
        StringBuffer chatLogSb = new StringBuffer();
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
        }

        /*NewsMsgRes res = new NewsMsgRes();
        res.setFromUserName(wxPubOriginId);
        res.setToUserName(wxfanOpenId);
        res.setMsgType("news");
        res.setArticles(pushArticleList);
        res.setCreateTime(new Date().getTime() / 1000L);
        res.setArticleCount(pushArticleList.size());*/


        String resXml = this.replyNewsStr(wxPubOriginId, wxfanOpenId, pushArticleList);

        chatLogService.saveResponse(wxPubOriginId, wxfanOpenId, chatLogSb.toString() , chatLogId, replySrc);

        return resXml;

    }
    private void  handleNewsMsgList(List<WxPubNews> news,List<CustomerNewsItem> as){
        for (WxPubNews wxPubNews:news){
            CustomerNewsItem article = new CustomerNewsItem();
            article.setDescription("");
            article.setTitle(wxPubNews.getTitle());
            article.setPicUrl(wxPubNews.getThumbUrl());
            String realUrl = wxPubNews.getUrl2();

            //如果文章真实地址存在，则用正式的
            if(realUrl != null){
                article.setUrl(realUrl);
            }else {
                article.setUrl(wxPubNews.getUrl());
            }
            as.add(article);
        }
    }



    private String getAskSearchKeywordCacheKey(String wxPubOriginId, String wxfanOpenId) {
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "AskSearchKw:" + wxPubOriginId + ":" + wxfanOpenId;
    }

    /**
     * 获取问问搜聊天次数cache的key
     * @param wxPubOpenId
     * @param wxUserOpenId
     * @return
     */
    private String getAskSearchCountCacheKey(String wxPubOpenId,String wxUserOpenId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "AskSearchCount:" + wxPubOpenId + ":" + wxUserOpenId;
    }



    private String getMoreNewsCountCacheKey(String wxfanOpenId,String content,String wxPubOriginId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "moreNewsCount:" + wxPubOriginId + ":" + wxfanOpenId + ":" + content ;
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

