package net.monkeystudio.chatrbtw.service.bean.chatpet;

import java.util.List;

/**
 * Created by bint on 2018/5/8.
 */
public class ChatPetExperinceRank {
    private List<ChatPetExperinceRankItem> chatPetExperinceRankItemList;

    private Integer total;

    public List<ChatPetExperinceRankItem> getChatPetExperinceRankItemList() {
        return chatPetExperinceRankItemList;
    }

    public void setChatPetExperinceRankItemList(List<ChatPetExperinceRankItem> chatPetExperinceRankItemList) {
        this.chatPetExperinceRankItemList = chatPetExperinceRankItemList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
