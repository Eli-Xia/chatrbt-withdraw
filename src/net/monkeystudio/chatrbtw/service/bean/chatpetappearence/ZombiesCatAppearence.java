package net.monkeystudio.chatrbtw.service.bean.chatpetappearence;

import net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetAppearenceSite;
import net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetAppearenceSiteColor;

/**
 * Created by bint on 2018/5/9.
 */
public class ZombiesCatAppearence {

    //外轮廓
    @ChatPetAppearenceSite(1)
    private String outline;

    //内填充
    @ChatPetAppearenceSite(2)
    private String infill;

    //纹理
    @ChatPetAppearenceSite(3)
    private String texture;

    //纹理颜色
    @ChatPetAppearenceSiteColor(3)
    private String textureColor;

    //纹理阴影
    @ChatPetAppearenceSite(4)
    private String textureShadow;

    //眼睛
    @ChatPetAppearenceSite(5)
    private String eye;

    //嘴巴
    @ChatPetAppearenceSite(6)
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

    public String getTextureColor() {
        return textureColor;
    }

    public void setTextureColor(String textureColor) {
        this.textureColor = textureColor;
    }
}
