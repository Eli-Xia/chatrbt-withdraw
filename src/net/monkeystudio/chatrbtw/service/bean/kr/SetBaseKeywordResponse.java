package net.monkeystudio.chatrbtw.service.bean.kr;

import java.util.List;

/**
 * Created by bint on 31/01/2018.
 */
public class SetBaseKeywordResponse {

    private List<String> keywords;
    private String response;
    private Integer rule;

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getRule() {
        return rule;
    }

    public void setRule(Integer rule) {
        this.rule = rule;
    }
}
