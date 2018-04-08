package net.monkeystudio.wx.mp.beam;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by linhongbin on 2017/10/30.
 */
public class Encryp {

    @XStreamAlias("AppId")
    private String appId ;

    @XStreamAlias("Encrypt")
    private String encrypt;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    @Override
    public String toString() {
        return "Encryp{" +
                "appId='" + appId + '\'' +
                ", encrypt='" + encrypt + '\'' +
                '}';
    }
}
