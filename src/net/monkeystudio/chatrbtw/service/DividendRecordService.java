package net.monkeystudio.chatrbtw.service;

import com.fasterxml.jackson.databind.util.BeanUtil;
import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.chatrbtw.entity.DividendRecord;
import net.monkeystudio.chatrbtw.mapper.DividendRecordMapper;
import net.monkeystudio.chatrbtw.service.bean.dividendrecord.DividendRecordResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bint on 2018/7/10.
 */
@Service
public class DividendRecordService {

    @Autowired
    private DividendRecordMapper dividendRecordMapper;

    public Integer save(DividendRecord dividendRecord){
        return dividendRecordMapper.insert(dividendRecord);
    }


    public List<DividendRecordResp> getDividendRecordRespList(Integer page , Integer pageSize ){

        Integer startIndex = (page - 1) * pageSize;
        List<DividendRecord> dividendRecordList = dividendRecordMapper.selectByPage(startIndex, pageSize);

        List<DividendRecordResp> dividendRecordRespList = new ArrayList<>();

        for(DividendRecord dividendRecord : dividendRecordList){

            DividendRecordResp dividendRecordResp = BeanUtils.copyBean(dividendRecord,DividendRecordResp.class);

            dividendRecordResp.setUserCount(dividendRecord.getTotalWxfanNumber());

            dividendRecordResp.setTotalMoney(dividendRecord.getMoney());

            dividendRecordRespList.add(dividendRecordResp);
        }

        return dividendRecordRespList;
    }

    /**
     * 统计历史总分红金额
     */
    public Float getHistoryTotalDividendAmount(){
        return dividendRecordMapper.countTotalDividendAmount();
    }
}
