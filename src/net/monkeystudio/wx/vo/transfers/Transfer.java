package net.monkeystudio.wx.vo.transfers;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;
import java.math.BigDecimal;

public class Transfer implements Serializable {

    private static final long serialVersionUID = -7583053588512963691L;

    /** 商户账号appid*/
    @XStreamAlias("mch_appid")
    public String mchAppid;
    /** 微信支付商户号*/
    @XStreamAlias("mchid")
    public String mchid;
    /** 随机串*/
    @XStreamAlias("nonce_str")
    public String nonceStr;
    /** 签名*/
    @XStreamAlias("sign")
    public String sign;
    /** 商户订单号*/
    @XStreamAlias("partner_trade_no")
    public String partnerTradeNo;
    /** 用户id*/
    @XStreamAlias("openid")
    public String openid;
    /** 是否校验用户姓名 NO_CHECK：不校验真实姓名  FORCE_CHECK：强校验真实姓名*/
    @XStreamAlias("check_name")
    public String checkName;
    /** 金额 单位：分*/
    @XStreamAlias("amount")
    public Integer amount;
    /** 企业付款描述信息*/
    @XStreamAlias("desc")
    public String desc;
    /** ip地址*/
    @XStreamAlias("spbill_create_ip")
    public String spbillCreateIp;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMchAppid() {
        return mchAppid;
    }

    public void setMchAppid(String mchAppid) {
        this.mchAppid = mchAppid;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPartnerTradeNo() {
        return partnerTradeNo;
    }

    public void setPartnerTradeNo(String partnerTradeNo) {
        this.partnerTradeNo = partnerTradeNo;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }
}
