package net.monkeystudio.chatrbtw.sdk.wx.bean.qrcode;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2018/4/13.
 */
public class Scene {

    @JsonProperty("scene_str")
    private String sceneStr;

    public String getSceneStr() {
        return sceneStr;
    }

    public void setSceneStr(String sceneStr) {
        this.sceneStr = sceneStr;
    }
}
