package net.monkeystudio.portal.controller;

import net.monkeystudio.base.Constants;
import net.monkeystudio.base.controller.BaseController;
import org.springframework.stereotype.Controller;


@Controller
public class PortalBaseController extends BaseController {
	
	@Override
	protected String getSessionUserIdAttrName(){
		return Constants.SESSION_ATTR_NAME_PORTAL_USERID;
	}
}
