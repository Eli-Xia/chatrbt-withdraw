package net.monkeystudio.admin.controller.resp;

import java.util.List;

/**
 * @author xiaxin
 */
public class ResForResp {
    private Integer id;

    private String name;

    private String res;

    private List<String> allowRoles;

    private String description;

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
        this.name = name;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public List<String> getAllowRoles() {
        return allowRoles;
    }

    public void setAllowRoles(List<String> allowRoles) {
        this.allowRoles = allowRoles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
