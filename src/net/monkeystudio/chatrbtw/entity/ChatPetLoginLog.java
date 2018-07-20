package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

/**
 * @author xiaxin
 */
public class ChatPetLoginLog {
    private Integer id;
    private Integer wxFanId;
    private Date loginTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWxFanId() {
        return wxFanId;
    }

    public void setWxFanId(Integer wxFanId) {
        this.wxFanId = wxFanId;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
