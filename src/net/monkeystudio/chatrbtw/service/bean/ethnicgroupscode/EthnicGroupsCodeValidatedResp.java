package net.monkeystudio.chatrbtw.service.bean.ethnicgroupscode;

/**
 * Created by bint on 2018/4/10.
 */
public class EthnicGroupsCodeValidatedResp {

    private Integer status;
    private String content;

    public EthnicGroupsCodeValidatedResp(Integer status, String content) {
        this.status = status;
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
