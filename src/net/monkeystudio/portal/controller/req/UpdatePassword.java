package net.monkeystudio.portal.controller.req;

/**
 * 修改密码
 * @author hebo
 *
 */
public class UpdatePassword {

	private String curPassword;  //当前密码
	private String newPassword;  //新密码
	
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
