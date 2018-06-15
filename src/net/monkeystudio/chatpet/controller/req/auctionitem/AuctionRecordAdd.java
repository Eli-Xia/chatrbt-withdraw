package net.monkeystudio.chatpet.controller.req.auctionitem;

/**
 * Created by bint on 2018/6/15.
 */
public class AuctionRecordAdd {
    private Float price;
    private Integer auctionItemId;


    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getAuctionItemId() {
        return auctionItemId;
    }

    public void setAuctionItemId(Integer auctionItemId) {
        this.auctionItemId = auctionItemId;
    }
}
