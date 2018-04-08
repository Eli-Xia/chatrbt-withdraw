package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.ChatLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ChatLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChatLog record);

    int insertSelective(ChatLog record);

    ChatLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChatLog record);

    int updateByPrimaryKeyWithBLOBs(ChatLog record);

    int updateByPrimaryKey(ChatLog record);

    List<ChatLog> selectByCreateTime(@Param("startTime")Date startTime , @Param("endTime") Date endTime);

    int countByTime(@Param("startTime") Long startTime ,@Param("endTime") Long endTime ,@Param("wxPubOpneId") String wxPubOpneId,@Param("userOpenId") String userOpenId);
    
    List<ChatLog> selectByPage(Map<String,Object> params);

    List<ChatLog> selectByWxPubOriginId(@Param("startIndex") int startIndex ,@Param("pageSize") int page ,@Param("wxPubOriginId") String wxPubOriginId);
    
    Integer count();

    Integer countByWxPubOriginId(@Param("wxPubOriginId") String wxPubOriginId);


    Long get2DaysAgoTotalChatMan(String originId); //统计指定公众号前天的聊天人数


    Long getYstdTotalChatNum(String originId);//   统计指定公众号昨日聊天次数


    Long get2DaysAgoTotalChatNum(String originId); //统计指定公众号前天的聊天次数


    Long getYstdTotalChatMan(String originId); //  统计指定公众号昨日聊天人数


    //key: chatlogtime , chatman   日期-聊天人数报表

    List<Map<String,Object>> totalChatManChartByDate(@Param("beginDate") Date beginDate,@Param("endDate") Date endDate,@Param("originId") String originId);


    //key: chatlogtime , chatnum   日期-聊天次数报表

    List<Map<String,Object>> totalChatNumChartByDate (@Param("beginDate") Date beginDate,@Param("endDate") Date endDate,@Param("originId") String originId);


    //从所有公众号中统计昨日聊天总人数   key:createtime ,totalchatman,totalchatnum
    List<Map<String,Object>> totalChatManAndNumByDateFromAllWxPub(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate);
}