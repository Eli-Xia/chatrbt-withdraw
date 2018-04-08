package net.monkeystudio.admin.controller.resp;

import java.util.Date;
import java.util.List;

import net.monkeystudio.entity.User;

/**
 * 用户详情
 * @author hebo
 *
 */
public class UserDetail {

	private Integer id;

    private String username;
    
    private String nickname;
    
    private Date createTime;
    
    private String avatar;
    
    private List<String> roles;
    
    public UserDetail(User user, List<String> roles){
    	this.id = user.getId();
    	this.username = user.getUsername();
    	this.nickname = user.getNickname();
    	this.createTime = user.getCreateTime();
    	this.avatar = user.getAvatar();
    	this.roles = roles;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
    
    
}
