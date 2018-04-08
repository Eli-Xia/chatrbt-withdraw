package net.monkeystudio.wx.vo.pub;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by bint on 2017/11/4.
 */
public class EventEncrypt {

    @XStreamAlias("ToUserName")
    private String toUserName;

    @XStreamAlias("Encrypt")
    private String encrypt;

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }
}
