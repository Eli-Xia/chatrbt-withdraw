package net.monkeystudio.portal.controller.req;

/**
 * 重置密码
 * @author hebo
 *
 */
public class ResetPassword {

	private String activeCode;
	private String newPassword;
	
	
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getActiveCode() {
		return activeCode;
	}
	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}
	
	
}
