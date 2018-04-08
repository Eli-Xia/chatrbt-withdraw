package net.monkeystudio.admin.controller.req;

/**
 * 设置公众号主
 * @author hebo
 *
 */
public class SetWxPubAdm {

	private Integer wxPubId; //公众号ID（系统内部ID）
	private Integer admUserId; //公众号主用户ID
	
	public Integer getWxPubId() {
		return wxPubId;
	}
	public void setWxPubId(Integer wxPubId) {
		this.wxPubId = wxPubId;
	}
	public Integer getAdmUserId() {
		return admUserId;
	}
	public void setAdmUserId(Integer admUserId) {
		this.admUserId = admUserId;
	}
	
	
}