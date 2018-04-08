package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.MoneyWithdrawRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MoneyWithdrawRecordMapper {
    Integer count();

    int deleteByPrimaryKey(Integer id);

    int insert(MoneyWithdrawRecord record);

    int insertSelective(MoneyWithdrawRecord record);

    MoneyWithdrawRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MoneyWithdrawRecord record);

    int updateByPrimaryKey(MoneyWithdrawRecord record);

    List<MoneyWithdrawRecord> selectByParamMap(Map<String,Object> map);

    //根据提现状态统计金额
    Float sumByState (@Param("states") List<Integer> states, @Param("userId")Integer userId);

    List<MoneyWithdrawRecord> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

}