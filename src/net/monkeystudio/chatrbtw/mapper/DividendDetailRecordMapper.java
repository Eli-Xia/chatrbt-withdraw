package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.DividendDetailRecord;

import java.util.List;

/**
 * Created by bint on 2018/7/10.
 */
public interface DividendDetailRecordMapper {

    int insert(DividendDetailRecord dividendDetailRecord);

    List<DividendDetailRecord> selectDetailRecordByChatPetId(Integer chatPetId);

    Float sumTotalDividendMoneyByChatPetId(Integer chatPetId);

}
