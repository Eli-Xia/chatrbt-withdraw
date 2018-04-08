package net.monkeystudio.admin.controller.resp;

import java.util.List;

/**
 * 树型导航菜单节点数据结构
 * @author hebo
 *
 */
public class NaviItem {

	private Integer id;
	private String text;
	private String link;
	
	private List<NaviItem> children;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<NaviItem> getChildren() {
		return children;
	}

	public void setChildren(List<NaviItem> children) {
		this.children = children;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
}
