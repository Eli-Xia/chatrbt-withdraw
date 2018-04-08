package net.monkeystudio.wx.vo.customerservice;

/**
 * 文本消息消息
 * @author hebo
 *
 */
public class CustomerMsgText extends CustomerMsgBase{

	private Text text;
	
	public CustomerMsgText(String touser, String content){
		this.msgtype = "text";
		this.touser = touser;
		this.setText(content);
	}
	
	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	private void setText(String content){
		Text text = new Text();
		text.setContent(content);
		this.text = text;
	}
	
	public class Text {

	    private String content;

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	    }
	}
}
