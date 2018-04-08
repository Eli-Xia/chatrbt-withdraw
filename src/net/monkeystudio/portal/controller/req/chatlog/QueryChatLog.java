package net.monkeystudio.portal.controller.req.chatlog;

/**
 * Created by bint on 26/12/2017.
 */
public class QueryChatLog {
    private Integer page;
    private Integer pageSize;
    private String wxPubOriginId;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }
}
