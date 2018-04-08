package net.monkeystudio.wx.vo.thirtparty;

import java.io.Serializable;

/**
 * Created by linhongbin on 2017/10/29.
 */
public class VerifyTicketInfo implements Serializable{

    private String signature;
    private String timestamp;
    private String nonce;
    private String ticket;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
