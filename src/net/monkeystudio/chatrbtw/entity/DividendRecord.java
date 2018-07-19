package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

/**
 * Created by bint on 2018/7/10.
 */
public class DividendRecord {

    private Integer id;
    private Date createTime;
    private Float money;
    private Integer chatPetType;
    private Double totalCoin;
    private Integer totalWxfanCount;

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

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public Double getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(Double totalCoin) {
        this.totalCoin = totalCoin;
    }

    public Integer getTotalWxfanCount() {
        return totalWxfanCount;
    }

    public void setTotalWxfanCount(Integer totalWxfanCount) {
        this.totalWxfanCount = totalWxfanCount;
    }
}
