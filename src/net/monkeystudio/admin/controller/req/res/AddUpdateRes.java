package net.monkeystudio.admin.controller.req.res;

import java.util.List;

/**
 * @author xiaxin
 */
public class AddUpdateRes {
    private Integer id;

    private String name;

    private String res;

    //private String allowRoles;

    private List<String> allowRoles;

    private String description;



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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getAllowRoles() {
        return allowRoles;
    }

    public void setAllowRoles(List<String> allowRoles) {
        this.allowRoles = allowRoles;
    }
}
