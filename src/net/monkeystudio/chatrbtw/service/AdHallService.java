package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.entity.RAdWxPub;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.mapper.RAdWxPubMapper;
import net.monkeystudio.chatrbtw.service.bean.ad.*;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaxin
 */
@Service
public class AdHallService {
    
    @Autowired
    private WxPubService wxPubService;
    @Autowired
    private AdService adService;
    @Autowired
    private RAdWxPubMapper rAdWxPubMapper;

    private final static Integer WX_PUB_IS_EXCLUDE = 0;//剔除
    private final static Integer WX_PUB_IS_NOT_EXCLUDE = 1;//未剔除
    private final static Integer AD_PUSH_SWITCH_OPEN = 1;//广告投放开启
    private final static Integer AD_PUSH_SWITCH_CLOSE = 0;//广告投放关闭

    //广告大厅广告列表
    public AdHallResp getAds(Integer userId) {
        AdHallResp resp = new AdHallResp();

        List<WxPub> wxPubs = wxPubService.getWxPubsByUserId(userId);//该广告主下公众号集

        List<Integer> wxPubIds = wxPubs.stream().map(WxPub::getId).collect(Collectors.toList());//该广告主下公众号id集

        //公众号主-->所有公众号-->广告
        Set<Ad> adSet = new HashSet<>();

        for (WxPub wxPub : wxPubs) {

            List<Ad> ads = adService.getAdByWxPubId(wxPub.getId());

            adSet.addAll(ads);
        }
        if(CollectionUtils.isEmpty(adSet)){
            return resp;
        }
        List<AdHallItem> pushItems = new ArrayList<>();

        List<AdHallItem> prePushItems = new ArrayList<>();

        List<AdHallItem> closePushItems = new ArrayList<>();

        for (Ad ad : adSet) {

            AdHallItem item = new AdHallItem();

            item.setId(ad.getId());

            item.setCoverPic(ad.getCoverPic());

            item.setPushTime(ad.getPushTime());

            item.setPortalTitle(ad.getPortalTitle());
            //广告大厅item投放状态
            Map<String, Object> paramMap = new HashMap<>();

            paramMap.put("adId", ad.getId());//这个广告是在公众号主下的

            List<RAdWxPub> rs = this.rAdWxPubMapper.selectByParamMap(paramMap);

            if (!CollectionUtils.isEmpty(rs)) {
                for (RAdWxPub obj:rs){
                    if(wxPubIds.contains(obj.getWxPubId())){
                        item.setState(obj.getState());
                        break;
                    }
                }
            }
            Integer pushState = adService.getAdPushState(ad);

            switch (pushState){
                case AdService.AD_PUSH_STATE_PREPUSH:prePushItems.add(item);break;

                case AdService.AD_PUSH_STATE_PUSHING:pushItems.add(item);break;

                case AdService.AD_PUSH_STATE_CLOSE:closePushItems.add(item);break;
            }
        }
        resp.setPrePushList(prePushItems);

        resp.setPushList(pushItems);

        resp.setClosePushList(closePushItems);

        return resp;
    }

    //修改广告投放开关状态
    public void updateItemState(Integer userId,Integer state,Integer adId) {
        //update rAdWxPub set state = ? where wxPubId = ? and adId = ?
        //如果是从关闭到开启状态  需要让公众号主下的这条广告下的所有公众号都是未剔除状态的.
        List<WxPub> wxPubs = wxPubService.getWxPubsByUserId(userId);

        for(WxPub wxPub:wxPubs){

            Map<String,Object> colParam = new HashMap<>();//字段
            Map<String,Object> conParam = new HashMap<>();//条件

            conParam.put("wxPubId",wxPub.getId());

            conParam.put("adId",adId);

            colParam.put("state",state);

            if(state == AD_PUSH_SWITCH_OPEN){//当开启广告投放时,所有公众号应勾选,需更新isExclude
                colParam.put("isExclude",WX_PUB_IS_NOT_EXCLUDE);
            }

            this.rAdWxPubMapper.updateByParamMap(colParam,conParam);
        }



    }

    //广告大厅广告详情
    public AdHallItemDetail getItemDetail(Integer userId, Integer adId) {

        Ad ad = adService.getAdById(adId);

        AdHallItemDetail detail = new AdHallItemDetail();

        detail.setAdId(adId);

        detail.setPortalTitle(ad.getPortalTitle());

        detail.setPushTime(ad.getPushTime());

        detail.setCloseTime(ad.getCloseTime());

        detail.setAdType(ad.getAdType());

        detail.setPortalContent(ad.getPortalContent());

        List<WxPub> wxPubs = wxPubService.getWxPubsByUserId(userId);

        Set<RAdWxPub> set = new HashSet<>();

        for(WxPub wxPub:wxPubs){
            Map<String,Object> param = new HashMap<>();

            param.put("wxPubId",wxPub.getId());

            param.put("adId",adId);

            List<RAdWxPub> rs = this.rAdWxPubMapper.selectByParamMap(param);

            set.addAll(rs);
        }
        List<AdHallItemDetailWxPub> list = new ArrayList<>();

        for(RAdWxPub obj:set){
            AdHallItemDetailWxPub adHallItemDetailWxPub = new AdHallItemDetailWxPub();
            adHallItemDetailWxPub.setIsExclude(obj.getIsExclude());
            adHallItemDetailWxPub.setNickname((wxPubService.getWxPubById(obj.getWxPubId())).getNickname());
            adHallItemDetailWxPub.setWxPubId(obj.getWxPubId());
            list.add(adHallItemDetailWxPub);
        }
        detail.setWxPubs(list);

        return detail;
    }

    public void detailConfirm(AdHallDetailConfirmReq req) {
        List<Integer> excludes = req.getExcludeWxPubIds();//剔除的公众号

        List<Integer> includes = req.getIncludeWxPubIds();//未剔除的公众号

        //update table set isExclude = ? where adId = ? and wxPubId = ?
        for(Integer wxPubId:includes){
            Map<String,Object> conditionMap = new HashMap();
            Map<String,Object> columnMap = new HashMap<>();
            columnMap.put("isExclude",WX_PUB_IS_NOT_EXCLUDE);
            conditionMap.put("wxPubId",wxPubId);
            this.rAdWxPubMapper.updateByParamMap(columnMap,conditionMap);
        }

        for(Integer wxPubId:excludes){
            Map<String,Object> conditionMap = new HashMap();
            Map<String,Object> columnMap = new HashMap<>();
            columnMap.put("isExclude",WX_PUB_IS_EXCLUDE);
            conditionMap.put("wxPubId",wxPubId);

            if(CollectionUtils.isEmpty(includes)){//如果全部剔除  update table set state = 0,isExclude = 0 where adId = ? and wxPub = ?
                columnMap.put("state",AD_PUSH_SWITCH_CLOSE);
            }
            this.rAdWxPubMapper.updateByParamMap(columnMap,conditionMap);
        }


    }
}
