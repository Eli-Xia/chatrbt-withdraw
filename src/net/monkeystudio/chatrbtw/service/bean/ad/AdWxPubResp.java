package net.monkeystudio.chatrbtw.service.bean.ad;

/**
 * @author xiaxin
 */
public class AdWxPubResp {
    private Integer id;
    private String nickname;
    private String appId;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
