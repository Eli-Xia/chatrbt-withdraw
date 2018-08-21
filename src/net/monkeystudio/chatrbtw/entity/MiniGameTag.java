package net.monkeystudio.chatrbtw.entity;

import java.io.Serializable;

public class MiniGameTag implements Serializable {

    private static final long serialVersionUID = -6333745050289784907L;

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}