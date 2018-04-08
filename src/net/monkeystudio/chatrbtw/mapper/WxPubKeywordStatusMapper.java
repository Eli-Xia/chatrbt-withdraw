package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxPubKeywordStatus;
import org.apache.ibatis.annotations.Param;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

public interface WxPubKeywordStatusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxPubKeywordStatus record);

    WxPubKeywordStatus selectByPrimaryKey(Integer id);

    List<WxPubKeywordStatus> selectAll();

    int updateByPrimaryKey(WxPubKeywordStatus record);

    Integer getKeywordSwitchByOriginId( String originId);

    Integer updateByOriginId(@Param("originId") String originId,@Param("status") Integer status);

    //key originid  wx_pub表中的所有originid
    List<Map<String,String>> getOriginIdListFromWxPub();

    //key originid  wx_pub_keyword_status表中的所有originid
    List<Map<String,String>> getOriginIdListFromWxPubKwStatus();

    int batchInsert(List<WxPubKeywordStatus> wxPubKeywordStatuses);
 }