package net.monkeystudio.chatrbtw.entity;

import java.util.Date;

/**
 * Created by bint on 2018/4/21.
 */
//表名e_crypto_kitties
public class CryptoKitties {
    private Integer id;
    private String url;
    private Date createTime;
    private String wxFanOpenId;
    private String wxPubOriginId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getWxFanOpenId() {
        return wxFanOpenId;
    }

    public void setWxFanOpenId(String wxFanOpenId) {
        this.wxFanOpenId = wxFanOpenId;
    }

    public String getWxPubOriginId() {
        return wxPubOriginId;
    }

    public void setWxPubOriginId(String wxPubOriginId) {
        this.wxPubOriginId = wxPubOriginId;
    }
}
