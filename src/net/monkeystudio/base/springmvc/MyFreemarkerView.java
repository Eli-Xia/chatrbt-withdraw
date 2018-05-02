package net.monkeystudio.base.springmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.monkeystudio.base.Constants;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

/**
 * 扩展spring的FreemarkerView，加上base属性。
 * 支持jsp标签，Application、Session、Request、RequestParameters属性
 */
public class MyFreemarkerView  extends FreeMarkerView
{
	/**
	 * 在model中增加部署路径base，方便处理部署路径问题。
	 */
	@SuppressWarnings("unchecked")
	protected void exposeHelpers(Map model, HttpServletRequest request) throws Exception 
	{
		super.exposeHelpers(model, request);
		
		//webroot
		String contextPath = request.getContextPath();
	    model.put(Constants.FM_TAG_CONTEXT_PATH, contextPath);
	   
	}
}
