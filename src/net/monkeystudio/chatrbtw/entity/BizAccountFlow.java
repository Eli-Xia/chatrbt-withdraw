package net.monkeystudio.chatrbtw.entity;

import java.math.BigDecimal;
import java.util.Date;

public class BizAccountFlow {
    private Integer id;
    private Integer actionType;
    private Integer bizAccountId;
    private BigDecimal amount = BigDecimal.ZERO;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Integer getBizAccountId() {
        return bizAccountId;
    }

    public void setBizAccountId(Integer bizAccountId) {
        this.bizAccountId = bizAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
