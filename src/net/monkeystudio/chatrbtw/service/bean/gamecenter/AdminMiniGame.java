package net.monkeystudio.chatrbtw.service.bean.gamecenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminMiniGame {
    private Integer id;
    private String headImgUrl;
    private String nickname;
    private String qrCodeImgUrl;
    private Integer needSign;
    private Integer openType;//打开类型 长按跳转 直接跳转
    private Date onlineTime;//上线时间
    private Date createTime;
    private Integer shelveState;//上下架状态
    private Boolean isHandpicked;//是否为精选编辑 对象类型兼容现有数据null
    private String coverImgUrl;
    private String appId;
    private Float starNum = 2F;
    private List<Integer> tagIdList = new ArrayList<>();
    private String article;

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getQrCodeImgUrl() {
        return qrCodeImgUrl;
    }

    public void setQrCodeImgUrl(String qrCodeImgUrl) {
        this.qrCodeImgUrl = qrCodeImgUrl;
    }

    public Integer getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Integer needSign) {
        this.needSign = needSign;
    }

    public Integer getOpenType() {
        return openType;
    }

    public void setOpenType(Integer openType) {
        this.openType = openType;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getShelveState() {
        return shelveState;
    }

    public void setShelveState(Integer shelveState) {
        this.shelveState = shelveState;
    }

    public Boolean getIsHandpicked() {
        return isHandpicked;
    }

    public void setIsHandpicked(Boolean handpicked) {
        isHandpicked = handpicked;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Float getStarNum() {
        return starNum;
    }

    public void setStarNum(Float starNum) {
        this.starNum = starNum;
    }

    public List<Integer> getTagIdList() {
        return tagIdList;
    }

    public void setTagIdList(List<Integer> tagIdList) {
        this.tagIdList = tagIdList;
    }
}
