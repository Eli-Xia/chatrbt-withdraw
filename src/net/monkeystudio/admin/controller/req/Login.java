package net.monkeystudio.admin.controller.req;

/**
 * 用户登录请求
 * @author hebo
 *
 */
public class Login {

	private String username;//用户名
	private String password;//密码
	
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
	
	
}
