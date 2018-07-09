package net.monkeystudio.chatrbtw.entity;

import java.io.Serializable;

//表名e_wx_fans
public class WxFan implements Serializable{
    private Integer id;
    private String nickname;
    private Integer sex;
    private String city;
    private String country;
    private String province;
    private String unionId;
    private Integer subscribeTime;
    private String wxPubOriginId;
    private String wxFanOpenId;
    private Long createAt;
    private String headImgUrl;
    private Integer wxMiniAppId;
    private Integer wxServiceType;

    public Integer getWxMiniAppId() {
        return wxMiniAppId;
    }

    public void setWxMiniAppId(Integer wxMiniAppId) {
        this.wxMiniAppId = wxMiniAppId;
    }

    public Integer getWxServiceType() {
        return wxServiceType;
    }

    public void setWxServiceType(Integer wxServiceType) {
        this.wxServiceType = wxServiceType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public Integer getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Integer subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }

    public String getWxFanOpenId() {
        return wxFanOpenId;
    }

    public void setWxFanOpenId(String wxFanOpenId) {
        this.wxFanOpenId = wxFanOpenId;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    @Override
    public String toString() {
        return "WxFan{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", unionId=" + unionId +
                ", subscribeTime=" + subscribeTime +
                ", wxPubOriginId='" + wxPubOriginId + '\'' +
                ", wxFanOpenId='" + wxFanOpenId + '\'' +
                ", createAt=" + createAt +
                '}';
    }
}