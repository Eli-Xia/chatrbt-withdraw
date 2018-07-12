package net.monkeystudio.chatrbtw.service.bean.gamecenter;

import net.monkeystudio.chatrbtw.entity.WxMiniGame;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class ChatPetGameCenterResp {
    private List<ChatPetCenterStallResp> chatPetCenterStallList = new ArrayList<>();
    private List<WxMiniGame> miniGameList = new ArrayList<>();

    public List<ChatPetCenterStallResp> getChatPetCenterStallList() {
        return chatPetCenterStallList;
    }

    public void setChatPetCenterStallList(List<ChatPetCenterStallResp> chatPetCenterStallList) {
        this.chatPetCenterStallList = chatPetCenterStallList;
    }

    public List<WxMiniGame> getMiniGameList() {
        return miniGameList;
    }

    public void setMiniGameList(List<WxMiniGame> miniGameList) {
        this.miniGameList = miniGameList;
    }
}
