package net.monkeystudio.chatrbtw.service.bean.minigamead;

public class AddMiniGameAd {
    private String bgImgUrl;
    private String qrCodeImgUrl;
    private String appId;
    private String gameUrl;

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
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
}
