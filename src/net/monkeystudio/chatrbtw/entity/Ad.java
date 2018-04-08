package net.monkeystudio.chatrbtw.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

//表名e_ad
public class Ad implements Serializable{

    private static final long serialVersionUID = -1767062236750738618L;

    private Integer id;
    private Integer adType;
    private String title;
    private String description;
    private String textContent;
    private String url;
    private String picUrl;
    private Integer pushType;
    private Date createAt;
    private String adRecommendStatement;
    private Float income;
    private String alias;
    private Date pushTime;
    private Date prePushTime;
    private Integer isOpen;
    private Date closeTime;
    private String coverPic;
    private Integer clickAmount;
    private String portalTitle;
    private String portalContent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdType() {
        return adType;
    }

    public void setAdType(Integer adType) {
        this.adType = adType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getPushType() {
        return pushType;
}

    public void setPushType(Integer pushType) {
        this.pushType = pushType;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getAdRecommendStatement() {
        return adRecommendStatement;
    }

    public void setAdRecommendStatement(String adRecommendStatement) {
        this.adRecommendStatement = adRecommendStatement;
    }

    public Float getIncome() {
        return income;
    }

    public void setIncome(Float income) {
        this.income = income;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public Date getPrePushTime() {
        return prePushTime;
    }

    public void setPrePushTime(Date prePushTime) {
        this.prePushTime = prePushTime;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public Integer getClickAmount() {
        return clickAmount;
    }

    public void setClickAmount(Integer clickAmount) {
        this.clickAmount = clickAmount;
    }

    public String getPortalTitle() {
        return portalTitle;
    }

    public void setPortalTitle(String portalTitle) {
        this.portalTitle = portalTitle;
    }

    public String getPortalContent() {
        return portalContent;
    }

    public void setPortalContent(String portalContent) {
        this.portalContent = portalContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return Objects.equals(id, ad.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}