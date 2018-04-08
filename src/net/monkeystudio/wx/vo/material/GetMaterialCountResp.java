package net.monkeystudio.wx.vo.material;

/**
 * 获取素材总数
 * @author hebo
 *
 */
public class GetMaterialCountResp {

	private Integer voice_count;
	private Integer video_count;
	private Integer image_count;
	private Integer news_count;
	
	@Override
	public String toString() {
		return "voice_count:"+ voice_count + ",video_count:" + video_count + ",image_count:" + image_count + ",news_count:" + news_count;
	}
	
	public Integer getVoice_count() {
		return voice_count;
	}
	public void setVoice_count(Integer voice_count) {
		this.voice_count = voice_count;
	}
	public Integer getVideo_count() {
		return video_count;
	}
	public void setVideo_count(Integer video_count) {
		this.video_count = video_count;
	}
	public Integer getImage_count() {
		return image_count;
	}
	public void setImage_count(Integer image_count) {
		this.image_count = image_count;
	}
	public Integer getNews_count() {
		return news_count;
	}
	public void setNews_count(Integer news_count) {
		this.news_count = news_count;
	}
	
	
}
