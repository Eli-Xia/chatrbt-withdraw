package net.monkeystudio.chatrbtw.service.bean.chatpetcount;

/**
 * @author xiaxin
 */
public class YesterdayStatistic {
    private Float totalExperience;
    private Float totalCoin;
    private Integer loginNum;
    private Integer playGameNum;
    private Integer playGameTotalCount;
    private Integer addChatPetNum;
    private Integer receiveCoinNum;

    public Integer getAddChatPetNum() {
        return addChatPetNum;
    }

    public void setAddChatPetNum(Integer addChatPetNum) {
        this.addChatPetNum = addChatPetNum;
    }

    public Integer getReceiveCoinNum() {
        return receiveCoinNum;
    }

    public void setReceiveCoinNum(Integer receiveCoinNum) {
        this.receiveCoinNum = receiveCoinNum;
    }

    public Float getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(Float totalExperience) {
        this.totalExperience = totalExperience;
    }

    public Float getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(Float totalCoin) {
        this.totalCoin = totalCoin;
    }

    public Integer getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(Integer loginNum) {
        this.loginNum = loginNum;
    }

    public Integer getPlayGameNum() {
        return playGameNum;
    }

    public void setPlayGameNum(Integer playGameNum) {
        this.playGameNum = playGameNum;
    }

    public Integer getPlayGameTotalCount() {
        return playGameTotalCount;
    }

    public void setPlayGameTotalCount(Integer playGameTotalCount) {
        this.playGameTotalCount = playGameTotalCount;
    }
}
