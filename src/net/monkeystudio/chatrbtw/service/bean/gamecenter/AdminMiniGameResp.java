package net.monkeystudio.chatrbtw.service.bean.gamecenter;

import java.util.Date;

/**
 * @author xiaxin
 */
public class AdminMiniGameResp {
    private Integer id;
    private Date createTime;
    private Date onlineTime;
    private Integer openType;
    private Integer needSign;
    private Integer shelveState;
    private Long playerNum;
    private String nickname;
    private Boolean isHandpicked;
    private Integer redirectType;

    public Integer getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(Integer redirectType) {
        this.redirectType = redirectType;
    }

    public Boolean getIsHandpicked() {
        return isHandpicked;
    }

    public void setIsHandpicked(Boolean handpicked) {
        isHandpicked = handpicked;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Integer getOpenType() {
        return openType;
    }

    public void setOpenType(Integer openType) {
        this.openType = openType;
    }

    public Integer getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Integer needSign) {
        this.needSign = needSign;
    }

    public Integer getShelveState() {
        return shelveState;
    }

    public void setShelveState(Integer shelveState) {
        this.shelveState = shelveState;
    }

    public Long getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(Long playerNum) {
        this.playerNum = playerNum;
    }
}
