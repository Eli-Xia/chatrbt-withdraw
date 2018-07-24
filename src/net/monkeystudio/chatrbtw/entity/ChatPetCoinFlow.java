package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

/**
 猫饼流水
 * @author xiaxin
 */
public class ChatPetCoinFlow {
    private Integer id;
    private Date createTime;
    private Integer chatPetId;
    private String note;
    private Integer coinActionType;//猫饼变化类型 什么原因导致猫饼数量变化
    private Float amount ;//此次流水猫饼改变数额

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getCoinActionType() {
        return coinActionType;
    }

    public void setCoinActionType(Integer coinActionType) {
        this.coinActionType = coinActionType;
    }

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

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
