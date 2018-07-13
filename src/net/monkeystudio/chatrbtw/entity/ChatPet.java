package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

/**
 * Created by bint on 2018/4/16.
 */
public class ChatPet {

    private Integer id;
    private Integer tempAppearence;
    private String wxPubOriginId;
    private String wxFanOpenId;
    private Integer miniProgramId;
    private Integer ethnicGroupsId;
    private Integer secondEthnicGroupsId;
    private Date createTime;
    private Integer parentId;
    private Float experience;
    private Float coin;
    private String appearanceCode;
    private Integer chatPetType;
    private Float money;
    private Integer wxFanId;



    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTempAppearence() {
        return tempAppearence;
    }

    public void setTempAppearence(Integer tempAppearence) {
        this.tempAppearence = tempAppearence;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }

    public String getWxFanOpenId() {
        return wxFanOpenId;
    }

    public void setWxFanOpenId(String wxFanOpenId) {
        this.wxFanOpenId = wxFanOpenId;
    }

    public Integer getEthnicGroupsId() {
        return ethnicGroupsId;
    }

    public void setEthnicGroupsId(Integer ethnicGroupsId) {
        this.ethnicGroupsId = ethnicGroupsId;
    }

    public Integer getSecondEthnicGroupsId() {
        return secondEthnicGroupsId;
    }

    public void setSecondEthnicGroupsId(Integer secondEthnicGroupsId) {
        this.secondEthnicGroupsId = secondEthnicGroupsId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Float getExperience() {
        return experience;
    }

    public void setExperience(Float experience) {
        this.experience = experience;
    }

    public String getAppearanceCode() {
        return appearanceCode;
    }

    public void setAppearanceCode(String appearanceCode) {
        this.appearanceCode = appearanceCode;
    }

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Integer getWxFanId() {
        return wxFanId;
    }

    public void setWxFanId(Integer wxFanId) {
        this.wxFanId = wxFanId;
    }

    public Integer getMiniProgramId() {
        return miniProgramId;
    }

    public void setMiniProgramId(Integer miniProgramId) {
        this.miniProgramId = miniProgramId;
    }
}
