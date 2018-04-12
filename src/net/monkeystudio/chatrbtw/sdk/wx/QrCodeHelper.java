package net.monkeystudio.chatrbtw.sdk.wx;

import net.monkeystudio.exception.BizException;
import net.monkeystudio.utils.HttpUtils;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/4/12.
 */
@Service
public class QrCodeHelper {

    @Autowired
    private WxAuthApiService wxAuthApiService;

    public String get(String wxPubAppId) throws BizException {

        String accessToken = wxAuthApiService.getAuthorizerAccessToken(wxPubAppId);

        String url = WxApiUrlUtil.getCreateTempQrCodeUrl(accessToken);

        //HttpUtils.postJson(url);
        return null;
    }

}
