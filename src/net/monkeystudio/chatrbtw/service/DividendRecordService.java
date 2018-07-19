package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.DividendRecord;
import net.monkeystudio.chatrbtw.mapper.DividendRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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


}
