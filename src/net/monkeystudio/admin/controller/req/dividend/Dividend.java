package net.monkeystudio.admin.controller.req.dividend;

/**
 * Created by bint on 2018/7/10.
 */
public class Dividend {

    private Integer chatPetType;
    private Float totalMoney;

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public Float getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Float totalMoney) {
        this.totalMoney = totalMoney;
    }
}
