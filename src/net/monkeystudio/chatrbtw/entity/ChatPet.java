package net.monkeystudio.chatrbtw.entity;

/**
 * Created by bint on 2018/4/16.
 */
public class ChatPet {
    private Integer id;
    private Integer tempAppearence;
    private String wxPubOrginId;
    private String wxFanOpenId;
    private Integer ethnicGroupsId;
    private Integer secondEthnicGroupsId;

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

    public String getWxPubOrginId() {
        return wxPubOrginId;
    }

    public void setWxPubOrginId(String wxPubOrginId) {
        this.wxPubOrginId = wxPubOrginId;
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
}
