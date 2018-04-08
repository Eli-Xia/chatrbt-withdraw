package net.monkeystudio.admin.controller.req;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加用户请求
 * @author hebo
 *
 */
public class AddUser {

	private String username;//用户名
	private String nickname;//昵称
	private String password;//密码
	private List<String> roles = new ArrayList<>();
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
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
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
