package net.monkeystudio.wx.vo.thirtparty;

import java.io.Serializable;

/**
 * @author xiaxin
 */
public class JsApiTicketResp implements Serializable{
    private String ticket;
    private Integer expiresIn;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
