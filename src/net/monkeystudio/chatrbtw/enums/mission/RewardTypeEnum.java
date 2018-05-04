package net.monkeystudio.chatrbtw.enums.mission;

/**
 * @author xiaxin
 */
public enum RewardTypeEnum {
    NULL_REWARD(0,"没有奖励"),GOLD_REWARD(1,"金币奖励"),EXPERIENCE_REWARD(2,"经验奖励");

    private int type;
    private String name;

    RewardTypeEnum(int type,String name){
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
