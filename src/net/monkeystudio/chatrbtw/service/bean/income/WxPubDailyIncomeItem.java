package net.monkeystudio.chatrbtw.service.bean.income;

import java.util.Date;

/**
 * Created by bint on 12/03/2018.
 */
public class WxPubDailyIncomeItem {

    private Float income;
    private Date date;


    public Float getIncome() {
        return income;
    }

    public void setIncome(Float income) {
        this.income = income;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
