package net.monkeystudio.portal.controller.req;

import java.util.HashMap;
import java.util.Map;

/**
 * 更新用户信息
 * @author hebo
 *
 */
public class UpdateUser {

	private String nickname;    //用户昵称
	private String contactEmail;//联系Email
	private String contactPhone;//联系电话
	
	public Map<String,Object> getMap(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("nickname", nickname);
		map.put("contactEmail", contactEmail);
		map.put("contactPhone", contactPhone);
		return map;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
}
