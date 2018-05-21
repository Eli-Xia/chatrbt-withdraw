package net.monkeystudio.chatrbtw.service.bean.ad;

/**
 * Created by bint on 21/12/2017.
 */
public class AdConfigResp {
    private Float pushAdRatio;
    private Integer pushAdId;
    private Integer pushAdSwitch;
    private Integer chatPushAdCount;

    private Float probabilityStrategyPushAdRatio;
    private Integer probabilityStrategyPushAdSwitch;

    public Float getProbabilityStrategyPushAdRatio() {
        return probabilityStrategyPushAdRatio;
    }

    public void setProbabilityStrategyPushAdRatio(Float probabilityStrategyPushAdRatio) {
        this.probabilityStrategyPushAdRatio = probabilityStrategyPushAdRatio;
    }

    public Integer getProbabilityStrategyPushAdSwitch() {
        return probabilityStrategyPushAdSwitch;
    }

    public void setProbabilityStrategyPushAdSwitch(Integer probabilityStrategyPushAdSwitch) {
        this.probabilityStrategyPushAdSwitch = probabilityStrategyPushAdSwitch;
    }

    public Float getPushAdRatio() {
        return pushAdRatio;
    }

    public void setPushAdRatio(Float pushAdRatio) {
        this.pushAdRatio = pushAdRatio;
    }

    public Integer getPushAdId() {
        return pushAdId;
    }

    public void setPushAdId(Integer pushAdId) {
        this.pushAdId = pushAdId;
    }

    public Integer getPushAdSwitch() {
        return pushAdSwitch;
    }

    public void setPushAdSwitch(Integer pushAdSwitch) {
        this.pushAdSwitch = pushAdSwitch;
    }

    public Integer getChatPushAdCount() {
        return chatPushAdCount;
    }

    public void setChatPushAdCount(Integer chatPushAdCount) {
        this.chatPushAdCount = chatPushAdCount;
    }
}
