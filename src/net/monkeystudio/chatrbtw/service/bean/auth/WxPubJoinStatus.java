package net.monkeystudio.chatrbtw.service.bean.auth;

/**
 * Created by bint on 28/03/2018.
 */
public class WxPubJoinStatus {

    private String wxPubOriginId;

    //接入的状态
    private Integer joinStatus;

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }

    public Integer getJoinStatus() {
        return joinStatus;
    }

    public void setJoinStatus(Integer joinStatus) {
        this.joinStatus = joinStatus;
    }
}
