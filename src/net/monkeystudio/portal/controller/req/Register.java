package net.monkeystudio.portal.controller.req;

/**
 * 用户注册
 * @author hebo
 *
 */
public class Register {

	private String nickname;
	private String email;
	private String password;
	private String capText;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getCapText() {
		return capText;
	}

	public void setCapText(String capText) {
		this.capText = capText;
	}
}
