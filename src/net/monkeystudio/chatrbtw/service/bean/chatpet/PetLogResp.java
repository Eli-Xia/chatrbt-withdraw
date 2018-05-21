package net.monkeystudio.chatrbtw.service.bean.chatpet;

import java.util.Date;

/**
 * @author xiaxin
 */
public class PetLogResp {
    private Date createTime;

    private Float coin;

    private String content;

    private Integer rewardType;

    public Integer getRewardType() {
        return rewardType;
    }

    public void setRewardType(Integer rewardType) {
        this.rewardType = rewardType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
