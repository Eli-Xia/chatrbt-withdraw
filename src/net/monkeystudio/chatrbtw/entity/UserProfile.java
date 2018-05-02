package net.monkeystudio.chatrbtw.entity;

import java.util.Date;
import java.util.List;

/**
 * 用户基础信息
 * 不含密码等安全敏感字段，用于返回前端
 * @author hebo
 *
 */
public class UserProfile {

	private Integer id;			//用户ID
	private String username;	//用户名
	private String nickname;    //昵称
	private Date createTime;    //创建时间
	private String avatar;      //头像
	private List<String> roles;  //用户角色
	
	public UserProfile(User user, List<String> roles){
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
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
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
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	
}
