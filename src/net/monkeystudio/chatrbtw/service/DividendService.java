package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.exception.BizException;
import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.sdk.wx.WxMsgTemplateHelper;
import net.monkeystudio.chatrbtw.sdk.wx.bean.msgtemplate.Data;
import net.monkeystudio.chatrbtw.sdk.wx.bean.msgtemplate.Keyword;
import net.monkeystudio.chatrbtw.sdk.wx.bean.msgtemplate.MsgTemplateParam;
import net.monkeystudio.chatrbtw.service.bean.dividendrecord.DividendQueueValueVO;
import net.monkeystudio.chatrbtw.utils.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/7/16.
 */
@Service
public class DividendService {


    @Autowired
    private DividendRecordService dividendRecordService;

    @Autowired
    private DividendDetailRecordService dividendDetailRecordService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private WxMsgTemplateHelper wxMsgTemplateHelper;

    @Autowired
    private MsgTemplateFormService msgTemplateFormService;

    @Autowired
    private WxFanService wxFanService;


    @Autowired
    private MsgTemplateService msgTemplateService;


    /**
     * 分红队列消耗
     * @return
     */
    public void dividendQuqueConsume(){
        String key = this.getDividendQuqueKey();

        List<String> list = redisCacheTemplate.brpop(0,key);

        String dividendQuqueValue = list.get(1);

        DividendQueueValueVO dividendQueueValueVO = this.paraseDividendQuqueValue(dividendQuqueValue);

        Double totalCoin = dividendQueueValueVO.getTotalCoin();
        BigDecimal totalExperienceBD = new BigDecimal(totalCoin);

        Float totalMoney = dividendQueueValueVO.getTotalMoney();
        BigDecimal totalMoneyBD = new BigDecimal(totalMoney);

        BigDecimal ratio = ArithmeticUtils.divide(totalMoneyBD, totalExperienceBD);

        Float experience = dividendQueueValueVO.getCoin();
        BigDecimal experienceBD = new BigDecimal(experience);
        BigDecimal moneyBD = experienceBD.multiply(ratio);

        moneyBD = BigDecimalUtil.dealDecimalPoint(moneyBD, 2);

        Float money = moneyBD.floatValue();

        Integer chatPetId = dividendQueueValueVO.getChatPetId();

        chatPetService.increaseMoney(chatPetId, money);


        DividendDetailRecord dividendDetailRecord = new DividendDetailRecord();
        dividendDetailRecord.setChatPetId(chatPetId);
        dividendDetailRecord.setMoney(money);
        dividendDetailRecord.setDividendId(dividendQueueValueVO.getDividendRecordId());
        dividendDetailRecord.setCreateTime(new Date());

        dividendDetailRecordService.save(dividendDetailRecord);


        ChatPet chatPet = chatPetService.getById(chatPetId);

        Integer wxFanId = chatPet.getWxFanId();

        //如果分红不为0,则发送消息通知
        if(money.floatValue() != 0F){

            this.sendMsg(wxFanId);
        }


    }



    private String getDividendQuqueKey(){
        String key = RedisTypeConstants.KEY_LIST_TYPE_PREFIX + "dividend-quque";
        return key;
    }


    public void listenDividendQuque(){
        //起一条独立的线程去监听
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    dividendQuqueConsume();
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("listenDividendQuque");
        thread.start();
        Log.d("finished listen DividendQuque");
    }


    /**
     * 解析分红队列的值
     * @param dividendQuqueValue
     * @return
     */
    private DividendQueueValueVO paraseDividendQuqueValue(String dividendQuqueValue){

        DividendQueueValueVO dividendQueueValueVO = new DividendQueueValueVO();

        String[] valueArray = dividendQuqueValue.split(":");

        Integer chatPetType = Integer.valueOf(valueArray[0]);
        dividendQueueValueVO.setChatPetType(chatPetType);

        Float money = Float.valueOf(valueArray[1]);
        dividendQueueValueVO.setTotalMoney(money);

        Integer chatPetId = Integer.valueOf(valueArray[2]);
        dividendQueueValueVO.setChatPetId(chatPetId);

        Float coin = Float.valueOf(valueArray[3]);
        dividendQueueValueVO.setCoin(coin);

        Double totalExperience = Double.valueOf(valueArray[4]);
        dividendQueueValueVO.setTotalCoin(totalExperience);

        Integer DividendRecordId = Integer.valueOf(valueArray[5]);
        dividendQueueValueVO.setDividendRecordId(DividendRecordId);

        return dividendQueueValueVO;

    }


    /**
     * 分红
     * @param totalMoney
     * @param chatPetType
     * @return
     */
    public void dividend(Float totalMoney ,Integer chatPetType){

        //保存分红记录
        DividendRecord dividendRecord = new DividendRecord();
        dividendRecord.setCreateTime(new Date());
        dividendRecord.setMoney(totalMoney);
        dividendRecord.setChatPetType(chatPetType);

        List<ChatPet> chatPetList = chatPetService.getByChatPetType(chatPetType);
        dividendRecord.setTotalWxfanNumber(chatPetList.size());

        Double totalCoin = chatPetService.countTotalCoin(chatPetType);
        dividendRecord.setTotalCoin(totalCoin);

        dividendRecordService.save(dividendRecord);

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {

                String key = getDividendQuqueKey();
                Integer dividendRecordId = dividendRecord.getId();

                for(ChatPet chatPet : chatPetList){
                    String value = getDividendQuqueValue(totalMoney, chatPetType, chatPet.getId(), chatPet.getCoin() , totalCoin,dividendRecordId);
                    redisCacheTemplate.lpush(key,value);
                }
            }
        });
    }


    private String getDividendQuqueValue(Float totalMoney,Integer chatPetType , Integer chatPetId ,Float experience , Double totalCoin ,Integer dividendRecordId){

        String moneyStr = String.valueOf(totalMoney);

        String experienceStr = String.valueOf(experience);

        String value = chatPetType + ":" + moneyStr + ":" + chatPetId + ":" + experienceStr + ":" + totalCoin + ":" + dividendRecordId;

        return value;

    }

    public void sendMsg(Integer wxFanId){

        MsgTemplateParam msgTemplateParam = new MsgTemplateParam();

        Data data = new Data();

        WxFan wxFan = wxFanService.getById(wxFanId);
        msgTemplateParam.setTouser(wxFan.getWxFanOpenId());

        MsgTemplateForm msgTemplateForm = msgTemplateFormService.getEnable(wxFanId);

        //如果没有可用的消息表单,则返回
        if(msgTemplateForm == null){
            Log.d("wxFanId [?] msgTemplateForm is null " ,String.valueOf(wxFanId));
            return ;
        }

        msgTemplateFormService.updateState(msgTemplateForm.getId());

        msgTemplateParam.setFormId(msgTemplateForm.getFormId());

        Keyword keyword1 = new Keyword();
        keyword1.setValue("猫六六乐园城市分红");
        data.setKeyword1(keyword1);

        Keyword keyword2 = new Keyword();
        keyword2.setValue("城市分红已到账，快查查又收到多少money吧！");
        data.setKeyword2(keyword2);

        msgTemplateParam.setData(data);

        MsgTemplate msgTemplate = msgTemplateService.getByMiniProgramIdAndCode(wxFan.getMiniProgramId(), MsgTemplateService.Constants.DIVIDEND_MSG_CODE);
        msgTemplateParam.setTemplateId(msgTemplate.getTemplateId());

        msgTemplateParam.setPage("/pages/dividend/dividend");

        try {
            wxMsgTemplateHelper.sendTemplateMsg(wxFanId,msgTemplateParam);
        } catch (BizException e) {
            Log.e(e);
        }
    }
}
