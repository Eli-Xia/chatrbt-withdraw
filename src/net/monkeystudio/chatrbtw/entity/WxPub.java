package net.monkeystudio.chatrbtw.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 接入公众号基本信息
 * @author hebo
 *
 */
public class WxPub implements Serializable{
	
	private Integer id;
    private String originId;
    private String nickname;
    private String appId;
    private Integer userId; //所属用户ID
    private Date createTime;
    private String chatbotName;
    private String headImgUrl;
    private Integer verifyTypeInfo;

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
        this.originId = originId == null ? null : originId.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getChatbotName() {
		return chatbotName;
	}

	public void setChatbotName(String chatbotName) {
		this.chatbotName = chatbotName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Integer getVerifyTypeInfo() {
        return verifyTypeInfo;
    }

    public void setVerifyTypeInfo(Integer verifyTypeInfo) {
        this.verifyTypeInfo = verifyTypeInfo;
    }

    @Override
    public String toString() {
        return "WxPub{" +
                "id=" + id +
                ", originId='" + originId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", appId='" + appId + '\'' +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", chatbotName='" + chatbotName + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", verifyTypeInfo=" + verifyTypeInfo +
                '}';
    }

}