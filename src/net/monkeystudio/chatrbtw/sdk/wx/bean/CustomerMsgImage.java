package net.monkeystudio.chatrbtw.sdk.wx.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 28/12/2017.
 */
public class CustomerMsgImage {

    @JsonProperty("touser")
    private String touser;

    @JsonProperty("msgtype")
    private String msgType;

    @JsonProperty("image")
    private CustomerMsgImageItem customerMsgImageItem;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public CustomerMsgImageItem getCustomerMsgImageItem() {
        return customerMsgImageItem;
    }

    public void setCustomerMsgImageItem(CustomerMsgImageItem customerMsgImageItem) {
        this.customerMsgImageItem = customerMsgImageItem;
    }
}
