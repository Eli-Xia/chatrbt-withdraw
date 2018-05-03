package net.monkeystudio.chatpet.controller;

import net.monkeystudio.Constants;
import net.monkeystudio.base.BaseController;

/**
 * @author xiaxin
 */
public class ChatPetBaseController extends BaseController {
    public static final String SESSION_ATTR_NAME_CHATPET_USERID = "SESSION_ATTR_NAME_CHATPET_USERID";
    @Override
    protected String getSessionUserIdAttrName() {
       return SESSION_ATTR_NAME_CHATPET_USERID;
    }
}
