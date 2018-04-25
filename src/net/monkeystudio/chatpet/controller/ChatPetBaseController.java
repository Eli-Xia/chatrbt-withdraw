package net.monkeystudio.chatpet.controller;

import net.monkeystudio.Constants;
import net.monkeystudio.base.BaseController;

/**
 * @author xiaxin
 */
public class ChatPetBaseController extends BaseController {
    @Override
    protected String getSessionUserIdAttrName() {
       return Constants.SESSION_ATTR_NAME_CHATPET_USERID;
    }
}
