package net.monkeystudio.chatpet.controller.req.ethnicgroups;

/**
 * Created by bint on 2018/4/28.
 */
public class EthnicGroupsRankReq {
    private Integer chatPetId;
    private Integer pageSize;

    public Integer getChatPetId() {
        return chatPetId;
    }

    public void setChatPetId(Integer chatPetId) {
        this.chatPetId = chatPetId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
