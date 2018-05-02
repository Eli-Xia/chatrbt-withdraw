package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.service.GlobalConstants;
import net.monkeystudio.base.utils.Base64EncodingUtil;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.URLUtil;
import net.monkeystudio.chatrbtw.entity.ChatRobotBaseInfo;
import net.monkeystudio.chatrbtw.entity.RRobotCharacter;
import net.monkeystudio.portal.controller.req.chatrotot.AddChatRobot;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import net.monkeystudio.wx.vo.thirtparty.ComponentAccessToken;
import net.monkeystudio.wx.vo.thirtparty.PreAuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 03/01/2018.
 */
@Service
public class WxPubAuthService {


    private final static String REDIRECT_URI = "/api/wx-pub/auth-callback";

    @Autowired
    private CfgService cfgService ;

    @Autowired
    private WxAuthApiService wxAuthApiService;

    @Autowired
    private ChatRobotService chatRobotBaseInfoService;

    @Autowired
    private UserExtService userExtService;


    private static String componentAppId = null;
    private static String appSecret = null;

    private static String redirctUriPart = null;


    @PostConstruct
    private void init(){
        componentAppId = cfgService.get(GlobalConfigConstants.COMPONENT_APP_ID_KEY);
        appSecret = cfgService.get(GlobalConfigConstants.APP_SECRET_KEY);

        String domain = "http://" + cfgService.get(GlobalConfigConstants.WEB_DOMAIN_KEY);
        redirctUriPart = domain + REDIRECT_URI;
    }

    /**
     * portal获取接入公众号的二维码链接
     * @param addChatRobot
     * @param userId
     * @return
     * @throws BizException
     */
    @Transactional
    public String getPortalJoinUrl(AddChatRobot addChatRobot,Integer userId) throws BizException {

        ChatRobotBaseInfo chatRobotBaseInfo = BeanUtils.copyBean(addChatRobot, ChatRobotBaseInfo.class);
        Integer robotId = chatRobotBaseInfoService.saveChatRobotBaseInfo(chatRobotBaseInfo);

        List<Integer> characterList = addChatRobot.getCharacterList();

        for(Integer characterId:characterList){
            RRobotCharacter rRobotCharacter = new RRobotCharacter();

            rRobotCharacter.setChatRobotId(robotId);
            rRobotCharacter.setChatRobotCharacterId(characterId);

            chatRobotBaseInfoService.saveRRobotCharacter(rRobotCharacter);
        }
        String url = this.getPortalAuthPageUrl(userId, robotId);

        return url;
    }

    /**
     * 获取微赞入口的认证url
     * @return
     * @throws BizException
     */
    public String getWeizanAuthUrl() throws BizException {

        String preAuthCodeStr = this.getPreAuthCodeStr();

        String encrypt = this.getWeizanEncrypStr();

        String redirctUrl = URLUtil.addPathParam(redirctUriPart,encrypt);

        String authPageUrl = WxApiUrlUtil.getAuthPageUrl(componentAppId ,preAuthCodeStr ,redirctUrl);

        return authPageUrl;
    }



    private String getPortalAuthPageUrl(Integer userId ,Integer robotId) throws BizException {

        String preAuthCodeStr = this.getPreAuthCodeStr();

        String encrypt = this.getPortalEncrypStr(userId, robotId);

        String redirctUrl = URLUtil.addPathParam(redirctUriPart,encrypt);

        String authPageUrl = WxApiUrlUtil.getAuthPageUrl(componentAppId,preAuthCodeStr,redirctUrl);

        return authPageUrl;
    }

    private String getPreAuthCodeStr() throws BizException {
        String componentAccessTokenStr = wxAuthApiService.getComponentAccessTokenStr();

        PreAuthCode preAuthCode = wxAuthApiService.fetchPreAuthCodeFromWx(componentAccessTokenStr);

        if(preAuthCode == null){
            throw new BizException("获取不到preAuthCode");
        }

        String preAuthCodeStr = preAuthCode.getPreAuthCode();

        return preAuthCodeStr;
    }

    /**
     * 获取portal入口加密的uri加密字符串
     * @param userId
     * @param robotId
     * @return
     * @throws BizException
     */
    private String getPortalEncrypStr(Integer userId ,Integer robotId) throws BizException {

        Integer source = GlobalConstants.JOIN_SOURCE_PORTAL;

        return this.getEncrypStr(userId,source,robotId);
    }

    /**
     * 获取微赞入口的加密的uri加密字符串
     * @return
     * @throws BizException
     */
    private String getWeizanEncrypStr() throws BizException {
        Integer weizanUserId = userExtService.getWeizanUserId();

        Integer source = GlobalConstants.JOIN_SOURCE_WEIZAN;

        return this.getEncrypStr(weizanUserId,source,null);
    }

    /**
     * 获取加密的uri字符串
     * @param userId
     * @param source
     * @param robotId
     * @return
     * @throws BizException
     */
    private String getEncrypStr(Integer userId ,Integer source , Integer robotId) throws BizException {

        String encryptSource = URLUtil.addParam("","userId", String.valueOf(userId));
        encryptSource = URLUtil.addParam(encryptSource, "source", String.valueOf(source));

        if(robotId != null){
            encryptSource = URLUtil.addParam(encryptSource, "chatbotId", String.valueOf(robotId));
        }

        String encrypt = null;
        try {
            encrypt = Base64EncodingUtil.encryptBASE64(encryptSource);
        } catch (Exception e) {
            throw new BizException("加密出错");
        }

        return encrypt;

    }
}
