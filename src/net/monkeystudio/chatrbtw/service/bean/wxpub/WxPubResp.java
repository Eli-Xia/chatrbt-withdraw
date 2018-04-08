package net.monkeystudio.chatrbtw.service.bean.wxpub;

import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.entity.WxPubTag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaxin
 */
public class WxPubResp{
    private List<WxPubTag> tags = new ArrayList<>();

    private String userNickName;
    private Integer status;
    private Integer verifyTypeInfo;

    private Integer id;
    private String originId;
    private String nickname;
    private String appId;
    private Date createTime;

    public List<WxPubTag> getTags() {
        return tags;
    }

    public void setTags(List<WxPubTag> tags) {
        this.tags = tags;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getVerifyTypeInfo() {
        return verifyTypeInfo;
    }

    public void setVerifyTypeInfo(Integer verifyTypeInfo) {
        this.verifyTypeInfo = verifyTypeInfo;
    }
}
