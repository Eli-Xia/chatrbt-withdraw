package net.monkeystudio.chatrbtw.service.bean.chatpet;

/**
 * @author xiaxin
 */
public class ChatPetSessionVo {
    private Integer wxPubId;
    private Integer wxFanId;
    private boolean isRedirectPoster ;

    public boolean isRedirectPoster() {
        return isRedirectPoster;
    }

    public void setRedirectPoster(boolean redirectPoster) {
        isRedirectPoster = redirectPoster;
    }

    public Integer getWxPubId() {
        return wxPubId;
    }

    public void setWxPubId(Integer wxPubId) {
        this.wxPubId = wxPubId;
    }

    public Integer getWxFanId() {
        return wxFanId;
    }

    public void setWxFanId(Integer wxFanId) {
        this.wxFanId = wxFanId;
    }
}
