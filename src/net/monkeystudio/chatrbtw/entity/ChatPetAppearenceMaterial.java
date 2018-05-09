package net.monkeystudio.chatrbtw.entity;

//表名e_chat_pet_appearence_material
public class ChatPetAppearenceMaterial {
    private Integer id;
    private Integer site;
    private String key;
    private Integer chatPetType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }
}
