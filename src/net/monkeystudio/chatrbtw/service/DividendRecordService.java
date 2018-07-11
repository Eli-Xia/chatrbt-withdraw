package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.DividendDetailRecord;
import net.monkeystudio.chatrbtw.entity.DividendRecord;
import net.monkeystudio.chatrbtw.entity.WxFan;
import net.monkeystudio.chatrbtw.mapper.DividendRecordMapper;
import net.monkeystudio.chatrbtw.service.bean.dividendrecord.DividendQueueValueVO;
import net.monkeystudio.chatrbtw.utils.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by bint on 2018/7/10.
 */
@Service
public class DividendRecordService {

    @Autowired
    private DividendRecordMapper dividendRecordMapper;

    @Autowired
    private ChatPetService chatPetService;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;


    @Autowired
    private DividendDetailRecordService dividendDetailRecordService;

    @Autowired
    private TaskExecutor taskExecutor;


    private Integer save(DividendRecord dividendRecord){
        return dividendRecordMapper.insert(dividendRecord);
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

        this.save(dividendRecord);

        Integer dividendRecordId = dividendRecord.getId();

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<ChatPet> chatPetList = chatPetService.getByChatPetType(chatPetType);
                String key = getDividendQuqueKey();

                Double totalExperience = chatPetService.countTotalexperience(chatPetType);

                for(ChatPet chatPet : chatPetList){
                    String value = getDividendQuqueValue(totalMoney, chatPetType, chatPet.getId(), chatPet.getExperience() ,totalExperience ,dividendRecordId);
                    redisCacheTemplate.lpush(key,value);
                }
            }
        });
    }


    private String getDividendQuqueValue(Float totalMoney,Integer chatPetType , Integer chatPetId ,Float experience , Double totalExperience ,Integer dividendRecordId){

        String moneyStr = String.valueOf(totalMoney);

        String experienceStr = String.valueOf(experience);

        String value = chatPetType + ":" + moneyStr + ":" + chatPetId + ":" + experienceStr + ":" + totalExperience + ":" + dividendRecordId;

        return value;

    }


    private String getDividendQuqueKey(){
        String key = "dividend-quque";
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
     * 分红队列消耗
     * @return
     */
    public void dividendQuqueConsume(){
        String key = this.getDividendQuqueKey();

        List<String> list = redisCacheTemplate.brpop(0,key);

        String dividendQuqueValue = list.get(1);

        DividendQueueValueVO dividendQueueValueVO = this.paraseDividendQuqueValue(dividendQuqueValue);

        Double totalExperience = dividendQueueValueVO.getTotalExperience();
        BigDecimal totalExperienceBD = new BigDecimal(totalExperience);



        Float totalMoney = dividendQueueValueVO.getTotalMoney();
        BigDecimal totalMoneyBD = new BigDecimal(totalMoney);

        BigDecimal ratio = ArithmeticUtils.divide(totalMoneyBD, totalExperienceBD);

        Float experience = dividendQueueValueVO.getExperience();
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

        Float experience = Float.valueOf(valueArray[3]);
        dividendQueueValueVO.setExperience(experience);

        Double totalExperience = Double.valueOf(valueArray[4]);
        dividendQueueValueVO.setTotalExperience(totalExperience);

        Integer DividendRecordId = Integer.valueOf(valueArray[5]);
        dividendQueueValueVO.setDividendRecordId(DividendRecordId);

        return dividendQueueValueVO;

    }


}
