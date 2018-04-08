package net.monkeystudio.chatrbtw.service.bean.income;

/**
 * Created by bint on 20/03/2018.
 */
public class WxPubAdIncomeOverview {

    private Integer adId;
    private String alias;
    private Double income;

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }
}
