package net.monkeystudio.wx.mp.beam;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by bint on 2017/10/31.
 */
public class ComponentVerifyTicket {

    @XStreamAlias("AppId")
    private String appId ;

    @XStreamAlias("ComponentVerifyTicket")
    private String ticket;

    @XStreamAlias("InfoType")
    private String infoType;

    @XStreamAlias("CreateTime")
    private String createTime;

    @XStreamAlias("AuthorizerAppid")
    private String authorizerAppId;

    @XStreamAlias("AuthorizationCode")
    private String authorizationCode;

    @XStreamAlias("AuthorizationCodeExpiredTime")
    private String authorizationCodeExpiredTime;

    @XStreamAlias("PreAuthCode")
    private String preAuthCode;

    //@XStreamAlias("ComponentVerifyTicket")
    //private String componentVerifyTicket;


    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getAuthorizerAppId() {
        return authorizerAppId;
    }

    public void setAuthorizerAppId(String authorizerAppId) {
        this.authorizerAppId = authorizerAppId;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getAuthorizationCodeExpiredTime() {
        return authorizationCodeExpiredTime;
    }

    public void setAuthorizationCodeExpiredTime(String authorizationCodeExpiredTime) {
        this.authorizationCodeExpiredTime = authorizationCodeExpiredTime;
    }

    public String getPreAuthCode() {
        return preAuthCode;
    }

    public void setPreAuthCode(String preAuthCode) {
        this.preAuthCode = preAuthCode;
    }
}
