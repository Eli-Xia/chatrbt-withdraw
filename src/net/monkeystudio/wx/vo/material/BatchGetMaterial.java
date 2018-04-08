package net.monkeystudio.wx.vo.material;

/**
 * 批量获取公众号素材
 * @author hebo
 *
 */
public class BatchGetMaterial {

	private String type;    //素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
	private Integer offset; //从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
	private Integer count;  //返回素材的数量，取值在1到20之间
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	
}
