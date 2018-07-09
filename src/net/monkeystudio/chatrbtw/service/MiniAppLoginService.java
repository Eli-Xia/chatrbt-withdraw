package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.sdk.wx.WxMiniAppHelper;
import net.monkeystudio.chatrbtw.sdk.wx.bean.miniapp.LoginVerifyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class MiniAppLoginService {
    @Autowired
    private WxMiniAppHelper wxMiniAppHelper;
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;
    @Autowired
    private WxFanService wxFanService;

    public String loginHandle(String jsCode){
        LoginVerifyInfo loginVerifyInfo = wxMiniAppHelper.fetchLoginVerifyInfo(jsCode);
        
        String openId = loginVerifyInfo.getOpneId();

        String sessionKey = loginVerifyInfo.getSessionKey();

        String token = CommonUtils.randomUUID();

        String key = this.getSessionTokenCacheKey(token);

        String value = openId + "+" + sessionKey;

        redisCacheTemplate.setString(key,value);

        redisCacheTemplate.expire(key,7200);//缓存两小时

        //去数据库或cache中查找openId是否存在,存在说明不是第一次登录,如果不存在wxFanId insert unionId,openId,并把数据放入缓存之中
        WxFan wxFan = wxFanService.getWxFan(openId, wxFanService.LUCK_CAT_MINI_APP_ID);
        if(wxFan == null){
            WxFan miniAppFan = new WxFan();
            miniAppFan.setWxFanOpenId(openId);
            miniAppFan.setWxMiniAppId(wxFanService.LUCK_CAT_MINI_APP_ID);
            miniAppFan.setWxServiceType(wxFanService.WX_SERVICE_TYPE_MINI_APP);
            wxFanService.save(miniAppFan);

            //生成一只宠物
        }

        return token;
    }

    /**
     * 获取redis sesion的key值
     * @param token
     * @return
     */
    public String getSessionTokenCacheKey(String token){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "miniAppSessionToken:" + token;
    }

    /**
     * 获取redis session中的openId
     * @param cacheKey
     * @return
     */
    public String getOpenIdFromRedisSession(String cacheKey){
        String value = redisCacheTemplate.getString(cacheKey);
        return value.split("\\+")[1];
    }

}
