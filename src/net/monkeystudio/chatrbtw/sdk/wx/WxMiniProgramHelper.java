package net.monkeystudio.chatrbtw.sdk.wx;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.MiniProgram;
import net.monkeystudio.chatrbtw.sdk.wx.bean.miniapp.LoginVerifyInfo;
import net.monkeystudio.chatrbtw.service.MiniProgramService;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaxin
 */
@Service
public class WxMiniProgramHelper {
    @Autowired
    private CfgService cfgService;

    @Autowired
    private MiniProgramService miniProgramService;


    /**
     * 小程序用户登录,通过code获取openId,unionId,session_key
     * @param jsCode
     * @return
     */
    public String getfetchLoginVerifyUrl(String appId,String appSecret,String jsCode){

        String url =  WxApiUrlUtil.getMiniAppLoginVerifyUrl(appId,appSecret,jsCode);

        return url;
    }

    /**
     * 小程序登录校验信息
     * @param jsCode
     * @param miniProgramId
     * @return
     */
    public LoginVerifyInfo fetchLoginVerifyInfo(Integer miniProgramId,String jsCode) throws BizException{
        Log.d("================= jsCode = {?} =================",jsCode);
        MiniProgram miniProgram = miniProgramService.getById(miniProgramId);
        String appId = miniProgram.getAppId();
        String appSecret = miniProgram.getAppSecret();

        String fetchLoginVerifyInfoUrl = this.getfetchLoginVerifyUrl(appId,appSecret,jsCode);

        String response = HttpsHelper.get(fetchLoginVerifyInfoUrl);

        if(response == null || response.indexOf("errorcode") != -1){
            throw new BizException("小程序登录校验失败");
        }

        Log.d("================= fetchMiniAppLoginVerifyInfo response = {?} ===================",response);

        LoginVerifyInfo loginVerifyInfo = JsonUtil.readValue(response, LoginVerifyInfo.class);

        if(loginVerifyInfo != null){
            Log.d("============ LoginVerifyInfo:  sessionkey = {?} , openid = {?} =============",loginVerifyInfo.getSessionKey(),loginVerifyInfo.getOpneId());
        }

        return loginVerifyInfo;
    }


}
