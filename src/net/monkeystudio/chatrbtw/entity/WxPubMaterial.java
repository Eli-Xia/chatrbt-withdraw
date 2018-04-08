package net.monkeystudio.chatrbtw.entity;

public class WxPubMaterial {
    private Integer id;

    private String wxPubOriginId;

    private String mediaId;

    private Long materialUpdateTime;

    private Long contentUpdateTime;

    private Long contentCreateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId == null ? null : wxPubOriginId.trim();
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId == null ? null : mediaId.trim();
    }

    public Long getContentUpdateTime() {
        return contentUpdateTime;
    }

    public void setContentUpdateTime(Long contentUpdateTime) {
        this.contentUpdateTime = contentUpdateTime;
    }

    public Long getContentCreateTime() {
        return contentCreateTime;
    }

    public void setContentCreateTime(Long contentCreateTime) {
        this.contentCreateTime = contentCreateTime;
    }

	public Long getMaterialUpdateTime() {
		return materialUpdateTime;
	}

	public void setMaterialUpdateTime(Long materialUpdateTime) {
		this.materialUpdateTime = materialUpdateTime;
	}
}