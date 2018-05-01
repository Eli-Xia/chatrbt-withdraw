package net.monkeystudio.chatrbtw.service;

import com.google.zxing.WriterException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.utils.ImageUtils;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.base.utils.QRCodeUtil;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.EthnicGroups;
import net.monkeystudio.chatrbtw.mapper.EthnicGroupsMapper;
import net.monkeystudio.chatrbtw.sdk.wx.QrCodeHelper;
import net.monkeystudio.chatrbtw.sdk.wx.bean.qrcode.QrCodeTicker;
import net.monkeystudio.chatrbtw.service.bean.ethnicgroupscode.EthnicGroupsCodeValidatedResp;
import net.monkeystudio.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/4/10.
 */
@Service
public class EthnicGroupsService {

    @Autowired
    private EthnicGroupsMapper ethnicGroupsCodeMapper;

    public final static Integer ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ENABLE = 0; //可用
    public final static Integer ETHNIC_GROUPS_CODE_VALIDATED_STATUS_NOT_FOUND = 1; //找不到族群码
    public final static Integer ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ACOUNT_FULL = 2; //族群码满额


    public final static Integer FOUDER_TYPE = 1;
    public final static Integer SECONDER_TYPE = 2;
    public final static Integer NORMAL_TYPE = 3;

    private final static String FOUNDER_EVENT_KEY = "-1";

    public final static String EVENT_SPECIAL_STR = "keendo_chat_pet";

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;


    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private QrCodeHelper qrCodeHelper;

    /**
     * 获取一级族群
     *
     * @param wxPubOriginId
     * @return
     */
    public EthnicGroups getFounderEthnicGroups(String wxPubOriginId) {
        return ethnicGroupsCodeMapper.selectByWxPubAndSecondFunder(wxPubOriginId, null);
    }

    private Integer save(EthnicGroups ethnicGroups) {

        Integer count = ethnicGroupsCodeMapper.insert(ethnicGroups);
        return ethnicGroups.getId();
    }


    /*public EthnicGroupsCodeValidatedResp validated(String code , String wxPubOriginId ){

        EthnicGroups parentEthnicGroupsCode = this.getByWxPubAndCode(code, wxPubOriginId);

        //不存在族群码
        if(parentEthnicGroupsCode == null){
            Integer status = ETHNIC_GROUPS_CODE_VALIDATED_STATUS_NOT_FOUND;
            String content = "找不到该族群码，请检查族群码";

            return new EthnicGroupsCodeValidatedResp(status,content);
        }

        //数量超过限制
        if(parentEthnicGroupsCode.getTotalValidCount() != null){
            Integer count = ethnicGroupsCodeMapper.countByCode(code);

            if(count.intValue() >= parentEthnicGroupsCode.getTotalValidCount().intValue() ){
                String content = "族群已满员，下次早点来哦！";

                return new EthnicGroupsCodeValidatedResp(ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ACOUNT_FULL,content);
            }
        }

        return new EthnicGroupsCodeValidatedResp(ETHNIC_GROUPS_CODE_VALIDATED_STATUS_ENABLE, null);
    }*/

    /**
     * 创建创始宠物的邀请二维码
     * @param wxPubOriginId
     * @throws BizException
     * @throws IOException
     * @throws WriterException
     */
    public void createFounderQrCodeImage(String wxPubOriginId) throws BizException, IOException, WriterException {

        String result = qrCodeHelper.createQrCodeByWxPubOriginId(wxPubOriginId, 2592000, QrCodeHelper.QrCodeType.TEMP, EVENT_SPECIAL_STR + FOUNDER_EVENT_KEY);
        QrCodeTicker qrCodeTicker = JsonUtil.readValue(result, QrCodeTicker.class);

        QRCodeUtil.createQRCode(qrCodeTicker.getUrl(), "/Users/bint/Documents/chart_robot/src/chatrbtw/WebRoot/test.jpg", 1200, 1200);
    }


    public String createInvitationQrCode(Integer chatPetId) throws BizException, IOException, WriterException {

        ChatPet chatPet = chatPetService.getById(chatPetId);

        String wxPubOriginId = chatPet.getWxPubOriginId();

        String result = qrCodeHelper.createQrCodeByWxPubOriginId(wxPubOriginId, 2592000, QrCodeHelper.QrCodeType.TEMP, EVENT_SPECIAL_STR + chatPetId);
        QrCodeTicker qrCodeTicker = JsonUtil.readValue(result, QrCodeTicker.class);

        BufferedImage bufferedImage = QRCodeUtil.toBufferedImage(qrCodeTicker.getUrl(), 100, 100);

        return ImageUtils.encodeImgageToBase64(bufferedImage,"png");
    }

    public Integer createSecondEthnicGroups(String wxPubOriginId, String wxFanOpenId) {
        EthnicGroups ethnicGroups = new EthnicGroups();

        ethnicGroups.setWxPubOriginId(wxPubOriginId);
        ethnicGroups.setSecondFounderId(wxFanOpenId);
        ethnicGroups.setCodeType(SECONDER_TYPE);


        EthnicGroups parentEthnicGroups = this.getFounderEthnicGroups(wxPubOriginId);
        ethnicGroups.setParentId(parentEthnicGroups.getId());

        Date date = new Date();
        ethnicGroups.setCreateTime(date);

        return this.save(ethnicGroups);
    }

    public Boolean isFounderEventKey(String enventKey) {
        if (FOUNDER_EVENT_KEY.equals(enventKey)) {
            return true;
        }

        return false;
    }

    public Boolean isNotFounderEventKey(String enventKey) {

        return !this.isFounderEventKey(enventKey);

    }


    private EthnicGroups getSecondFounderEthnicGroups(String wxPubOriginId) {
        List<EthnicGroups> list = ethnicGroupsCodeMapper.selectFounderByWxPub(wxPubOriginId, FOUDER_TYPE);

        if (ListUtil.isEmpty(list)) {
            return null;
        }

        EthnicGroups ethnicGroups = list.get(0);

        return ethnicGroups;
    }

    private EthnicGroups getById(Integer ethnicGroupsId){
        return ethnicGroupsCodeMapper.select(ethnicGroupsId);
    }

    /**
     * 是否允许领取宠物
     * @param wxPubOriginId
     * @return
     */
    public  Boolean allowToAdopt(String wxPubOriginId){
        Integer ethnicGroupsId = this.getFounderEthnicGroups(wxPubOriginId).getId();
        return this.allowToAdopt(ethnicGroupsId);
    }

    /**
     * 是否允许领养
     * @param ethnicGroupsId
     * @return
     */
    private Boolean allowToAdopt(Integer ethnicGroupsId){
        String key = getEthnicGroupsExistingNumberKey();

        Long num = redisCacheTemplate.hincrby(key,String.valueOf(ethnicGroupsId),1L);

        EthnicGroups ethnicGroups = this.getById(ethnicGroupsId);
        Integer limit = ethnicGroups.getTotalValidCount();

        //如果没有限制，则允许
        if(limit == null){
            return true;
        }

        //如果当日领养数大于限制数，则不允许
        if(num.intValue() > limit.intValue()){
            return false;
        }

        return true;
    }


    /**
     * 重置当日的数量
     */
    public void resetDailyRestrictions(){
        String key = this.getEthnicGroupsExistingNumberKey();

        redisCacheTemplate.del(key);
    }


    private static String getEthnicGroupsExistingNumberKey(){

        String ethnicGroupsExistingNumberKey = "hash:ethnicGroupsExistingNumber";

        return ethnicGroupsExistingNumberKey;

    }
}
