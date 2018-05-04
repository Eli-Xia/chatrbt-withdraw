package net.monkeystudio.chatrbtw.entity;

//表名e_ad_push_log
public class AdPushLog {
    private Integer id;
    private String wxPubAppId;
    private String wxFanOpenId;
    private Long pushAdTimestamp;
    private Integer adId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPushAdTimestamp() {
        return pushAdTimestamp;
    }

    public void setPushAdTimestamp(Long pushAdTimestamp) {
        this.pushAdTimestamp = pushAdTimestamp;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public String getWxPubAppId() {
        return wxPubAppId;
    }

    public void setWxPubAppId(String wxPubAppId) {
        this.wxPubAppId = wxPubAppId;
    }

    public String getWxFanOpenId() {
        return wxFanOpenId;
    }

    public void setWxFanOpenId(String wxFanOpenId) {
        this.wxFanOpenId = wxFanOpenId;
    }


}