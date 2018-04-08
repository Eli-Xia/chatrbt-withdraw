package net.monkeystudio.chatrbtw.entity;

public class KrResponse {
	
    private Integer id;
    private String response;
    private String type;
    private String mediaId;
    private String wxPubOriginId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response == null ? null : response.trim();
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getWxPubOriginId() {
		return wxPubOriginId;
	}

	public void setWxPubOriginId(String wxPubOriginId) {
		this.wxPubOriginId = wxPubOriginId;
	}
    
    
}