package net.monkeystudio.chatrbtw.entity;

//表名r_chat_pet_appearence_site_color
public class RChatPetAppearenceSiteColor {
    private Integer id;
    private Integer chatPetType;
    private Integer site;
    private String colorKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatPetType() {
        return chatPetType;
    }

    public void setChatPetType(Integer chatPetType) {
        this.chatPetType = chatPetType;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public String getColorKey() {
        return colorKey;
    }

    public void setColorKey(String colorKey) {
        this.colorKey = colorKey;
    }
}