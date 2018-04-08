package net.monkeystudio.chatrbtw.service.bean.income;

import net.monkeystudio.chatrbtw.entity.WxPub;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bint on 13/03/2018.
 */
public class WxPubDailyIncome {
    private Date date;
    private WxPub wxPub;
    private BigDecimal dailyIncome;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public WxPub getWxPub() {
        return wxPub;
    }

    public void setWxPub(WxPub wxPub) {
        this.wxPub = wxPub;
    }

    public BigDecimal getDailyIncome() {
        return dailyIncome;
    }

    public void setDailyIncome(BigDecimal dailyIncome) {
        this.dailyIncome = dailyIncome;
    }
}
