package net.monkeystudio.chatrbtw.service.bean.ad;

/**
 * @author xiaxin
 */
public class AdHallItemDetailWxPub {
    private Integer wxPubId;
    private String nickname;
    private Integer isExclude;

    public Integer getWxPubId() {
        return wxPubId;
    }

    public void setWxPubId(Integer wxPubId) {
        this.wxPubId = wxPubId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getIsExclude() {
        return isExclude;
    }

    public void setIsExclude(Integer isExclude) {
        this.isExclude = isExclude;
    }
}
