package net.monkeystudio.chatpet.controller.req;

import java.math.BigDecimal;

public class WithdrawReq {
    private BigDecimal amount = BigDecimal.ZERO;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
