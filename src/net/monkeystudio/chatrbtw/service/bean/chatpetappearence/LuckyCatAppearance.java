package net.monkeystudio.chatrbtw.service.bean.chatpetappearence;

import net.monkeystudio.chatrbtw.annotation.chatpet.ChatPetAppearanceCodeSite;

/**
 * Created by bint on 2018/6/27.
 */
public class LuckyCatAppearance {

    //内填充
    @ChatPetAppearanceCodeSite(1)
    private String infill;

    //纹理
    @ChatPetAppearanceCodeSite({2,3})
    private String texture;

    //眼睛
    @ChatPetAppearanceCodeSite(4)
    private String eye;

    //嘴巴
    @ChatPetAppearanceCodeSite(5)
    private String mouth;

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
