package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxPubAuthorizerRefreshToken;

/**
 * Created by bint on 2017/11/2.
 */
public interface WxPubAuthorizerRefreshTokenMapper {

    int insert(WxPubAuthorizerRefreshToken record);

    WxPubAuthorizerRefreshToken selectByAuthorizerAppId(String appId);

    int updateByAppId(WxPubAuthorizerRefreshToken record);


}
