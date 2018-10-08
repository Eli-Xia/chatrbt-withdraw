package net.monkeystudio.chatrbtw.service.bean.dividendrecord;

import net.monkeystudio.chatrbtw.entity.DividendRecord;

/**
 * Created by bint on 2018/7/10.
 */
public class DividendQueueValueVO {

    private Integer chatPetType;
    private Float totalMoney;
    private Integer chatPetId;
    private Float coin;
    private Double totalCoin;
    private Float money;
    private Integer dividendRecordId;
    private Integer dividendMsgId;

    public Integer getDividendMsgId() {
        return dividendMsgId;
    }

    public void setDividendMsgId(Integer dividendMsgId) {
        this.dividendMsgId = dividendMsgId;
    }

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public Float getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Float totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }

    public Double getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(Double totalCoin) {
        this.totalCoin = totalCoin;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Integer getDividendRecordId() {
        return dividendRecordId;
    }

    public void setDividendRecordId(Integer dividendRecordId) {
        this.dividendRecordId = dividendRecordId;
    }
}
