package net.monkeystudio.admin.controller.req.ad;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * Created by bint on 19/01/2018.
 */
public class AddAd {
    private Integer adType;
    private String title;
    private String description;
    private String textContent;
    private String url;
    private Integer pushType;
    private Long createAt;
    private String adRecommendStatement;
    private Float income;
    private String alias;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date pushTime;
    private MultipartFile wxPic;
    private MultipartFile coverPic;
    private Integer clickAmount;
    private String portalTitle;
    private String portalContent;
    private Integer pushStrategyType;

    public Integer getPushStrategyType() {
        return pushStrategyType;
    }

    public void setPushStrategyType(Integer pushStrategyType) {
        this.pushStrategyType = pushStrategyType;
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

    public MultipartFile getWxPic() {
        return wxPic;
    }

    public void setWxPic(MultipartFile wxPic) {
        this.wxPic = wxPic;
    }

    public Integer getPushType() {
        return pushType;
    }

    public void setPushType(Integer pushType) {
        this.pushType = pushType;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
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

    public MultipartFile getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(MultipartFile coverPic) {
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
}
