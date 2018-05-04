package net.monkeystudio.base.springmvc;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * ViewResolver for GlhFreeMarkerView
 * Override buildView, if viewName start with / , then ignore prefix.
 */
public class MyFreemarkerViewResolver   extends AbstractTemplateViewResolver
{
	/**
	 * Set default viewClass
	 */
	public MyFreemarkerViewResolver() 
	{
		setViewClass(MyFreemarkerView.class);
	}
	
	/**
	 * if viewName start with / , then ignore prefix.
	 */
	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception 
	{
		AbstractUrlBasedView view = super.buildView(viewName);
		
		// start with / ignore prefix
		if (viewName.startsWith("/")) 
		{
			view.setUrl(viewName + getSuffix());
		}
		
		return view;
	}
}
