package net.monkeystudio.chatrbtw.service.bean.gamecenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class ChatPetGameCenterResp {
    private List<ChatPetCenterStallResp> chatPetCenterStallList = new ArrayList<>();
    private List<ChatPetMiniGameResp> miniGameList = new ArrayList<>();

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
