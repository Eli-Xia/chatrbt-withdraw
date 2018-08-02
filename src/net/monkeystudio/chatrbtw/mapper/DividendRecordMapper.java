package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.DividendRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 2018/7/10.
 */
public interface DividendRecordMapper {
    int insert(DividendRecord dividendRecord);
    List<DividendRecord> selectByPage(@Param("startIndex") Integer startIndex , @Param("pageSize") Integer pageSize );
    Float countTotalDividendAmount();
}
