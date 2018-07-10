package net.monkeystudio.chatrbtw.sdk.wx.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2018/7/6.
 */
public class ErrorInfo {

    @JsonProperty("error")
    private String error ;

    @JsonProperty("errmsg")
    private String errMsg ;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
