package net.monkeystudio.base.service.bean;

import java.util.List;

import net.monkeystudio.base.Constants;
import net.monkeystudio.chatrbtw.entity.Role;
import net.monkeystudio.chatrbtw.entity.User;
import org.springframework.beans.BeanUtils;

public class UserDetail extends User {

	private List<Role> roles;
	private String statusName;

	public UserDetail(User user, List<Role> roles){
		BeanUtils.copyProperties(user, this);
		this.roles = roles;
		this.statusName = Constants.USER_STATUS_NAME_MAP.get(user.getStatus());
	}
	
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
}
