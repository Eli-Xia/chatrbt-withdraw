package net.monkeystudio.admin.controller.req;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加用户请求
 * @author hebo
 *
 */
public class UpdateUser {

	private Integer userId;//用户ID
	private String username;//用户名
	private String nickname;//昵称
	private String password;//密码
	private String avatar;//头像
	private List<String> roles = new ArrayList<>();
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
