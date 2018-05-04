package net.monkeystudio.chatrbtw.enums.chatpet;

/**
 * @author xiaxin
 */
public enum ChatPetTaskEnum {
    DAILY_READ_NEWS(1,"每日阅读文章任务",3f),DAILY_CHAT(2,"每日聊天互动签到任务",1f);

    private int code;
    private String name;
    private Float coinValue;

    ChatPetTaskEnum(int code,String name,Float coinValue){
        this.code = code;
        this.name = name;
        this.coinValue = coinValue;
    }

    public static ChatPetTaskEnum codeOf(int code){
        for(ChatPetTaskEnum taskEnum : values()){
            if(taskEnum.getCode() == code){
                return taskEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCoinValue() {
        return coinValue;
    }

    public void setCoinValue(Float coinValue) {
        this.coinValue = coinValue;
    }
}
