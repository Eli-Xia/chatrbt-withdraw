package net.monkeystudio.admin.controller.req.minigame;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author xiaxin
 */
public class AddMiniGameReq {
    private MultipartFile headImg;
    private MultipartFile qrCodeImg;
    private String nickname;
    private Integer needSign;
    private Integer openType;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date onlineTime;

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Integer getOpenType() {
        return openType;
    }

    public void setOpenType(Integer openType) {
        this.openType = openType;
    }

    public MultipartFile getHeadImg() {
        return headImg;
    }

    public void setHeadImg(MultipartFile headImg) {
        this.headImg = headImg;
    }

    public MultipartFile getQrCodeImg() {
        return qrCodeImg;
    }

    public void setQrCodeImg(MultipartFile qrCodeImg) {
        this.qrCodeImg = qrCodeImg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Integer needSign) {
        this.needSign = needSign;
    }
}
