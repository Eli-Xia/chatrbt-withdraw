package net.monkeystudio.chatrbtw.service.bean.chatpetmyinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市分红页面vo
 * @author xiaxin
 */
public class ChatPetDividendDetailVO {
    private Float money = 0F;
    private Float historyTotalDividendAmount = 0F;
    private Float withdrawMoney = 0F;
    private List<ChatPetDividendDetail> details = new ArrayList<>();

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Float getHistoryTotalDividendAmount() {
        return historyTotalDividendAmount;
    }

    public void setHistoryTotalDividendAmount(Float historyTotalDividendAmount) {
        this.historyTotalDividendAmount = historyTotalDividendAmount;
    }

    public Float getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(Float withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public List<ChatPetDividendDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ChatPetDividendDetail> details) {
        this.details = details;
    }
}
