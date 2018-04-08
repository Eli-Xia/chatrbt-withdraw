package net.monkeystudio.admin.controller.req;

/**
 * 修改密码请求
 * @author hebo
 *
 */
public class ChangePassword {

	private String curPassword;
	private String newPassword;
	
	public String getCurPassword() {
		return curPassword;
	}
	public void setCurPassword(String curPassword) {
		this.curPassword = curPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	
}
