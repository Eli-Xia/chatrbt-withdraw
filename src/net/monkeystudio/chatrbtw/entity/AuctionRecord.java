package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

//表名e_auction_record
public class AuctionRecord {

    private Integer id;
    private Integer wxFanId;
    private Float price;
    private Date bidTime;
    private Integer auctionItemId;

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

    public Integer getAuctionItemId() {
        return auctionItemId;
    }

    public void setAuctionItemId(Integer auctionItemId) {
        this.auctionItemId = auctionItemId;
    }
}