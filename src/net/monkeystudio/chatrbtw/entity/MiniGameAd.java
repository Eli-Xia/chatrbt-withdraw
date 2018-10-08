package net.monkeystudio.chatrbtw.entity;

public class MiniGameAd {
    private Integer id;
    private String bgImgUrl;
    private String qrCodeImgUrl;
    private String appId;
    private Integer state;
    private String gameUrl;

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBgImgUrl() {
        return bgImgUrl;
    }

    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }

    public String getQrCodeImgUrl() {
        return qrCodeImgUrl;
    }

    public void setQrCodeImgUrl(String qrCodeImgUrl) {
        this.qrCodeImgUrl = qrCodeImgUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
