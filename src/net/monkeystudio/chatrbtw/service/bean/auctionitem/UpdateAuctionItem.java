package net.monkeystudio.chatrbtw.service.bean.auctionitem;

import java.util.Date;

/**
 * Created by bint on 2018/6/13.
 */
public class UpdateAuctionItem {

    private Integer id;
    private Date startTime;
    private Date endTime;
    private Integer chatPetType;
    private Integer auctionType;
    private String name;
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

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public Integer getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(Integer auctionType) {
        this.auctionType = auctionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuctionItemPic() {
        return auctionItemPic;
    }

    public void setAuctionItemPic(String auctionItemPic) {
        this.auctionItemPic = auctionItemPic;
    }
}
