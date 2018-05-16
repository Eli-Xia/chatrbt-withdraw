package net.monkeystudio.chatrbtw.service.bean.chatpetappearence;

import net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetAppearanceCodeSite;

/**
 * Created by bint on 2018/5/9.
 */
public class ZombiesCatAppearance {

    //外轮廓
    @ChatPetAppearanceCodeSite(1)
    private String outline;

    //内填充
    @ChatPetAppearanceCodeSite(3)
    private String infill;

    //纹理
    @ChatPetAppearanceCodeSite({5,6})
    private String texture;

    //纹理颜色
    //@ChatPetAppearanceCodeSite(6)
    //private String textureColor;

    //纹理阴影
    @ChatPetAppearanceCodeSite(7)
    private String textureShadow;

    //眼睛
    @ChatPetAppearanceCodeSite(9)
    private String eye;

    //嘴巴
    @ChatPetAppearanceCodeSite(11)
    private String mouth;

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public String getInfill() {
        return infill;
    }

    public void setInfill(String infill) {
        this.infill = infill;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getTextureShadow() {
        return textureShadow;
    }

    public void setTextureShadow(String textureShadow) {
        this.textureShadow = textureShadow;
    }

    public String getEye() {
        return eye;
    }

    public void setEye(String eye) {
        this.eye = eye;
    }

    public String getMouth() {
        return mouth;
    }

    public void setMouth(String mouth) {
        this.mouth = mouth;
    }

}
