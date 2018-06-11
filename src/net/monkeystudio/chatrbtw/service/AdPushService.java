package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.*;
import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.entity.AdPushLog;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.chatrbtw.service.bean.chatpetmission.DispatchMissionParam;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.service.WxTextMessageHandler;
import net.monkeystudio.wx.vo.customerservice.CustomerNewsItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 2017/11/13.
 */
@Service
public class AdPushService {
    @Autowired
    private ChatPetMissionPoolService chatPetMissionPoolService;

    @Autowired
    private PushMessageConfigService pushMessageConfigService;

    @Autowired
    private WxCustomerHelper wxCustomerHelper;

    @Autowired
    private AdPushLogService adPushLogService;

    @Autowired
    private AdService adService;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private WxTextMessageHandler wxTextMessageHandler;

    @Autowired
    private CfgService cfgService;

    private static String AD_COUNT_SYS_API =  null;

    @Autowired
    private ChatPetService chatPetService;

    @PostConstruct
    public void init(){
        AD_COUNT_SYS_API = cfgService.get(GlobalConfigConstants.AD_COUNT_SYS_API_KEY);
    }

    /**
     * 推送纯文本的广告
     * @param wxPubAppId
     * @param wxFanOpenId
     * @param adContent
     */
    public String pushTextAd(String wxPubAppId , String wxFanOpenId ,String adContent){

        String result = wxCustomerHelper.sendTextMessageByAuthorizerId(wxFanOpenId, wxPubAppId, adContent);

        return result;
    }

    private String pushNewsAd(String wxPubAppId , String wxFanOpenId , List<CustomerNewsItem> customerNewsItemList) throws BizException {
        return wxCustomerHelper.sendNews(wxPubAppId,wxFanOpenId,customerNewsItemList);
    }


    /**
     * 按照指定的概率去推送广告
     * @param wxPubOriginId
     * @param wxfanId
     */
    public void randomPush(String wxPubOriginId ,Integer wxfanId,Integer pushType) throws BizException {

        //临时代码
        /*if(!wxPubTagService.hasTag("关系户", wxPubAppId)){
            return ;
        }*/

        /*String ratioStr = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_AD_RATIO_KEY);

        Float ratio = Float.valueOf(ratioStr);

        Boolean needToPush = RandomUtil.shot(ratio);*/


        Ad ad = this.getAd4WxFanByPushType(pushType,wxfanId);
        if(ad == null){
            return ;
        }

        Log.i("adType = ?  title = ?  textContext = ?   url = ?  adRecommendStatement ",ad.getAdType().toString(),ad.getTitle(),ad.getTextContent(),ad.getUrl(),ad.getAdRecommendStatement());

        boolean needToPush = this.judgeIfAdNeed2PushByStrategyType(ad.getPushStrategyType(),wxPubOriginId,wxfanId,ad.getPushType());

        boolean isReach = adService.isReachMaxClick(ad);

        if(isReach){
            //点击数到达最大点击数
            adService.closeAdPushWhenReachMaxClick(ad);
        }

        //临时:如果是陪聊宠资讯任务广告派发到达最大次数,则不再推广告
        Boolean isReachMaxDispatchTime = false;
        if(adService.AD_PUSH_TYPE_CHAT_PET.equals(ad.getPushType())){
            WxFan wxFan = wxFanService.getById(wxfanId);
            ChatPet chatPet = chatPetService.getChatPetByFans(wxFan.getWxPubOriginId(), wxFan.getWxFanOpenId());
            isReachMaxDispatchTime = chatPetMissionPoolService.isReachMaxDispatchTime(chatPet.getId(), ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);
        }

        //当前广告点击数量 < 最大广告点击数量
        if(needToPush && !isReach && !isReachMaxDispatchTime){
            this.pushAdHandle(ad,wxfanId);
        }
    }

    /**
     * 根据广告推送策略类型(1:概率触发 2:聊天次数触发)判断广告是否能够推送
     * @param pushStrategyType : 推送策略
     * @param wxPubOriginId    :  wx原始id
     * @param wxFanId           :  粉丝id
     * @param pushType          :  投放类型(陪聊宠,智能聊) -> 二者统计聊天次数的缓存策略不同(一天之内,24小时内)
     * @return
     */
    public boolean judgeIfAdNeed2PushByStrategyType(Integer pushStrategyType,String wxPubOriginId,Integer wxFanId,Integer pushType){
        boolean needToPush = false;

        if(adService.AD_PUSH_STRATEGY_CHANCE.equals(pushStrategyType)){

            //广告投放开关开启
            if(this.getPushAdSwitch(pushStrategyType)){

                if(this.checkIsShotByAdPushRatio(pushStrategyType)){
                    needToPush = true;
                }
            }
        }

        if(adService.AD_PUSH_STRATEGY_CHAT_COUNT.equals(pushStrategyType)){
            //广告投放开关开启
            if(this.getPushAdSwitch(pushStrategyType)){

                //所有陪聊宠广告都是走概率策略,下面这步应该省略
                if(adService.AD_PUSH_TYPE_CHAT_PET.equals(pushType)){
                    Integer count = wxTextMessageHandler.getChatCount4ChatPetAd(wxPubOriginId, wxFanId);
                }

                if(adService.AD_PUSH_TYPE_SMART_CHAT.equals(pushType)){

                    Integer count = wxTextMessageHandler.getChatCount4SmartChatAd(wxPubOriginId, wxFanId);
                    String pushCount = pushMessageConfigService.getByKey(PushMessageConfigService.CHAT_PUSH_AD_COUNT_KEY);
                    //到达聊天次数
                    if(Integer.valueOf(pushCount).equals(count)){
                        if(this.checkIsShotByAdPushRatio(pushStrategyType)){
                            needToPush = true;
                        }
                    }
                }
            }
        }
        return needToPush;
    }

    /**
     * 获取推送的广告
     * @return
     */
    public Ad getPushAd(){
        String adIdStr = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_AD_ID_KEY);

        Ad ad = adService.getAdById(Integer.valueOf(adIdStr));

        return ad;
    }

    /**
     * 推送广告时的处理
     * @param ad
     * @param wxFanId
     */
    public void pushAdHandle(Ad ad,Integer wxFanId) throws BizException {


        WxFan wxFan = wxFanService.getById(wxFanId);
        String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxFan.getWxPubOriginId());

        String wxFanOpenId = wxFan.getWxFanOpenId();
        //推送广告推荐语
        String adRecommendStatement = ad.getAdRecommendStatement();
        if(adRecommendStatement != null){
            this.pushTextAd(wxPubAppId,wxFanOpenId,adRecommendStatement);
        }


        //推送广告
        String pushAdRespStr = this.pushAd(wxPubAppId,wxFanId,ad);


        if(pushAdRespStr.indexOf("errcode") == -1){
            Log.e("ad push faild ! error info :" + pushAdRespStr);

            return ;
        }

        Log.d("===============陪聊宠随机任务触发 检查参数 adId = {?} , wxFanId = {?} =================",ad.getId().toString(),wxFanId.toString());
        //陪聊宠随机任务触发
        /*if(adService.AD_PUSH_TYPE_CHAT_PET.equals(ad.getPushType())){
            chatPetMissionPoolService.saveMissionRecordWhenPushChatPetAd(ad.getId(),wxFanId);
        }*/
        DispatchMissionParam param = new DispatchMissionParam();

        param.setChatPetId(chatPetService.getChatPetIdByFans(wxFan.getWxPubOriginId(),wxFan.getWxFanOpenId()));
        param.setMissionCode(ChatPetMissionEnumService.SEARCH_NEWS_MISSION_CODE);
        param.setAdId(ad.getId());

        chatPetMissionPoolService.dispatchMission(param);

        //记录广告推送日志
        AdPushLog adPushLog = new AdPushLog();

        adPushLog.setPushAdTimestamp(TimeUtil.getCurrentTimestamp());
        adPushLog.setWxPubAppId(wxPubAppId);
        adPushLog.setWxFanOpenId(wxFanOpenId);
        adPushLog.setAdId(ad.getId());

        adPushLogService.save(adPushLog);
    }

    /**
     * 判断广告是否已经推送过给粉丝
     * @param wxFanId   粉丝id
     * @param adId       广告id
     * @return
     */
    public Boolean isAdHasPushedWxFanId(Integer wxFanId,Integer adId){
        Boolean isPush = false;

        WxFan wxFan = wxFanService.getById(wxFanId);
        String wxFanOpenId = wxFan.getWxFanOpenId();

        String wxPubOriginId = wxFan.getWxPubOriginId();
        String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);

        //获取该广告推送给该粉丝的次数
        Integer count = adPushLogService.countAdPushAmount4WxFan(wxPubAppId, wxFanOpenId, adId);

        if(count.intValue() > 0){
            isPush = true;
        }

        return isPush;
    }


    /**
     * 得到广告开关是否开启
     * @return

    public Boolean getPushAdhSwith(){
        String pushAdhSwithStr = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_AD_SWITCH_KEY);

        Integer pushAdhSwith = Integer.valueOf(pushAdhSwithStr);

        if(pushAdhSwith.intValue() == 0){
            return false;
        }

        return true;
    }
     */

    /**
     * 得到广告开关是否开启
     * @param pushStrategyType : 广告推送策略
     * @return
     */
    public Boolean getPushAdSwitch(Integer pushStrategyType){
        String pushAdSwitchStr = "0";//默认关闭

        if(adService.AD_PUSH_STRATEGY_CHAT_COUNT.equals(pushStrategyType)){
            pushAdSwitchStr = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_AD_SWITCH_KEY);
        }

        if(adService.AD_PUSH_STRATEGY_CHANCE.equals(pushStrategyType)){
            pushAdSwitchStr = pushMessageConfigService.getByKey(PushMessageConfigService.PROBABILITY_STRATEGY_PUSH_AD_SWITCH_KEY);
        }

        Integer pushAdSwitch = Integer.valueOf(pushAdSwitchStr);

        if(pushAdSwitch.intValue() == 0){
            return false;
        }

        return true;
    }

    /**
     * 推送概率是否命中
     * @param pushStrategyType
     * @return
     */
    public Boolean checkIsShotByAdPushRatio(Integer pushStrategyType){
        Float ratio = 1F;

        String ratioStr = null;

        if(adService.AD_PUSH_STRATEGY_CHANCE.equals(pushStrategyType)){
            ratioStr = pushMessageConfigService.getByKey(PushMessageConfigService.PROBABILITY_STRATEGY_PUSH_AD_RATIO_KEY);
        }

        if(adService.AD_PUSH_STRATEGY_CHAT_COUNT.equals(pushStrategyType)){
            ratioStr = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_AD_RATIO_KEY);
        }
        ratio = Float.valueOf(ratioStr);

        return RandomUtil.shot(ratio);
    }



    //获取广告推送地址
    public String getAdPushUrl(Integer adId,Integer wxFanId,String wxFanOpenId){

        String pushUrl = URLUtil.addParam(AD_COUNT_SYS_API, "adId", String.valueOf(adId));

        pushUrl = URLUtil.addParam(pushUrl,"wxFanId",String.valueOf(wxFanId));

        pushUrl = URLUtil.addParam(pushUrl, "wxFanOpenId", wxFanOpenId);

        return pushUrl;
    }



    private String pushAd(String wxPubAppId, Integer wxFanId, Ad ad) throws BizException {

        Integer adType = ad.getAdType();
        WxFan wxFan = wxFanService.getById(wxFanId);

        String wxFanOpenId = wxFan.getWxFanOpenId();

        String pushUrl = this.getAdPushUrl(ad.getId(), wxFanId, wxFanOpenId);

        if(adType.intValue() == AdService.AD_NEWS_TYPE.intValue()){

            List<CustomerNewsItem> customerNewsItemList = new ArrayList<>();

            CustomerNewsItem customerNewsItem = new CustomerNewsItem();
            customerNewsItem.setUrl(pushUrl);
            customerNewsItem.setTitle(ad.getTitle());
            customerNewsItem.setPicUrl(ad.getPicUrl());
            customerNewsItem.setDescription(ad.getDescription());

            customerNewsItemList.add(customerNewsItem);

            return this.pushNewsAd(wxPubAppId,wxFanOpenId,customerNewsItemList);
        }


        if(adType.intValue() == AdService.AD_TEXT_TYPE.intValue()){

            String adTextContent = ad.getTextContent();

            String adUrl = HtmlTagUtil.getATagHref(adTextContent);

            adTextContent = adTextContent.replace(adUrl, pushUrl);

            return this.pushTextAd(wxPubAppId,wxFanOpenId,adTextContent);
        }

        if(adType.intValue() == AdService.AD_IMAGE_TYPE.intValue()){

        }

        return null;
    }

    /**
     * 根据推送类型(智能聊,陪聊宠)为指定公众号下指定的粉丝获取一条广告
     * @param pushType   :     广告类型(智能聊,陪聊宠)
     * @param wxFanId    :     粉丝id
     * @return
     */
    public Ad getAd4WxFanByPushType(Integer pushType,Integer wxFanId){

        if(adService.AD_PUSH_TYPE_CHAT_PET.equals(pushType)){
            return adService.getChatPetPushAd(wxFanId);
        }
        if(adService.AD_PUSH_TYPE_SMART_CHAT.equals(pushType)){
            return adService.getSmartChatPushAd(wxFanId);
        }

        return null;
    }

}
