package net.monkeystudio.chatrbtw.entity;

public class Res {
    //private String code;
    private Integer id;

    private String name;

    private String res;

    private String allowRoles;

    private String description;

    /*public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res == null ? null : res.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getAllowRoles() {
        return allowRoles;
    }

    public void setAllowRoles(String allowRoles) {
        this.allowRoles = allowRoles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}