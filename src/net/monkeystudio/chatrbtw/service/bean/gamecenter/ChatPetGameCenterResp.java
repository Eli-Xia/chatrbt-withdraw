package net.monkeystudio.chatrbtw.service.bean.gamecenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class ChatPetGameCenterResp {
    private List<ChatPetCenterStallResp> chatPetCenterStallList = new ArrayList<>();
    private List<ChatPetMiniGameResp> miniGameList = new ArrayList<>();
    private Integer owerWxFanId;//拥有宠物的粉丝的id

    public Integer getOwerWxFanId() {
        return owerWxFanId;
    }

    public void setOwerWxFanId(Integer owerWxFanId) {
        this.owerWxFanId = owerWxFanId;
    }

    public List<ChatPetCenterStallResp> getChatPetCenterStallList() {
        return chatPetCenterStallList;
    }

    public void setChatPetCenterStallList(List<ChatPetCenterStallResp> chatPetCenterStallList) {
        this.chatPetCenterStallList = chatPetCenterStallList;
    }

    public List<ChatPetMiniGameResp> getMiniGameList() {
        return miniGameList;
    }

    public void setMiniGameList(List<ChatPetMiniGameResp> miniGameList) {
        this.miniGameList = miniGameList;
    }
}
