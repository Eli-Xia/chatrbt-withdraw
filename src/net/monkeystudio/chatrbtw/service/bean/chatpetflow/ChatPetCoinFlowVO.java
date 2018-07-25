package net.monkeystudio.chatrbtw.service.bean.chatpetflow;

import java.util.ArrayList;
import java.util.List;

/**
 * 猫饼流水页
 * @author xiaxin
 */
public class ChatPetCoinFlowVO {
    private List<ChatPetCoinFlowResp> flows = new ArrayList<>();
    private Float coin;

    public List<ChatPetCoinFlowResp> getFlows() {
        return flows;
    }

    public void setFlows(List<ChatPetCoinFlowResp> flows) {
        this.flows = flows;
    }

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }
}

