package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.admin.controller.req.ad.AddAd;
import net.monkeystudio.admin.controller.resp.ad.AdMgrListResp;
import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.*;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.mapper.AdMapper;
import net.monkeystudio.chatrbtw.mapper.RAdWxPubMapper;
import net.monkeystudio.chatrbtw.mapper.RAdWxPubTagMapper;
import net.monkeystudio.chatrbtw.service.bean.ad.*;
import net.monkeystudio.wx.service.WxPubAuthorizerRefreshTokenService;
import net.monkeystudio.wx.service.WxPubService;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bint on 2017/12/8.
 */
@Service
public class AdService {

    //图文形式的广告
    public final static Integer AD_NEWS_TYPE = 1;

    //文本形式的广告
    public final static Integer AD_TEXT_TYPE = 2;

    //图片形式的广告
    public final static Integer AD_IMAGE_TYPE = 3 ;

    //广告推送类型-宠陪聊
    public final static Integer AD_PUSH_TYPE_CHAT_PET = 2;
    //广告推送类型-问问搜
    public final static Integer AD_PUSH_TYPE_ASK_SEARCH = 1;
    //广告推送类型-智能聊
    public final static Integer AD_PUSH_TYPE_SMART_CHAT = 0;

    //广告推送策略-概率触发
    public final static Integer AD_PUSH_STRATEGY_CHANCE = 0;
    //广告推送策略-聊天次数触发
    public final static Integer AD_PUSH_STRATEGY_CHAT_COUNT = 1;

    //缓存时间30分钟
    private final static Integer AD_CACHE_PERIOD = 60 * 30;

    public final static Integer AD_PUSH_CLOSE = 0;//广告投放关闭(全局)
    public final static Integer AD_PUSH_OPEN = 1;//广告投放开启(全局)

    public final static Integer WX_PUB_AD_PUSH_STATE_OPEN = 1;//针对公众号主,公众号下广告投放状态(默认开启)
    public final static Integer WX_PUB_AD_PUSH_STATE_CLOSE = 0;//针对公众号主,公众号下广告投放状态
    public final static Integer WX_PUB_AD_PUSH_EXCLUDE = 0;//针对公众号主,公众号是否剔除广告投放
    public final static Integer WX_PUB_AD_PUSH_NOT_EXCLUDE = 1;//针对公众号主,公众号是否剔除广告投放(默认不剔除)

    public final static int AD_PUSH_STATE_NOT_YET = 0;//未投放
    public final static int AD_PUSH_STATE_PREPUSH = 1;//预投放
    public final static int AD_PUSH_STATE_PUSHING = 2;//投放中
    public final static int AD_PUSH_STATE_CLOSE = 3;//已结束

    private static final String AD_COVER_PIC_DIRECTORY = "/ad/cover-pic/";//portal广告封面图片存放目录
    private static final String AD_WX_PIC_DIRECTORY = "/ad/wx-pic";//微信广告图片存放目录

    private static final Float MIN_AD_INCOME = 0.01F;//最小广告单价


    @Autowired
    private AdMapper adMapper;

    @Autowired
    private AdPushService adPushService;

    @Autowired
    private WxPubTagService wxPubTagService;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private RAdWxPubMapper rAdWxPubMapper;

    @Autowired
    private RAdWxPubTagMapper rAdWxPubTagMapper;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private AdClickLogService adClickLogService;

    @Autowired
    private COSService cosService;

    @Autowired
    private WxPubAuthorizerRefreshTokenService wxPubAuthorizerRefreshTokenService;



    /**
     * 根据id获取广告id
     * @param id
     * @return
     */
    public Ad getAdById(Integer id){

        Ad ad = this.getAdByIdFromCache(id);
        if(ad != null){
            return ad;
        }

        ad = this.getAdByIdFromDb(id);

        String key = this.getCacheKeyById(id);
        redisCacheTemplate.setObject(key,ad);
        redisCacheTemplate.expire(key, AD_CACHE_PERIOD);

        return ad;

    }

    private Ad getAdByIdFromDb(Integer id){
        return adMapper.selectById(id);
    }

    private Ad getAdByIdFromCache(Integer id){
        String key = this.getCacheKeyById(id);
        Ad ad = redisCacheTemplate.getObject(key);
        return ad;
    }

    /**
     * 获取指定广告类型最新的一条广告
     * @param adType
     * @param pushType
     * @return
     */
    public Ad getNewestByType(Integer adType ,Integer pushType){
        return adMapper.selectNewestByType(adType, pushType);
    }

    /**
     * 获得广告的分页数据
     * @param page
     * @param pageSize
     * @return
     */
    public List<AdMgrListResp> getAds(Integer page, Integer pageSize){
        Integer startIndex = CommonUtils.page2startIndex(page, pageSize);
        List<Ad> ads = adMapper.selectByPage(startIndex, pageSize);

        List<AdMgrListResp> list = new ArrayList<>();

        for(Ad ad:ads){
            AdMgrListResp resp =new AdMgrListResp();
            BeanUtils.copyProperties(ad,resp);
            resp.setTotalClickCount(adClickLogService.getAdTotalClick(ad.getId()));

            Integer pushState = this.getAdPushState(ad);

            switch (pushState){
                case AD_PUSH_STATE_NOT_YET: resp.setPushState(pushState);   break;

                case AD_PUSH_STATE_PREPUSH: resp.setPushState(pushState);   break;

                case AD_PUSH_STATE_PUSHING: resp.setPushState(pushState);   break;

                case AD_PUSH_STATE_CLOSE:   resp.setPushState(pushState);   break;
            }

            list.add(resp);
        }
        return list;
    }

    /**
     * 更新广告
     * @param
     * @return
     */
    public void updateAd(AdUpdateReq req){
        //修改前的广告
        Ad oldAd = this.getAdById(req.getId());

        Ad ad = new Ad();

        //上传广告图片和封面图片并返回地址
        if(req.getWxPic() != null){
            String wxPicUrl = this.uploadPic(req.getWxPic(), AD_WX_PIC_DIRECTORY);
            ad.setPicUrl(wxPicUrl);
        }

        if(req.getCoverPic() != null){
            String coverPicUrl = this.uploadPic(req.getCoverPic(),AD_COVER_PIC_DIRECTORY);
            ad.setCoverPic(coverPicUrl);
        }

        ad.setId(req.getId());
        ad.setClickAmount(req.getClickAmount());
        ad.setAlias(req.getAlias());
        ad.setAdRecommendStatement(req.getAdRecommendStatement());
        ad.setAdType(req.getAdType());
        ad.setDescription(req.getDescription());
        ad.setPushType(req.getPushType());
        ad.setTextContent(req.getTextContent());
        ad.setUrl(req.getUrl());
        ad.setTitle(req.getTitle());
        ad.setPortalTitle(req.getPortalTitle());
        ad.setPortalContent(req.getPortalContent());
        ad.setClickAmount(req.getClickAmount());
        ad.setPushTime(CommonUtils.dateStartTime(req.getPushTime()));
        ad.setPrePushTime(CommonUtils.dateOffset(ad.getPushTime(),-1));
        ad.setPushStrategyType(req.getPushStrategyType());
        //全局广告投放按钮关闭后无法再开启
        if( AD_PUSH_OPEN.equals(oldAd.getIsOpen())){
            ad.setIsOpen(req.getIsOpen());
        }
        //广告投放结束,设置结束时间
        if(req.getIsOpen() == AD_PUSH_CLOSE && AD_PUSH_OPEN.equals(oldAd.getIsOpen())){
            ad.setCloseTime(new Date());
        }
        this.update(ad);

    }

    public void update(Ad ad){
        adMapper.updateByPrimaryKeySelective(ad);

        Ad dbAd = this.getAdByIdFromDb(ad.getId());

        String key = this.getCacheKeyById(ad.getId());

        redisCacheTemplate.setObject(key,dbAd);

        redisCacheTemplate.expire(key, AD_CACHE_PERIOD);
    }

    /**
     * 获得所有的数据数
     * @return
     */
    public Integer getCount(){
        return adMapper.count();
    }

    public Integer save(AddAd addAd) {

        Ad ad = new Ad();

        BeanUtils.copyProperties(addAd,ad,"pushTime","wxPic","coverPic","createAt");

        //上传广告图片和封面图片并返回地址
        String wxPicUrl = null;

        String coverPicUrl = null;

        if(addAd.getWxPic() != null){
            wxPicUrl = this.uploadPic(addAd.getWxPic(), AD_WX_PIC_DIRECTORY);
        }
        if(addAd.getCoverPic() != null){
            coverPicUrl = this.uploadPic(addAd.getCoverPic(),AD_COVER_PIC_DIRECTORY);
        }
        ad.setCreateAt(new Date());//广告创建时间

        ad.setCoverPic(coverPicUrl);

        ad.setPicUrl(wxPicUrl);

        ad.setIsOpen(AD_PUSH_OPEN);//广告投放状态默认开启

        ad.setPushTime(CommonUtils.dateStartTime(addAd.getPushTime()));

        ad.setPrePushTime(CommonUtils.dateOffset(ad.getPushTime(),-1));//预投放时间为投放时间的前一天

        return adMapper.insert(ad);
    }

    public Integer delete(Integer id) {

        return adMapper.detete(id);
    }

    /**
     * 获取admin广告投放页面数据
     * @param id  广告id
     * @return 页面数据
     */
    public AdDistribute2WxPubResp createAdDistribute2WxPubResp(Integer id) {

        //标签集合
        List<WxPubTag> tags = wxPubTagService.getAllTags();

        //上次投放该广告所确认的标签集
        List<RAdWxPubTag> tagsByAdId = rAdWxPubTagMapper.selectByAdId(id);
        List<Integer> tagIdsOfAd = tagsByAdId.stream().map(RAdWxPubTag::getWxPubTagId).collect(Collectors.toList());

        //上次投放广告划分的微信公众号集
        List<RAdWxPub> wxPubsByAdId = rAdWxPubMapper.selectByAdId(id);
        List<Integer> wxPubIds = wxPubsByAdId.stream().map(RAdWxPub::getWxPubId).collect(Collectors.toList());

        //标签和公众号对应关系数据集
        List<TagId2WxPubsItem> items = new ArrayList<>();
        for(WxPubTag tag:tags){
            TagId2WxPubsItem item = new TagId2WxPubsItem();
            List<WxPub> wxPubsByTagId = wxPubService.getWxPubsByTagId(tag.getId());
            List<AdWxPubResp> adWxPubResps = new ArrayList<>();
            for(WxPub wxPub:wxPubsByTagId){

                if(wxPub.getVerifyTypeInfo() == wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY
                        && wxPubAuthorizerRefreshTokenService.checkRefreshToken(wxPub.getAppId())){
                    AdWxPubResp adWxPubResp = new AdWxPubResp();
                    BeanUtils.copyProperties(wxPub,adWxPubResp);
                    adWxPubResps.add(adWxPubResp);
                }
            }
            item.setTagId(tag.getId());
            item.setWxPubs(adWxPubResps);
            items.add(item);
        }

        AdDistribute2WxPubResp resp = new AdDistribute2WxPubResp();
        resp.setTags(tags);
        resp.setTagIdsOfAd(tagIdsOfAd);
        resp.setWxPubIdsOfAd(wxPubIds);
        resp.setTagId2WxPubsItems(items);

        return resp;

    }

    /**
     * 广告投放"确认"操作
     * @param req
     */
    @Transactional
    public void distributeAdToWxPubConfirm(AdDistribute2WxPubConfirmReq req) {

        Integer adId = req.getAdId();

        //广告对应标签集合
        List<Integer> tagIds = req.getTagIds();

        //广告-标签关系维护
        List<RAdWxPubTag> rAdWxPubTags = new ArrayList<>();
        for(Integer tagId:tagIds){
            RAdWxPubTag rAdWxPubTag = new RAdWxPubTag();
            rAdWxPubTag.setAdId(adId);
            rAdWxPubTag.setWxPubTagId(tagId);
            rAdWxPubTags.add(rAdWxPubTag);
        }


        //被该广告划分到的公众号集合
        List<Integer> wxPubIds = req.getWxPubIds();


        //adId --> List<RAdWxPub>
        Map<String,Object> param = new HashMap<>();

        param.put("adId",adId);

        List<RAdWxPub> list = this.rAdWxPubMapper.selectByParamMap(param);

        boolean isEmpty = ListUtil.isEmpty(list);

        //若表中有数据先delete再insert
        if(!isEmpty){

            rAdWxPubMapper.deleteByAdId(adId);

            rAdWxPubTagMapper.deleteByAdId(adId);

        }else{
            //如果表中没有数据批量insert
            List<RAdWxPub> ret = new ArrayList<>();

            for(Integer wxPubId:wxPubIds){

                RAdWxPub rAdWxPub = new RAdWxPub();

                rAdWxPub.setWxPubId(wxPubId);

                rAdWxPub.setAdId(adId);

                rAdWxPub.setIsExclude(WX_PUB_AD_PUSH_NOT_EXCLUDE);

                rAdWxPub.setState(WX_PUB_AD_PUSH_STATE_OPEN);

                ret.add(rAdWxPub);
            }
            rAdWxPubMapper.batchInsert(ret);
        }

        rAdWxPubTagMapper.batchInsert(rAdWxPubTags);

        if(isEmpty){
            return ;
        }
        //广告原来存在于关系表中
        for(Integer wxPubId:wxPubIds){

            RAdWxPub rAdWxPub = new RAdWxPub();

            rAdWxPub.setWxPubId(wxPubId);

            rAdWxPub.setAdId(adId);

            for(int i = 0 ; i < list.size() ; i++){

                if(list.contains(rAdWxPub)){

                    RAdWxPub obj = list.get(i);

                    rAdWxPub.setState(obj.getState());

                    rAdWxPub.setIsExclude(obj.getIsExclude());

                    this.rAdWxPubMapper.insert(rAdWxPub);

                    break;

                }else{

                    rAdWxPub.setIsExclude(WX_PUB_AD_PUSH_NOT_EXCLUDE);

                    rAdWxPub.setState(WX_PUB_AD_PUSH_STATE_OPEN);

                    this.rAdWxPubMapper.insert(rAdWxPub);

                    break;
                }
            }

        }
    }

    /**
     * 广告是否为问问搜类型
     * @param ad
     * @return
     */
    private boolean isAskSearchAd(Ad ad){
        return AD_PUSH_TYPE_ASK_SEARCH.equals(ad.getPushType());
    }

    private interface Hook{

        boolean callback(Ad ad);
    }

    /**
     * 随机获取一条指定公众号下的陪聊宠广告
     * @param wxPubOriginId
     * @return
     */
    public Ad getChatPetPushAd(String wxPubOriginId,Integer wxFanId) {
        Ad ad = this.getPushAdTemplate(wxPubOriginId,wxFanId, new Hook() {

            @Override
            public boolean callback(Ad ad) {

                return !AD_PUSH_TYPE_CHAT_PET.equals(ad.getPushType());
            }
        });

        return ad;
    }

    /**
     * 随机获取一条接入指定公众号的智能聊广告
     * @param wxPubOriginId
     * @return
     */
    public Ad getSmartChatPushAd(String wxPubOriginId,Integer wxFanId){

        Ad ad = this.getPushAdTemplate(wxPubOriginId,wxFanId ,new Hook() {

            @Override
            public boolean callback(Ad ad) {

                return !AD_PUSH_TYPE_SMART_CHAT.equals(ad.getPushType());
            }
        });

        return ad;
    }

    /**
     * 随机获取一条接入指定公众号的问问搜广告
     * 图文广告
     * @param wxPubOriginId
     * @return
     */
    public Ad getAskSearchPushAd(String wxPubOriginId, Integer wxFanId){
        Ad ad = this.getPushAdTemplate(wxPubOriginId,wxFanId, new Hook() {

            @Override
            public boolean callback(Ad ad) {

                return !AD_PUSH_TYPE_ASK_SEARCH.equals(ad.getPushType()) || !AD_NEWS_TYPE.equals(ad.getAdType());
            }
        });

        return ad;
    }

    /**
     * 随机获取指定公众号下的一条广告 模板方法
     * @param wxPubOriginId :公众号
     * @param judgePushTypeHook:callback
     * @return
     */
    private Ad getPushAdTemplate(String wxPubOriginId,Integer wxFanId,Hook judgePushTypeHook){
        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);

        //未认证及未授权公众号不推送广告
        if(wxPubService.WX_PUB_VERIFY_TYPE_UN_VERIFY.intValue()  == wxPub.getVerifyTypeInfo().intValue()
                || !wxPubAuthorizerRefreshTokenService.checkRefreshToken(wxPub.getAppId())){
            return null;
        }

        Integer wxPubId = wxPub.getId();

        Map<String,Object> param = new HashMap<>();

        param.put("wxPubId",wxPubId);

        List<RAdWxPub> rAdWxPubs = rAdWxPubMapper.selectByParamMap(param);

        if(ListUtil.isEmpty(rAdWxPubs)){
            return null;
        }

        List<Ad> ads = new ArrayList<>();

        for(RAdWxPub obj:rAdWxPubs){
            //满足广告投放的条件:1,智能聊广告(排除问问搜) 2,投放开关开启 3,正在投放 4,公众号主开启投放且公众号未被剔除
            Ad ad = this.getAdById(obj.getAdId());

            if(AD_PUSH_CLOSE.equals(ad.getIsOpen()) //广告开发
                    ||  judgePushTypeHook.callback(ad)//宠物类型
                    ||  AD_PUSH_STATE_PUSHING != this.getAdPushState(ad)//广告投放状态
                    ||  WX_PUB_AD_PUSH_STATE_CLOSE.equals(obj.getState())//公众号主设置广告投放状态
                    ||  WX_PUB_AD_PUSH_EXCLUDE.equals(obj.getIsExclude())//公众号是否被剔除
                    ){

                continue;
            }

            ads.add(ad);
        }

        if(ListUtil.isEmpty(ads)){
            return null;
        }

        List<Ad> shotAds = this.excludePushedAdFromList(ads, wxFanId);

        if(ListUtil.isEmpty(shotAds)){
            return null;
        }

        //从集合中随机获取一条广告
        return shotAds.get(RandomUtil.randomIndex(shotAds.size()));
    }

    /**
     * 从当前可投放的广告集中剔除粉丝已经点击过的广告并返回剔除后的广告集
     * @param ads : 当前可投放的广告集
     * @param wxFanId : 粉丝Id
     * @return
     */
    private List<Ad> excludePushedAdFromList(List<Ad> ads,Integer wxFanId){
        List<Ad> shotAds = new ArrayList<>();

        for(Ad ad : ads){
            Boolean isPush = adPushService.isAdHasPushedWxFanId(wxFanId, ad.getId());

            if(!isPush){
                shotAds.add(ad);
            }
        }

        return shotAds;
    }

    /**
     * 获取指定公众号对应的广告集
     * @param wxPubId
     * @return
     */
    public List<Ad> getAdByWxPubId(Integer wxPubId){

        Map<String,Object> param = new HashMap<>();

        param.put("wxPubId",wxPubId);

        List<RAdWxPub> rs = rAdWxPubMapper.selectByParamMap(param);

        List<Ad> ads = new ArrayList<>();

        if(ListUtil.isEmpty(rs)){
            return ads;
        }
        //遍历公众号-广告关系,得到公众号对应广告集
        rs.stream().forEach(obj -> ads.add(this.getAdById(obj.getAdId())));

        return ads;
    }

    private String getCacheKeyById(Integer adId){
        String key = RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "Ad:ad:" + adId;
        return key;
    }


    /**
     * 上传图片
     * @param file
     * @param directory
     * @return
     */
    public String uploadPic(MultipartFile file,String directory){
        String cosPath = this.createPicPath(file,directory);
        String cosPicUrl = null;
        try {
            String pathJson = cosService.uploadFile(cosPath, IOUtils.toByteArray(file.getInputStream()));
            String dataJson = JsonHelper.getStringFromJson(pathJson, "data");
            cosPicUrl = JsonHelper.getStringFromJson(dataJson,"access_url");
        } catch (IOException e) {
            Log.e(e);
        }
        return cosPicUrl;
    }

    /**
     * 生成文件名
     * 文件名: 年月日时分秒-用户id.后缀
     * @param file
     * @param directory 文件目录
     * @return
     */
    private String createPicPath(MultipartFile file,String directory){
        String fileName = file.getOriginalFilename();
        String suffix = CommonUtils.getFilenamePostfix(fileName);
        String sFormat = CommonUtils.dateFormat(new Date(), "yyyyMMddHHmmss");
        StringBuilder sb = new StringBuilder();
        sb.append(directory).append(sFormat).append(".").append(suffix);
        return sb.toString();
    }

    /**
     * 是否达到最小广告单价
     * @param income
     * @return
     */
    public boolean isMoreThanMinIncome(Float income){
        return income >= MIN_AD_INCOME;
    }


    /**
     * 当前广告点击数到达最大点击数时关闭广告投放
     * @param ad
     * @return
     */
    public void closeAdPushWhenReachMaxClick(Ad ad){
        //如果广告投放已经关闭则return
        if(AD_PUSH_CLOSE.equals(ad.getIsOpen())){
            return;
        }
        Ad newAd = new Ad();
        newAd.setIsOpen(AD_PUSH_CLOSE);
        newAd.setCloseTime(new Date());
        newAd.setId(ad.getId());
        this.update(newAd);
    }

    /**
     * 判断广告是否达到最大点击数
     * @param ad
     * @return
     */
    public boolean isReachMaxClick(Ad ad) throws BizException {
        Integer currentClickAmount = adClickLogService.getAdTotalClick(ad.getId());

        Integer maxClickAmount = ad.getClickAmount();

        if(maxClickAmount == null || maxClickAmount <= 0 ){
            throw new BizException("该广告未设置最大点击数");
        }
        return currentClickAmount >= maxClickAmount;
    }

    /**
     * 判断广告当前的投放状态
     *
     * @param ad
     * @return 0:未投放 1:预投放 2:投放中 3:已结束
     */
    public Integer getAdPushState(Ad ad){

        long now = new Date().getTime();

        long preTime = 0;
        long pushTime = 0;

        if(ad.getPrePushTime() != null){
            preTime = ad.getPrePushTime().getTime();
        }
        if(ad.getPushTime() != null){
            pushTime = ad.getPushTime().getTime();
        }

        if(AD_PUSH_CLOSE.equals(ad.getIsOpen())){

            return AD_PUSH_STATE_CLOSE;

        }else if(AD_PUSH_OPEN.equals(ad.getIsOpen())){
            if(now < preTime){

                return AD_PUSH_STATE_NOT_YET;

            }else if (now >= preTime && now < pushTime) {

                return AD_PUSH_STATE_PREPUSH;

            } else if (now >= pushTime) {

                return AD_PUSH_STATE_PUSHING;
            }
        }
        return null;
    }



}
