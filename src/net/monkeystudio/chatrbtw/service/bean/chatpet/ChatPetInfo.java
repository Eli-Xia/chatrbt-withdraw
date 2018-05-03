package net.monkeystudio.chatrbtw.service.bean.chatpet;

import net.monkeystudio.chatrbtw.service.bean.chatpetlevel.ExperienceProgressRate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 2018/4/19.
 */
public class ChatPetInfo {

    private Integer tempAppearance;
    private String appearanceUrl;
    private OwnerInfo ownerInfo;

    private String ownerId;
    private String geneticCode;

    private String invitationQrCode;
    private String wPubHeadImgUrl;

    private Integer experience;
    private Integer chatPetLevel;

    private ExperienceProgressRate experienceProgressRate;

    private List<PetLogResp> petLogs = new ArrayList<>();

    private Float fanTotalCoin = 0F;

    public String getwPubHeadImgUrl() {
        return wPubHeadImgUrl;
    }

    public void setwPubHeadImgUrl(String wPubHeadImgUrl) {
        this.wPubHeadImgUrl = wPubHeadImgUrl;
    }

    public String getInvitationQrCode() {

        return invitationQrCode;
    }

    public void setInvitationQrCode(String invitationQrCode) {
        this.invitationQrCode = invitationQrCode;
    }

    public Integer getTempAppearance() {
        return tempAppearance;
    }

    public void setTempAppearance(Integer tempAppearance) {
        this.tempAppearance = tempAppearance;
    }

    public OwnerInfo getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(OwnerInfo ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getGeneticCode() {
        return geneticCode;
    }

    public void setGeneticCode(String geneticCode) {
        this.geneticCode = geneticCode;
    }

    public Float getFanTotalCoin() {
        return fanTotalCoin;
    }

    public void setFanTotalCoin(Float fanTotalCoin) {
        this.fanTotalCoin = fanTotalCoin;
    }

    public List<PetLogResp> getPetLogs() {
        return petLogs;
    }

    public void setPetLogs(List<PetLogResp> petLogs) {
        this.petLogs = petLogs;
    }

    public String getAppearanceUrl() {
        return appearanceUrl;
    }

    public void setAppearanceUrl(String appearanceUrl) {
        this.appearanceUrl = appearanceUrl;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public ExperienceProgressRate getExperienceProgressRate() {
        return experienceProgressRate;
    }

    public void setExperienceProgressRate(ExperienceProgressRate experienceProgressRate) {
        this.experienceProgressRate = experienceProgressRate;
    }

    public Integer getChatPetLevel() {
        return chatPetLevel;
    }

    public void setChatPetLevel(Integer chatPetLevel) {
        this.chatPetLevel = chatPetLevel;
    }
}
