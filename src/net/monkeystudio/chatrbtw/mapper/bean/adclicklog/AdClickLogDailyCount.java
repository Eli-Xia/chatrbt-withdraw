package net.monkeystudio.chatrbtw.mapper.bean.adclicklog;

import java.util.Date;

/**
 * Created by bint on 21/03/2018.
 */
public class AdClickLogDailyCount {
    private Date clickDate;
    private Integer clickCount;

    public Date getClickDate() {
        return clickDate;
    }

    public void setClickDate(Date clickDate) {
        this.clickDate = clickDate;
    }

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }
}
