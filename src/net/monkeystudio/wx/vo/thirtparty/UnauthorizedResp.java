package net.monkeystudio.wx.vo.thirtparty;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.xml.bind.annotation.*;

/**
 * 取消授权的时候传回的VO类
 * Created by bint on 2017/11/14.
 */
public class UnauthorizedResp {

    @XStreamAlias("AppId")
    private String appId;

    @XStreamAlias("CreateTime")
    private String createTime;

    @XStreamAlias("InfoType")
    private String infoType;

    @XStreamAlias("AuthorizerAppid")
    private String wxPubOpenId;


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

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getWxPubOpenId() {
        return wxPubOpenId;
    }

    public void setWxPubOpenId(String wxPubOpenId) {
        this.wxPubOpenId = wxPubOpenId;
    }

    @Override
    public String toString() {
        return "UnauthorizedResp{" +
                "appId='" + appId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", infoType='" + infoType + '\'' +
                ", wxPubOpenId='" + wxPubOpenId + '\'' +
                '}';
    }
}
