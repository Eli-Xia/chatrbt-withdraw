package net.monkeystudio.chatrbtw.service.bean.gamecenter;

/**
 * 猫市摊位信息
 * @author xiaxin
 */
public class ChatPetCenterStallResp {
    private Integer state;// 0:未完成 1:完成 null:不需要状态
    private String title;//标题
    private String description;//描述

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
