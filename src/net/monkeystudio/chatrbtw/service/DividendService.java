package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.service.TaskExecutor;
import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.ChatPet;
import net.monkeystudio.chatrbtw.entity.DividendDetailRecord;
import net.monkeystudio.chatrbtw.entity.DividendRecord;
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


}
