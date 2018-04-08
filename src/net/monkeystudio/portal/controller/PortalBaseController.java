package net.monkeystudio.portal.controller;

import org.springframework.stereotype.Controller;

import net.monkeystudio.Constants;
import net.monkeystudio.base.BaseController;

@Controller
public class PortalBaseController extends BaseController{
	
	@Override
	protected String getSessionUserIdAttrName(){
		return Constants.SESSION_ATTR_NAME_PORTAL_USERID;
	}
}
