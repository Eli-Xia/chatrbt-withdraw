package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.WxFanMapper;
import net.monkeystudio.chatrbtw.sdk.wx.WxFanHelper;
import net.monkeystudio.wx.service.WxPubService;
import net.monkeystudio.wx.vo.user.WxFanBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bint on 2017/12/5.
 */
@Service
public class WxFanService {

    private static final Integer WX_FAN_CACHE_PERIOD = 60 * 30;

    public static final Integer LUCK_CAT_MINI_APP_ID = 1;

    public static final Integer WX_SERVICE_TYPE_PUB = 0;

    public static final Integer WX_SERVICE_TYPE_MINI_APP = 1;

    @Autowired
    private WxFanMapper wxFanMapper ;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxFanHelper wxFanHelper;

    @Autowired
    private WxPubService wxPubService;


    public WxFan getById(Integer id){
        WxFan wxFan = wxFanMapper.selectById(id);
        return wxFan;
    }

    public WxFan getWxFan(String wxFanOpenId,Integer miniProgramId){
        WxFan wxFan = this.getWxFanFromCache(null, wxFanOpenId, miniProgramId);

        if(wxFan != null && wxFan.getId() != null){
            return wxFan;
        }

        wxFan = this.getWxFanFromDb(null,wxFanOpenId,miniProgramId);

        if(wxFan != null){
            this.setWxFanCache(null,wxFanOpenId,miniProgramId,wxFan);
            return wxFan;
        }

        return null;
    }

    /**
     * 获取粉丝基础信息
     * @param wxPubOriginId 公众号原始Id
     * @param wxFanOpenId 微信粉丝OpenId
     * @return
     */
    public WxFan getWxFan(String wxPubOriginId ,String wxFanOpenId){

        WxFan wxFan = this.getWxFanFromCache(wxPubOriginId , wxFanOpenId , null);

        if(wxFan != null && wxFan.getId() != null ){
            return wxFan;
        }

        wxFan = this.getWxFanFromDb(wxPubOriginId,wxFanOpenId,null);

        if(wxFan != null){
            this.setWxFanCache(wxPubOriginId,wxFanOpenId,null,wxFan);
            return wxFan;
        }

        String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);
        WxFanBaseInfo wxFanBaseInfo = null;
        try {
            wxFanBaseInfo = wxFanHelper.fetcWxhUserBaseInfo(wxFanOpenId,wxPubAppId);
        } catch (BizException e) {
            Log.e(e);
            return null;
        }

        wxFan = this.wxUserBaseInfoHandle(wxFanBaseInfo,wxPubOriginId,wxFanOpenId);

        return wxFan;
    }

    private void setWxFanCache(String wxPubOriginId ,String wxFanOpenId ,Integer miniProgramId,WxFan wxFan){
        String cacheKey = this.getWxFanCacheKey(wxPubOriginId,wxFanOpenId,miniProgramId);
        redisCacheTemplate.setObject(cacheKey,wxFan );
        redisCacheTemplate.expire(cacheKey, WX_FAN_CACHE_PERIOD);
    }

    private WxFan getWxFanFromDb(String wxPubOriginId ,String wxFanOpenId ,Integer miniProgramId){
        List<WxFan> list = wxFanMapper.select(wxPubOriginId, wxFanOpenId, miniProgramId);

        if(list == null || list.size() == 0){
            return null;
        }

        return list.get(0);
    }

    private WxFan getWxFanFromCache(String wxPubOriginId , String wxFanOpenId ,Integer miniProgramId){
        String key = this.getWxFanCacheKey(wxPubOriginId,wxFanOpenId,miniProgramId);
        return redisCacheTemplate.getObject(key);
    }

    private String getWxFanCacheKey(String wxPubOriginId ,String wxFanOpenId ,Integer miniProgramId ){
        String key = RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "WxFan:" + wxFanOpenId + ":";
        if(wxPubOriginId != null){
            key.concat(wxPubOriginId);
        }
        if(miniProgramId != null){
            key.concat(miniProgramId.toString());
        }
        return key;
    }

    /**
     * 保存微信粉丝基础信息
     * @param wxFan
     * @return
     */
    public Integer save(WxFan wxFan){

        /*String nickname = wxFan.getNickname();
        nickname = this.filter(nickname);
        wxFan.setNickname(nickname);*/

        Integer result = null;

        try{
            result = wxFanMapper.insert(wxFan);
        }catch (Exception e){
            Log.e(e);
        }

        this.setWxFanCache(wxFan.getWxPubOriginId(),wxFan.getWxFanOpenId(),wxFan.getMiniProgramId(),wxFan);

        return result;
    }


    /**
     * 过滤非utf-8的数据
     * @param str
     * @return
     */
    private String filter(String str){
        if(str.trim().isEmpty()){
            return str;
        }
        String pattern="[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
        String reStr="";
        Pattern emoji = Pattern.compile(pattern);
        Matcher emojiMatcher=emoji.matcher(str);
        str=emojiMatcher.replaceAll(reStr);
        return str;
    }

    private WxFan wxUserBaseInfoHandle(WxFanBaseInfo wxFanBaseInfo ,String wxPubOriginId , String wxFanOpenId){
        //如果该公众号没有授权，则获取不到粉丝的信息
        if(wxFanBaseInfo == null){
            Log.i("WxFanBaseInfo is null ,the wxPubOriginId [?] , wxFanOpenId [?]", wxPubOriginId,wxFanOpenId);
            return null;
        }

        WxFan wxFan = new WxFan();
        BeanUtils.copyProperties(wxFanBaseInfo,wxFan);

        wxFan.setWxPubOriginId(wxPubOriginId);
        wxFan.setWxFanOpenId(wxFanOpenId);

        wxFan.setCreateAt(TimeUtil.getCurrentTimestamp());

        this.save(wxFan);
        return wxFan;
    }

    /**
     * 修正微信粉丝信息
     * @param wxPubOriginId
     * @param wxFanOpenId
     */
    public void reviseWxPub(String wxPubOriginId ,String wxFanOpenId){
        String wxPubAppId = wxPubService.getWxPubAppIdByOrginId(wxPubOriginId);
        WxFanBaseInfo wxFanBaseInfo = null;
        try {
            wxFanBaseInfo = wxFanHelper.fetcWxhUserBaseInfo(wxFanOpenId,wxPubAppId);
        } catch (BizException e) {
            Log.e(e);
        }

        WxFan wxFan = this.getWxFan(wxPubOriginId, wxFanOpenId);

        BeanUtils.copyProperties(wxFanBaseInfo,wxFan);

        this.update(wxFan);

        wxFan = this.getById(wxFan.getId());
        this.setWxFanCache(wxPubOriginId, wxFanOpenId ,null,wxFan);
    }

    public Integer update(WxFan wxFan) {
        return wxFanMapper.update(wxFan);
    }

    public boolean isFans(String wxPubOriginId ,String wxFanOpenId){
        boolean isFans = false;
        WxFan wxFan = this.getWxFanFromDb(wxPubOriginId, wxFanOpenId,null);
        if(wxFan != null){
            isFans = true;
        }
        return isFans;
    }


    /**
     * FIXME 应该加上小程序id
     * 根据小程序id和用户openId获取粉丝
     * @param wxFanOpenId
     * @return
     */
    public WxFan getByMiniAppIdAndOpenId(String wxFanOpenId){
        List<WxFan> wxFanList = wxFanMapper.select(null, wxFanOpenId, null);

        if(ListUtil.isEmpty(wxFanList)){
            return null;
        }

        return wxFanList.get(0);
    }


    /**
     * 根据公众号原始id获取该公众号的粉丝
     * @param wxPubOriginid
     * @return
     */
    public List<WxFan> getListByWxPubOriginid(String wxPubOriginid){
        return wxFanMapper.selectListByWxPubOriginId(wxPubOriginid);
    }


    /**
     * 通过小程序id获取对应的粉丝
     * @param miniProgramId
     * @return
     */
    public List<WxFan> getListByMiniProgramId(Integer miniProgramId){
        return wxFanMapper.selectByMiniProgramId(miniProgramId);
    }
}
