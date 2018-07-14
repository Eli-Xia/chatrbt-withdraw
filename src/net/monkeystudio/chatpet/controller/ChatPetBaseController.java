package net.monkeystudio.chatpet.controller;


import net.monkeystudio.base.controller.BaseController;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.StringUtil;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.service.WxFanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author xiaxin
 */
public class ChatPetBaseController extends BaseController {
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private HttpSession httpSession;

    public static final String SESSION_ATTR_NAME_CHATPET_USERID = "SESSION_ATTR_NAME_CHATPET_USERID";

    private static final String SESSION_TOKEN_KEY_SUFFIX = "str:miniAppSessionToken:";



    @Override
    protected String getSessionUserIdAttrName() {
       return SESSION_ATTR_NAME_CHATPET_USERID;
    }


    @Override
    protected Integer getUserId() {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader("token");

        if(token != null){
            String value = redisCacheTemplate.getString(SESSION_TOKEN_KEY_SUFFIX + token);
            String userOpenId = value.split(":")[0];
            //根据openId查询wxFanId
            WxFan wxFan = wxFanService.getWxFan(userOpenId, wxFanService.LUCK_CAT_MINI_APP_ID);
            if(wxFan != null){
                return wxFan.getId();
            }
        }

        Integer userId = (Integer)httpSession.getAttribute(SESSION_ATTR_NAME_CHATPET_USERID);

        return userId;
    }


}
