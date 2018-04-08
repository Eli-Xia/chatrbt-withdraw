package net.monkeystudio.chatrbtw.service.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/12/7.
 */
public class TuLingRespItem {
    private String name;
    private String icon;
    private String info;
    @JsonProperty("detailurl")
    private String detailUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}
