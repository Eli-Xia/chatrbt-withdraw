package net.monkeystudio.admin.controller.req.income;

import java.util.Date;

/**
 * Created by bint on 12/03/2018.
 */
public class WxPubDailyIncomeQueryReq {

    private String wxPubOriginId ;
    private Date startDate ;
    private Date endDate ;
    private Integer page;

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
