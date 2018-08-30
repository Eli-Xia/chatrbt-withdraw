package net.monkeystudio.chatrbtw.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现类
 */
public class Withdraw {
    private Integer id;
    private Integer wxFanId;
    private Integer accountFlowId;
    private String mchTradeNo;
    private String wxPaymentNo;
    private String errorCode;
    private Date createTime;
    private Date paymentTime;
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWxFanId() {
        return wxFanId;
    }

    public void setWxFanId(Integer wxFanId) {
        this.wxFanId = wxFanId;
    }

    public Integer getAccountFlowId() {
        return accountFlowId;
    }

    public void setAccountFlowId(Integer accountFlowId) {
        this.accountFlowId = accountFlowId;
    }

    public String getMchTradeNo() {
        return mchTradeNo;
    }

    public void setMchTradeNo(String mchTradeNo) {
        this.mchTradeNo = mchTradeNo;
    }

    public String getWxPaymentNo() {
        return wxPaymentNo;
    }

    public void setWxPaymentNo(String wxPaymentNo) {
        this.wxPaymentNo = wxPaymentNo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }
}
