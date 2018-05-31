package net.monkeystudio.chatrbtw.service.bean.chatpet;

/**
 * 魔币统计(根据陪聊宠类型)
 * @author xiaxin
 */
public class MagicCoinCount {
    private Float historyTotalAmount;//魔币总产值
    private Float yesterdayTotalAmount;//昨日总产值

    public Float getHistoryTotalAmount() {
        return historyTotalAmount;
    }

    public void setHistoryTotalAmount(Float historyTotalAmount) {
        this.historyTotalAmount = historyTotalAmount;
    }

    public Float getYesterdayTotalAmount() {
        return yesterdayTotalAmount;
    }

    public void setYesterdayTotalAmount(Float yesterdayTotalAmount) {
        this.yesterdayTotalAmount = yesterdayTotalAmount;
    }
}
