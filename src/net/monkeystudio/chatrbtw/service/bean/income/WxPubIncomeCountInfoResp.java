package net.monkeystudio.chatrbtw.service.bean.income;

/**
 * Created by bint on 09/03/2018.
 */
public class WxPubIncomeCountInfoResp {
    private Float yesterdayIncome;
    private Double historyTotalIncome;

    public Float getYesterdayIncome() {
        return yesterdayIncome;
    }

    public void setYesterdayIncome(Float yesterdayIncome) {
        this.yesterdayIncome = yesterdayIncome;
    }

    public Double getHistoryTotalIncome() {
        return historyTotalIncome;
    }

    public void setHistoryTotalIncome(Double historyTotalIncome) {
        this.historyTotalIncome = historyTotalIncome;
    }
}
