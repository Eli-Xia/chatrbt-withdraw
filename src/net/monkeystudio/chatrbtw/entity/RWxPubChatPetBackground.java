package net.monkeystudio.chatrbtw.entity;

public class RWxPubChatPetBackground {
    private Integer id;

    private String wxPubOriginId;

    private Integer chatPetBackgroundId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId == null ? null : wxPubOriginId.trim();
    }

    public Integer getChatPetBackgroundId() {
        return chatPetBackgroundId;
    }

    public void setChatPetBackgroundId(Integer chatPetBackgroundId) {
        this.chatPetBackgroundId = chatPetBackgroundId;
    }
}