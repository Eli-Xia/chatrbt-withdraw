package net.monkeystudio.chatrbtw.sdk.wx;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.sdk.wx.bean.MiniProgramResponse;
import net.monkeystudio.chatrbtw.sdk.wx.bean.msgtemplate.MsgTemplateParam;
import net.monkeystudio.chatrbtw.service.MiniProgramService;
import net.monkeystudio.chatrbtw.service.WxFanService;
import net.monkeystudio.wx.service.WxAuthApiService;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 2018/7/31.
 */
@Service
public class WxMsgTemplateHelper {

    @Autowired
    private WxAuthApiService wxAuthApiService;

    @Autowired
    private WxFanService wxFanService;

    @Autowired
    private MiniProgramService miniProgramService;

    /**
     * 发送模版消息
     * @param msgTemplateParam
     * @param wxFanId
     * @throws BizException
     */
    public MiniProgramResponse sendTemplateMsg(Integer wxFanId ,MsgTemplateParam msgTemplateParam) throws BizException {

        WxFan wxFan = wxFanService.getById(wxFanId);

        Integer miniProgramId = wxFan.getMiniProgramId();

        if(miniProgramId == null){
            throw new BizException("暂只支持小程序发送消息");
        }

        String openId = wxFan.getWxFanOpenId();

        msgTemplateParam.setTouser(openId);

        String accessToken = miniProgramService.getAcceessToken(miniProgramId);

        String url = WxApiUrlUtil.getSendTemplateUrl(accessToken);
        String jsonStr = JsonUtil.toJSon(msgTemplateParam);
        String response = HttpsHelper.postJsonByStr(url, jsonStr);

        MiniProgramResponse miniProgramResponse = JsonUtil.readValue(response, MiniProgramResponse.class);

        return miniProgramResponse;
    }
}
