package net.monkeystudio.chatrbtw.service.bean.chatpetflow;

import java.util.ArrayList;
import java.util.List;

/**
 * 经验值流水页
 * @author xiaxin
 */
public class ChatPetExpFlowVO {
    private List<ChatPetExpFlowResp> flows = new ArrayList<>();
    private Integer level;

    public List<ChatPetExpFlowResp> getFlows() {
        return flows;
    }

    public void setFlows(List<ChatPetExpFlowResp> flows) {
        this.flows = flows;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
