package net.monkeystudio.wx.vo.customerservice;

/**
 * 图文消息
 * @author hebo
 *
 */
public class CustomerMsgMpNews extends CustomerMsgBase{

	private MpNews mpnews;
	
	public CustomerMsgMpNews(String touser, String mediaId){
		this.msgtype = "mpnews";
		this.touser = touser;
		this.setMpnews(mediaId);
	}
	
	public MpNews getMpnews() {
		return mpnews;
	}

	public void setMpnews(MpNews mpnews) {
		this.mpnews = mpnews;
	}
	
	private void setMpnews(String mediaId){
		MpNews mpNews = new MpNews();
		mpNews.setMedia_id(mediaId);
		this.mpnews = mpNews;
	}
	
	public class MpNews{
		
		String media_id;

		public String getMedia_id() {
			return media_id;
		}

		public void setMedia_id(String media_id) {
			this.media_id = media_id;
		}
	}
}
