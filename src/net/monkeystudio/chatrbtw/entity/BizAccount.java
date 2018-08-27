package net.monkeystudio.chatrbtw.entity;

import java.math.BigDecimal;

public class BizAccount {
    private Integer id;
    private BigDecimal amount = BigDecimal.ZERO;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
