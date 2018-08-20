package net.monkeystudio.chatrbtw.entity;

public class RMiniGameTag {
    private Integer id;

    private Integer miniGameId;

    private Integer tagId;

    private Boolean handpicked;

    public Boolean getHandpicked() {
        return handpicked;
    }

    public void setHandpicked(Boolean handpicked) {
        this.handpicked = handpicked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMiniGameId() {
        return miniGameId;
    }

    public void setMiniGameId(Integer miniGameId) {
        this.miniGameId = miniGameId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}