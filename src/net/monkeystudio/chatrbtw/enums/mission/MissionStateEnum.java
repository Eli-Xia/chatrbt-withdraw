package net.monkeystudio.chatrbtw.enums.mission;

/**
 * @author xiaxin
 */
public enum MissionStateEnum {
    NOT_FINISH(0,"未完成"),GOING_ON(1,"进行中"),FINISH_NOT_AWARD(2,"已完成未领奖"),FINISH_AND_AWARD(3,"已完成已领奖");

    private Integer code;
    private String name;

    MissionStateEnum(Integer code,String name){
        this.code = code;
        this.name = name;
    }

    public static MissionStateEnum codeOf(Integer code){
        for(MissionStateEnum stateEnum : values()){
            if(stateEnum.getCode() == code){
                return stateEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
