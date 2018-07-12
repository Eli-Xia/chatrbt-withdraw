package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

/**
 * Created by bint on 2018/7/10.
 */
//表名e_dividend_detail_record
public class DividendDetailRecord {
    private Integer id;
    private Integer chatPetId;
    private Float money;
    private Date createTime;
    private Integer dividendId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDividendId() {
        return dividendId;
    }

    public void setDividendId(Integer dividendId) {
        this.dividendId = dividendId;
    }
}
