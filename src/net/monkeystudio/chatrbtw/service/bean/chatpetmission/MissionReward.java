package net.monkeystudio.chatrbtw.service.bean.chatpetmission;

/**
 * 任务类型对应奖励
 * @author xiaxin
 */
public class MissionReward {
    private Float experience = 0F;
    private Float coin = 0F;

    public Float getExperience() {
        return experience;
    }

    public void setExperience(Float experience) {
        this.experience = experience;
    }

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }
}
