package net.monkeystudio.chatrbtw.entity;

/**
 * Created by bint on 2018/5/17.
 */
public class RWxPubChatPetType {

    private Integer id;
    private String wxPubOriginId;
    private Integer chatPetType;

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
        this.wxPubOriginId = wxPubOriginId;
    }

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }
}
