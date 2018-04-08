package net.monkeystudio.chatrbtw.entity;

import net.monkeystudio.wx.vo.material.BatchMaterialNews.NewsItem;

public class WxPubNewsWithBLOBs extends WxPubNews {
	
    private String digest;
    private String content;

    @Override
	public void initWithNews(String wxPubOriginId, Integer materialId, NewsItem news) {
		super.initWithNews(wxPubOriginId,materialId, news);
		digest = news.getDigest();
		content = news.getContent();
	}

	public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest == null ? null : digest.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}