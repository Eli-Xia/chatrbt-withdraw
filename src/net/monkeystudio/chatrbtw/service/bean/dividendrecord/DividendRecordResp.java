package net.monkeystudio.chatrbtw.service.bean.dividendrecord;

import java.util.Date;

/**
 * Created by bint on 2018/7/19.
 */
public class DividendRecordResp {
    private Integer id;
    private Date createTime ;
    private Integer userCount;
    private Float totalMoney;
    private Double totalCoin;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Float getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Float totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Double getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(Double totalCoin) {
        this.totalCoin = totalCoin;
    }
}
