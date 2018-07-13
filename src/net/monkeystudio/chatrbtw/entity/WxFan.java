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
<<<<<<< HEAD
    private Integer wxMiniProgramId;
    private Integer wxServiceType;

    public Integer getWxMimiProgramId() {
        return wxMiniProgramId;
    }

    public void setWxMiniProgramId(Integer wxMiniProgramId) {
        this.wxMiniProgramId = wxMiniProgramId;
    }
=======
    private Integer miniProgramId;
    private Integer wxServiceType;

>>>>>>> 5865b1ec39e3c9fbc72ce31c9c461ef048c2d416

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

    public Integer getMiniProgramId() {
        return miniProgramId;
    }

    public void setMiniProgramId(Integer miniProgramId) {
        this.miniProgramId = miniProgramId;
    }
}