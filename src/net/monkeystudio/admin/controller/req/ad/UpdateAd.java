package net.monkeystudio.admin.controller.req.ad;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * Created by bint on 19/12/2017.
 */
public class UpdateAd {
    private Integer id;
    private Integer adType;
    private String title;
    private String description;
    private String textContent;
    private String url;
    private Integer pushType;
    private Date createAt;
    private String adRecommendStatement;
    private String alias;
    private Integer isOpen;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date pushTime;
    private MultipartFile wxPic;
    private MultipartFile coverPic;
    private Integer clickAmount;
    private String portalTitle;
    private String portalContent;

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public MultipartFile getWxPic() {
        return wxPic;
    }

    public void setWxPic(MultipartFile wxPic) {
        this.wxPic = wxPic;
    }

    public MultipartFile getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(MultipartFile coverPic) {
        this.coverPic = coverPic;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
