package net.monkeystudio.chatrbtw.service.bean.dividendrecord;

import net.monkeystudio.chatrbtw.entity.DividendRecord;

/**
 * Created by bint on 2018/7/10.
 */
public class DividendQueueValueVO {

    private Integer chatPetType;
    private Float totalMoney;
    private Integer chatPetId;
    private Float experience;
    private Double totalExperience;
    private Float money;
    private Integer dividendRecordId;

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

    public Float getExperience() {
        return experience;
    }

    public void setExperience(Float experience) {
        this.experience = experience;
    }

    public Double getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(Double totalExperience) {
        this.totalExperience = totalExperience;
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
