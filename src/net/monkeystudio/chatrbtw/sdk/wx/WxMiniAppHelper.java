package net.monkeystudio.chatrbtw.sdk.wx;

import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.sdk.wx.bean.miniapp.LoginVerifyInfo;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class WxMiniAppHelper {
    @Autowired
    private CfgService cfgService;


    /**
     * 小程序用户登录,通过code获取openId,unionId,session_key
     * @param jsCode
     * @return
     */
    public String getfetchLoginVerifyUrl(String jsCode){
        String appId = cfgService.get(GlobalConfigConstants.MINI_APP_APP_ID);

        String secrect = cfgService.get(GlobalConfigConstants.MINI_APP_APP_SECRET);

        String url =  WxApiUrlUtil.getMiniAppLoginVerifyUrl(appId,secrect,jsCode);

        return url;
    }

    /**
     * 小程序登录校验信息
     * @param jsCode
     * @return
     */
    public LoginVerifyInfo fetchLoginVerifyInfo(String jsCode){
        Log.d("================= jsCode = {?} =================",jsCode);
        String fetchLoginVerifyInfoUrl = this.getfetchLoginVerifyUrl(jsCode);

        String response = HttpsHelper.get(fetchLoginVerifyInfoUrl);

        if(response == null || response.indexOf("errorcode") != -1){
            return null;
        }

        Log.d("================= fetchMiniAppLoginVerifyInfo response = {?} ===================",response);

        LoginVerifyInfo loginVerifyInfo = JsonUtil.readValue(response, LoginVerifyInfo.class);

        if(loginVerifyInfo != null){
            Log.d("============ LoginVerifyInfo:  sessionkey = {?} , openid = {?} =============",loginVerifyInfo.getSessionKey(),loginVerifyInfo.getOpneId());
        }

        return loginVerifyInfo;
    }


}
