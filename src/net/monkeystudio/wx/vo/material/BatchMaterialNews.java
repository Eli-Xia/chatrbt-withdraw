package net.monkeystudio.wx.vo.material;

import java.util.List;

/**
 * 批量获取永久图文素材响应
 * @author hebo
 *
 */
public class BatchMaterialNews {

	private Integer total_count;
	private Integer item_count;
	private Item[] item;

	public Integer getTotal_count() {
		return total_count;
	}

	public void setTotal_count(Integer total_count) {
		this.total_count = total_count;
	}

	public Integer getItem_count() {
		return item_count;
	}

	public void setItem_count(Integer item_count) {
		this.item_count = item_count;
	}

	public Item[] getItem() {
		return item;
	}

	public void setItem(Item[] item) {
		this.item = item;
	}
	
	public static class Item {
		private String media_id;
		private Content content;
		private Long update_time;
		
		public String getMedia_id() {
			return media_id;
		}
		public void setMedia_id(String media_id) {
			this.media_id = media_id;
		}
		public Content getContent() {
			return content;
		}
		public void setContent(Content content) {
			this.content = content;
		}
		public Long getUpdate_time() {
			return update_time;
		}
		public void setUpdate_time(Long update_time) {
			this.update_time = update_time;
		}
	}
	

	public static class Content {
		private List<NewsItem> news_item;
		private Long create_time;
		private Long update_time;
	
		public List<NewsItem> getNews_item() {
			return news_item;
		}
	
		public void setNews_item(List<NewsItem> news_item) {
			this.news_item = news_item;
		}
	
		public Long getCreate_time() {
			return create_time;
		}
	
		public void setCreate_time(Long create_time) {
			this.create_time = create_time;
		}
	
		public Long getUpdate_time() {
			return update_time;
		}
	
		public void setUpdate_time(Long update_time) {
			this.update_time = update_time;
		}
	}
	
	public static class NewsItem{
		
		private String title;
		private String thumb_media_id;
		private Integer show_cover_pic;
		private String author;
		private String digest;
		private String content;
		private String url;
		private String content_source_url;
		private String thumb_url;
		private Integer need_open_comment;
		private Integer only_fans_can_comment;
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getThumb_media_id() {
			return thumb_media_id;
		}
		public void setThumb_media_id(String thumb_media_id) {
			this.thumb_media_id = thumb_media_id;
		}
		public Integer getShow_cover_pic() {
			return show_cover_pic;
		}
		public void setShow_cover_pic(Integer show_cover_pic) {
			this.show_cover_pic = show_cover_pic;
		}
		public String getAuthor() {
			return author;
		}
		public void setAuthor(String author) {
			this.author = author;
		}
		public String getDigest() {
			return digest;
		}
		public void setDigest(String digest) {
			this.digest = digest;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getContent_source_url() {
			return content_source_url;
		}
		public void setContent_source_url(String content_source_url) {
			this.content_source_url = content_source_url;
		}
		public String getThumb_url() {
			return thumb_url;
		}
		public void setThumb_url(String thumb_url) {
			this.thumb_url = thumb_url;
		}
		public Integer getNeed_open_comment() {
			return need_open_comment;
		}
		public void setNeed_open_comment(Integer need_open_comment) {
			this.need_open_comment = need_open_comment;
		}
		public Integer getOnly_fans_can_comment() {
			return only_fans_can_comment;
		}
		public void setOnly_fans_can_comment(Integer only_fans_can_comment) {
			this.only_fans_can_comment = only_fans_can_comment;
		}
	}

	
	
}
