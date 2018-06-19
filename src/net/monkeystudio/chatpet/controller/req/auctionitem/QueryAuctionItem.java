package net.monkeystudio.chatpet.controller.req.auctionitem;

/**
 * Created by bint on 2018/6/13.
 */
public class QueryAuctionItem {

    private Integer pageSize;
    private Integer page;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
