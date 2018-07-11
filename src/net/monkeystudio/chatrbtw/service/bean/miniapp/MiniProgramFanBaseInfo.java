package net.monkeystudio.chatrbtw.service.bean.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author xiaxin
 */
public class MiniProgramFanBaseInfo {
    @JsonProperty("openId")
    private String openId;
    @JsonProperty("nickName")
    private String nickname;
    @JsonProperty("gender")
    private Integer sex;
    @JsonProperty("city")
    private String city;
    @JsonProperty("province")
    private String province;
    @JsonProperty("country")
    private String country;
    @JsonProperty("avatarUrl")
    private String headImgUrl;
    @JsonProperty("unionId")
    private String unionId;
    @JsonProperty("watermark")
    private Watermark watermark;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public Watermark getWatermark() {
        return watermark;
    }

    public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }
}
