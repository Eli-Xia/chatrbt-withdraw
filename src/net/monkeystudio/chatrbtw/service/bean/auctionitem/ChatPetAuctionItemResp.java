package net.monkeystudio.chatrbtw.service.bean.auctionitem;

import java.util.List;

/**
 * Created by bint on 2018/6/16.
 */
public class ChatPetAuctionItemResp {
    private Float coin;
    private List<ChatPetAuctionItemListResp> list;

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }

    public List<ChatPetAuctionItemListResp> getList() {
        return list;
    }

    public void setList(List<ChatPetAuctionItemListResp> list) {
        this.list = list;
    }
}
