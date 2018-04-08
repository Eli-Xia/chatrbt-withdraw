package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HtmlTagUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.RandomUtil;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.base.utils.URLUtil;
import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.entity.AdPushLog;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.sdk.wx.WxCustomerHelper;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.service.CfgService;
import net.monkeystudio.wx.service.WxPubService;
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
    private WxPubTagService wxPubTagService;

    @Autowired
    private CfgService cfgService;

    private static String AD_COUNT_SYS_API =  null;

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
    private String pushTextAd(String wxPubAppId , String wxFanOpenId ,String adContent){

        String result = wxCustomerHelper.sendTextMessageByAuthorizerId(wxFanOpenId, wxPubAppId, adContent);

        return result;
    }

    private String pushNewsAd(String wxPubAppId , String wxFanOpenId , List<CustomerNewsItem> customerNewsItemList) throws BizException {
        return wxCustomerHelper.sendNews(wxPubAppId,wxFanOpenId,customerNewsItemList);
    }


    /**
     * 按照指定的概率去推送广告
     * @param wxPubAppId
     * @param wxfanId
     */
    public void randomPush(String wxPubAppId ,Integer wxfanId) throws BizException {

        //临时代码
        /*if(!wxPubTagService.hasTag("关系户", wxPubAppId)){
            return ;
        }*/

        String ratioStr = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_AD_RATIO_KEY);

        Float ratio = Float.valueOf(ratioStr);

        Boolean needToPush = RandomUtil.shot(ratio);

        Ad ad = this.getPushAdByWxPub(wxPubAppId);

        if(ad == null){
            return ;
        }

        Log.i("adType = ?  title = ?  textContext = ?   url = ?  adRecommendStatement ",ad.getAdType().toString(),ad.getTitle(),ad.getTextContent(),ad.getUrl(),ad.getAdRecommendStatement());

        boolean isReach = adService.isReachMaxClick(ad);

        if(isReach){
            //点击数到达最大点击数
            adService.closeAdPushWhenReachMaxClick(ad);
        }
        //当前广告点击数量 < 最大广告点击数量
        if(needToPush && !isReach){
            this.pushAdHandle(ad,wxfanId);
        }
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
    private void pushAdHandle(Ad ad,Integer wxFanId) throws BizException {


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

        //记录广告推送日志
        AdPushLog adPushLog = new AdPushLog();

        adPushLog.setPushAdTimestamp(TimeUtil.getCurrentTimestamp());
        adPushLog.setWxPubAppId(wxPubAppId);
        adPushLog.setWxFanOpenId(wxFanOpenId);
        adPushLog.setAdId(ad.getId());

        adPushLogService.save(adPushLog);
    }

    /**
     * 得到广告开关是否开启
     * @return
     */
    public Boolean getPushAdhSwith(){
        String pushAdhSwithStr = pushMessageConfigService.getByKey(PushMessageConfigService.PUSH_AD_SWITCH_KEY);

        Integer pushAdhSwith = Integer.valueOf(pushAdhSwithStr);

        if(pushAdhSwith.intValue() == 0){
            return false;
        }

        return true;
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

    public Ad getPushAdByWxPub(String wxPubAppId){

        WxPub wxPub = wxPubService.getWxPubByAppId(wxPubAppId);

        return  adService.getPushAdByWxPub(wxPub);

    }
}
