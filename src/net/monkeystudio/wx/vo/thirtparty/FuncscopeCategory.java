package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/10/27.
 */
public class FuncscopeCategory {

    @JsonProperty("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
