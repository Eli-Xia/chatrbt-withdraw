package net.monkeystudio.chatrbtw.service.bean.auctionitem;

import java.util.Date;

/**
 * Created by bint on 2018/6/14.
 */
public class ChatPetAuctionItemListResp {

    private Integer id;
    private Date startTime;
    private Date endTime;
    private String auctionItemName;
    private Integer state;
    private Integer auctionType;
    private String auctionItemPic;
    private Integer number;
    private String wxFanNickname;
    private Date bidTime;
    private Float bidPrice;
    private Date dealTime;
    private Float dealPrice;
    private Boolean isWinner;

    public Float getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(Float dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAuctionItemName() {
        return auctionItemName;
    }

    public void setAuctionItemName(String auctionItemName) {
        this.auctionItemName = auctionItemName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(Integer auctionType) {
        this.auctionType = auctionType;
    }

    public String getAuctionItemPic() {
        return auctionItemPic;
    }

    public void setAuctionItemPic(String auctionItemPic) {
        this.auctionItemPic = auctionItemPic;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getWxFanNickname() {
        return wxFanNickname;
    }

    public void setWxFanNickname(String wxFanNickname) {
        this.wxFanNickname = wxFanNickname;
    }

    public Date getDealTime() {
        return dealTime;
    }

    public void setDealTime(Date dealTime) {
        this.dealTime = dealTime;
    }

    public Date getBidTime() {
        return bidTime;
    }

    public void setBidTime(Date bidTime) {
        this.bidTime = bidTime;
    }

    public Float getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Float bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Boolean getWinner() {
        return isWinner;
    }

    public void setWinner(Boolean winner) {
        isWinner = winner;
    }
}
