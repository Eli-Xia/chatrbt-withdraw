package net.monkeystudio.chatrbtw.service.bean.cos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/12/13.
 */
public class COSUploadSuccessResp {

    private Integer code;
    private String message;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("data")
    private COSUploadDataResp cosUploadDataResp;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public COSUploadDataResp getCosUploadDataResp() {
        return cosUploadDataResp;
    }

    public void setCosUploadDataResp(COSUploadDataResp cosUploadDataResp) {
        this.cosUploadDataResp = cosUploadDataResp;
    }
}
