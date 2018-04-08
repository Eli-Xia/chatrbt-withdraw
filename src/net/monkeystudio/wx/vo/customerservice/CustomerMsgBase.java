package net.monkeystudio.wx.vo.customerservice;

/**
 * 客服接口消息基类
 * @author hebo
 *
 */
public class CustomerMsgBase {
	
	protected String touser;
    protected String msgtype;
    
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
    
    
}
