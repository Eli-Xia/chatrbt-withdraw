package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

/**
 * 宠物经验值流水
 * @author xiaxin
 */
public class ChatPetExpFlow {
    private Integer id;
    private Integer chatPetId;
    private Date createTime;
    private String note;//流水说明
    private Integer expActionType;//经验值变化类型 什么原因导致经验值变化
    private Float amount ;//此次流水经验值改变数额

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }


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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getExpActionType() {
        return expActionType;
    }

    public void setExpActionType(Integer expActionType) {
        this.expActionType = expActionType;
    }
}

