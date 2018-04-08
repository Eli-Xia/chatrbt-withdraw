package net.monkeystudio.portal.controller.req.income;

import java.util.Date;

/**
 * Created by bint on 14/03/2018.
 */
public class UserDailyIncomeQueryReq {
    private Integer page;
    private Date startDate;
    private Date endDate;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
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
}
