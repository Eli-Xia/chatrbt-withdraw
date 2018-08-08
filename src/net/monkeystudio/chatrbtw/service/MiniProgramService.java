package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.chatrbtw.entity.MiniProgram;
import net.monkeystudio.chatrbtw.mapper.MiniProgramMapper;
import net.monkeystudio.chatrbtw.sdk.wx.WxMiniProgramHelper;
import net.monkeystudio.chatrbtw.sdk.wx.bean.miniapp.MiniProgramAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author xiaxin
 */
@Service
public class MiniProgramService {
    @Autowired
    private MiniProgramMapper wxMiniProgramMapper;
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxMiniProgramHelper wxMiniProgramHelper;

    public MiniProgram getById(Integer id){
        return wxMiniProgramMapper.selectByPrimaryKey(id);
    }


    /**
     * 获取小程序的accessToken
     * @param miniProgramId
     * @return
     */
    public String getAcceessToken(Integer miniProgramId){

        String accessTokenFromCache = this.getAccessTokenFromCache(miniProgramId);

        if(accessTokenFromCache != null){
            return accessTokenFromCache;
        }

        MiniProgram miniProgram = this.getById(miniProgramId);

        String appId = miniProgram.getAppId();
        String appSecret = miniProgram.getAppSecret();

        MiniProgramAccessToken accessTokenObj = wxMiniProgramHelper.getAccessToken(appId, appSecret);

        String accessToken = accessTokenObj.getAccessToken();

        Integer expiresIn = accessTokenObj.getExpiresIn();

        this.setAccessTokenCache(miniProgramId, accessToken, expiresIn);

        return accessToken;
    }


    private String getAccessTokenFromCache(Integer miniProgramId){
        String key = this.getMiniProgramTokenKey(miniProgramId);
        String acceessToken = redisCacheTemplate.getString(key);

        return acceessToken;
    }

    private void setAccessTokenCache(Integer miniProgramId , String accessToken ,Integer expiresIn){
        String key = this.getMiniProgramTokenKey(miniProgramId);
        redisCacheTemplate.setStringWithExpire(key, accessToken, expiresIn);
    }


    private String getMiniProgramTokenKey(Integer miniProgramId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "mini-program:access-token:" + String.valueOf(miniProgramId);
    }

}
