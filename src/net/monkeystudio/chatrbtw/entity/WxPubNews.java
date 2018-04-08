package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

import net.monkeystudio.wx.vo.material.BatchMaterialNews;

public class WxPubNews {
	
    private Integer id;
    private String wxPubOriginId;
    private Integer materialId;
    private String title;
    private String thumbMediaId;
    private Integer showCoverPic;
    private String author;
    private String url;
    private String url2;
    private String contentSourceUrl;
    private String thumbUrl;
    private Integer needOpenComment;
    private Integer onlyFansCanComment;
    private Date createTime;
    private Date updateTime;
    
    public void initWithNews(String wxPubOriginId, Integer materialId, BatchMaterialNews.NewsItem news){
    	
    	this.wxPubOriginId = wxPubOriginId;
    	this.materialId = materialId;
    	
    	title = news.getTitle();
    	thumbMediaId = news.getThumb_media_id();
    	showCoverPic = news.getShow_cover_pic();
    	author = news.getAuthor();
    	url = news.getUrl();
    	contentSourceUrl = news.getContent_source_url();
    	thumbUrl = news.getThumb_url();
    	needOpenComment = news.getNeed_open_comment();
    	onlyFansCanComment = news.getOnly_fans_can_comment();
    	createTime = new Date();
    	updateTime = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId == null ? null : thumbMediaId.trim();
    }

    public Integer getShowCoverPic() {
        return showCoverPic;
    }

    public void setShowCoverPic(Integer showCoverPic) {
        this.showCoverPic = showCoverPic;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getContentSourceUrl() {
        return contentSourceUrl;
    }

    public void setContentSourceUrl(String contentSourceUrl) {
        this.contentSourceUrl = contentSourceUrl == null ? null : contentSourceUrl.trim();
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl == null ? null : thumbUrl.trim();
    }

    public Integer getNeedOpenComment() {
        return needOpenComment;
    }

    public void setNeedOpenComment(Integer needOpenComment) {
        this.needOpenComment = needOpenComment;
    }

    public Integer getOnlyFansCanComment() {
        return onlyFansCanComment;
    }

    public void setOnlyFansCanComment(Integer onlyFansCanComment) {
        this.onlyFansCanComment = onlyFansCanComment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getWxPubOriginId() {
		return wxPubOriginId;
	}

	public void setWxPubOriginId(String wxPubOriginId) {
		this.wxPubOriginId = wxPubOriginId;
	}

	public String getUrl2() {
		return url2;
	}

	public void setUrl2(String url2) {
		this.url2 = url2;
	}
}