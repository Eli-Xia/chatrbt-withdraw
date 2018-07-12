package net.monkeystudio.chatrbtw.enums.gamecenter;

/**
 * 猫市中心摊位枚举
 * @author xiaxin
 */
public enum ChatPetMarketStallEnum {

    INVITE_FIREND(1,"赠送一只猫六六",0,3),DAILY_LOGIN(2,"每日登录",1,6),AUTION_HALL(3,"拍卖大堂",null,null);

    private Integer code;
    private String name;
    private Integer state;//0:未完成 1:已完成  null:不需要状态表示
    private Integer missionCode;//任务code   null:不是任务摊位

    ChatPetMarketStallEnum(Integer code,String name,Integer state,Integer missionCode){
        this.code = code;
        this.name = name;
        this.state = state;
        this.missionCode = missionCode;
    }

    public static ChatPetMarketStallEnum codeOf(Integer code){
        for(ChatPetMarketStallEnum stateEnum : values()){
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(Integer missionCode) {
        this.missionCode = missionCode;
    }

    /*private Integer missionCode;//奖励对应的任务,为null则不是任务奖励
    private String  type;
*/

}
