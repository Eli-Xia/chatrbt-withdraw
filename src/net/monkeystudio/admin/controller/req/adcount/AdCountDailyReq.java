package net.monkeystudio.admin.controller.req.adcount;

import java.util.Date;

/**
 * Created by bint on 21/03/2018.
 */
public class AdCountDailyReq {

    private Date startDate;
    private Date endDate;
    private Integer adId;

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
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
