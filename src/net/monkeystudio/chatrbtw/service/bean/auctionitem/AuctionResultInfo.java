package net.monkeystudio.chatrbtw.service.bean.auctionitem;

import java.util.Date;

/**
 * Created by bint on 2018/6/20.
 */
public class AuctionResultInfo {
    private String ownerNickname;
    private String openId;
    private Float price;
    private Date bidTime;
    private Integer shipState;

    public String getOwnerNickname() {
        return ownerNickname;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Date getBidTime() {
        return bidTime;
    }

    public void setBidTime(Date bidTime) {
        this.bidTime = bidTime;
    }

    public Integer getShipState() {
        return shipState;
    }

    public void setShipState(Integer shipState) {
        this.shipState = shipState;
    }
}
