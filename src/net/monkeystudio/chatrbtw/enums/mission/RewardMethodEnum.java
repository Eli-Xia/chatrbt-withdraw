package net.monkeystudio.chatrbtw.enums.mission;

/**
 * 奖励方式枚举
 * @author xiaxin
 */
public enum RewardMethodEnum {
    NULL_REWARD(0,"没有奖励"),GOLD_REWARD(1,"金币奖励"),EXPERIENCE_REWARD(2,"经验奖励");

    private Integer type;
    private String name;

    RewardMethodEnum(Integer type,String name){
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
