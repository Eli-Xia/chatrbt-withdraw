package net.monkeystudio.chatpet.controller.req.auctionitem;

import java.util.Date;

/**
 * Created by bint on 2018/6/13.
 */
public class AuctionItemResp {

    private Integer id;
    private Date startTime;
    private Date endTime;
    private String name;
    private Integer state;
    private Integer auctionType;
    private String auctionItemPic;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
