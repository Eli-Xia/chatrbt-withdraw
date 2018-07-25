package net.monkeystudio.chatrbtw.service.bean.chatpetflow;

import java.util.ArrayList;
import java.util.List;

/**
 * 经验值流水页
 * @author xiaxin
 */
public class ChatPetExpFlowVO {
    private List<ChatPetExpFlowResp> flows = new ArrayList<>();
    private Float experience = 0F;

    public List<ChatPetExpFlowResp> getFlows() {
        return flows;
    }

    public void setFlows(List<ChatPetExpFlowResp> flows) {
        this.flows = flows;
    }

    public Float getExperience() {
        return experience;
    }

    public void setExperience(Float experience) {
        this.experience = experience;
    }
}
