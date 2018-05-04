package net.monkeystudio.chatrbtw.service.bean.user;

import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class UserResp extends User {
    private List<Role> roles = new ArrayList<>();

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
