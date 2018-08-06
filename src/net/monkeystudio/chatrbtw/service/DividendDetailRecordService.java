package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.chatrbtw.entity.DividendDetailRecord;
import net.monkeystudio.chatrbtw.mapper.DividendDetailRecordMapper;
import net.monkeystudio.chatrbtw.service.bean.chatpetmyinfo.ChatPetDividendDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * Created by bint on 2018/7/10.
 */
@Service
public class DividendDetailRecordService {

    @Autowired
    private DividendDetailRecordMapper dividendDetailRecordMapper;


    public Integer save(DividendDetailRecord dividendDetailRecord){
        return dividendDetailRecordMapper.insert(dividendDetailRecord);
    }

    /**
     * 根据宠物id获取分红记录List
     * @param chatPetId
     * @return
     */
    private List<DividendDetailRecord> getDividendDetailRecordByChatPetId(Integer chatPetId){
        return dividendDetailRecordMapper.selectDetailRecordByChatPetId(chatPetId);
    }

    /**
     * 城市分红记录和提现记录按时间大小排列
     * @return
     */
    public List<ChatPetDividendDetail> getChatPetDividendDetailList(Integer chatPetId){
        List<DividendDetailRecord> recordList = this.getDividendDetailRecordByChatPetId(chatPetId);

        List<ChatPetDividendDetail> detailList = new ArrayList<>();

        for(DividendDetailRecord record:recordList){
            ChatPetDividendDetail chatPetDividendDetail = this.generateChatPetDividendDetail(record);
            detailList.add(chatPetDividendDetail);
        }

        //返回排序后的100条记录
        return this.sortChatPetDividendDetailListByDate(detailList);
    }

    /**
     * 城市分红记录按时间从大到小排序,取100条记录
     */
    private List<ChatPetDividendDetail> sortChatPetDividendDetailListByDate(List<ChatPetDividendDetail> detailList){
        Collections.sort(detailList, new Comparator<ChatPetDividendDetail>() {
            @Override
            public int compare(ChatPetDividendDetail o1, ChatPetDividendDetail o2) {
                int result;

                result = o1.getCreateTime().compareTo(o2.getCreateTime()) > 0 ? -1 : (o1.getCreateTime().compareTo(o2.getCreateTime()) == 0 ? 0 : 1);

                return result;
            }
        });

        List<ChatPetDividendDetail> sortedChatPetDividendDetails = new ArrayList<>();

        Iterator<ChatPetDividendDetail> iterator = detailList.iterator();

        int count = 0;

        while (iterator.hasNext()){
            if(count >= 100){
                break;
            }

            ChatPetDividendDetail obj = iterator.next();

            sortedChatPetDividendDetails.add(obj);

            count++;
        }

        return sortedChatPetDividendDetails;

    }

    /**
     * 根据宠物id统计总分红金额
     * @param chatPetId
     * @return
     */
    public Float getTotalDividendByChatPetId(Integer chatPetId){
        return dividendDetailRecordMapper.sumTotalDividendMoneyByChatPetId(chatPetId);
    }


    private ChatPetDividendDetail generateChatPetDividendDetail(DividendDetailRecord record){
        ChatPetDividendDetail chatPetDividendDetail = new ChatPetDividendDetail();

        Float money = record.getMoney();
        chatPetDividendDetail.setNote("分红+" + ArithmeticUtils.keep2DecimalPlace(money));
        chatPetDividendDetail.setCreateTime(record.getCreateTime());

        return chatPetDividendDetail;
    }

}
