package net.monkeystudio.chatrbtw.entity;

import java.io.Serializable;

/**
 * Created by linhongbin on 2017/11/13.
 */
public class PushMessageConfig implements Serializable{

    private Integer id;
    private String key;
    private String value;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
