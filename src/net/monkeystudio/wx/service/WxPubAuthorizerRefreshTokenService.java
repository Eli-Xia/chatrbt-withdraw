package net.monkeystudio.wx.service;

import net.monkeystudio.chatrbtw.entity.WxPubAuthorizerRefreshToken;
import net.monkeystudio.chatrbtw.mapper.WxPubAuthorizerRefreshTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2017/11/2.
 */
    @Service
    public class WxPubAuthorizerRefreshTokenService {

        //未验证状态
        public final Integer UN_AUTHORIZED_STATUS = 0;

        //已经验证
        public final Integer AUTHORIZED_STATUS = 1;


        @Autowired
    private WxPubAuthorizerRefreshTokenMapper wxPubAuthorizerRefreshTokenMapper;

    private void save(String authorizerAppid ,String authorizerRefreshToken){

        WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken = new WxPubAuthorizerRefreshToken();
        wxPubAuthorizerRefreshToken.setAuthorizerAppid(authorizerAppid);
        wxPubAuthorizerRefreshToken.setAuthorizerRefreshToken(authorizerRefreshToken);
        wxPubAuthorizerRefreshToken.setStatus(AUTHORIZED_STATUS);

        wxPubAuthorizerRefreshTokenMapper.insert(wxPubAuthorizerRefreshToken);
    }

    public void saveOrUpdate(String authorizerAppid ,String authorizerRefreshToken){

        WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken = this.getByAuthorizerAppId(authorizerAppid);
        if(wxPubAuthorizerRefreshToken != null){

            wxPubAuthorizerRefreshToken.setStatus(AUTHORIZED_STATUS);
            wxPubAuthorizerRefreshToken.setAuthorizerRefreshToken(authorizerRefreshToken);
            this.updateByAppId(wxPubAuthorizerRefreshToken);
            return ;
        }

        this.save(authorizerAppid , authorizerRefreshToken);
    }


    public WxPubAuthorizerRefreshToken getByAuthorizerAppId(String authorizerAppId){
        WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken = wxPubAuthorizerRefreshTokenMapper.selectByAuthorizerAppId(authorizerAppId);
        return wxPubAuthorizerRefreshToken;
    }

    /**
     * RefreshToken是否还有效
     * @param wxPubAppId
     * @return
     */
    public Boolean checkRefreshToken(String wxPubAppId){
        WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken = this.getByAuthorizerAppId(wxPubAppId);

        if(wxPubAuthorizerRefreshToken == null){
            return false;
        }

        if(AUTHORIZED_STATUS.equals(wxPubAuthorizerRefreshToken.getStatus())){
            return true;
        }

        return false;
    }

    /**
     * 更新refreshToken
     * @param authorizerAppId
     * @param authorizerRefreshToken
     * @return
     */
    /*private Integer updateRefreshTokenByAppId(String authorizerAppId ,String authorizerRefreshToken){

        WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken = new WxPubAuthorizerRefreshToken();

        wxPubAuthorizerRefreshToken.setAuthorizerAppid(authorizerAppId);
        wxPubAuthorizerRefreshToken.setAuthorizerRefreshToken(authorizerRefreshToken);

        return this.updateByAppId(wxPubAuthorizerRefreshToken);

    }*/

    /**
     * 取消授权
     * @param wxPubOpenId
     */
    public void unAuthorized(String wxPubOpenId){
        this.upateStatus(wxPubOpenId, UN_AUTHORIZED_STATUS);
    }

    /**
     * 更新状态
     * @param wxOpenId
     * @param status
     * @return
     */
    private Integer upateStatus(String wxOpenId ,Integer status) {
        WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken = new WxPubAuthorizerRefreshToken();

        wxPubAuthorizerRefreshToken.setAuthorizerAppid(wxOpenId);
        wxPubAuthorizerRefreshToken.setStatus(status);

        return this.updateByAppId(wxPubAuthorizerRefreshToken);
    }

    private Integer updateByAppId(WxPubAuthorizerRefreshToken wxPubAuthorizerRefreshToken){
        return wxPubAuthorizerRefreshTokenMapper.updateByAppId(wxPubAuthorizerRefreshToken);
    }
}
