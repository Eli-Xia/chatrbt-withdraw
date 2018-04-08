package net.monkeystudio.chatrbtw.service.bean.cos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/12/13.
 */
public class COSUploadDataResp {

    @JsonProperty("access_url")
    private String accessUrl;

    @JsonProperty("resource_path")
    private String resourcePath;

    @JsonProperty("source_url")
    private String sourceUrl;

    private String url;

    private String vid;

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }
}
