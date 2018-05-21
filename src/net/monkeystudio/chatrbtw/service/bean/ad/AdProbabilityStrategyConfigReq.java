package net.monkeystudio.chatrbtw.service.bean.ad;

/**
 * @author xiaxin
 */
public class AdProbabilityStrategyConfigReq {
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
}
