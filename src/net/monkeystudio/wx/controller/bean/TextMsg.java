package net.monkeystudio.wx.controller.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @deprecated
 * 接收-文本消息
 * @author hebo
 *
 */
public class TextMsg {

	private String ToUserName;
	private String FromUserName;
	private Long CreateTime;
	private String MsgType;
	private String Content;
	
	protected Element root;
	
	public TextMsg(){
		root = DocumentHelper.createElement("xml"); 
	}
	
	/**
	 * 添加当前类需要生成XML的属性到root节点中。
	 * 子类需要重写该方法，并在其中调用父类方法。
	 */
	protected void addField2XmlRoot(){
		root.addElement("ToUserName").addText(ToUserName);
		root.addElement("FromUserName").addText(FromUserName);
		root.addElement("CreateTime").addText(CreateTime+"");
		root.addElement("MsgType").addText(MsgType);
		root.addElement("Content").addText(Content);
	}
	
	public String toXml(){
		this.addField2XmlRoot();
		return root.asXML(); 	
	}
	
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public Long getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
}
