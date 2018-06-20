package net.monkeystudio.chatrbtw.service.bean.auctionitem;

import java.util.Date;

/**
 * Created by bint on 2018/6/20.
 */
public class AddAuctionItem {
    private Date startTime;
    private Date endTime;
    private String name;
    private Integer auctionType;
    private Integer chatPetType;
    private String auctionItemPic;


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

    public Integer getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(Integer auctionType) {
        this.auctionType = auctionType;
    }

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public String getAuctionItemPic() {
        return auctionItemPic;
    }

    public void setAuctionItemPic(String auctionItemPic) {
        this.auctionItemPic = auctionItemPic;
    }
}
