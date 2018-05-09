package net.monkeystudio.chatrbtw.sdk.wx;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import net.monkeystudio.wx.vo.user.WxFanBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2017/12/11.
 */
//TODO 去除掉BizException的依赖
@Service
public class WxFanHelper {

    @Autowired
    private WxAuthApiService wxAuthApiService;


    /**
     * 从微信平台获取粉丝的基础信息
     * @param userOpenId
     * @param wxPubAppId
     * @return
     */
    public WxFanBaseInfo fetcWxhUserBaseInfo(String userOpenId , String wxPubAppId) throws BizException {

        String accessToken = wxAuthApiService.getAuthorizerAccessToken(wxPubAppId);

        String fetchUserBaseInfoUrl = WxApiUrlUtil.getFetchUserBaseInfoUrl(accessToken,userOpenId);

        String response = HttpsHelper.get(fetchUserBaseInfoUrl);

        Log.d("============ wx fan response = {?} ==================",response);

        if (response.indexOf("errcode") != -1){
            Log.i(response);
            return null;
        }

        WxFanBaseInfo userBaseInfo = JsonUtil.readValue(response, WxFanBaseInfo.class);

        return userBaseInfo;
    }



}
