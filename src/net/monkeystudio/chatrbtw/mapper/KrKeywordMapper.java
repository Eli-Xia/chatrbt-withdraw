package net.monkeystudio.chatrbtw.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import net.monkeystudio.chatrbtw.entity.KrKeyword;

public interface KrKeywordMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(KrKeyword record);

    int insertSelective(KrKeyword record);

    KrKeyword selectByPrimaryKey(Integer id);
    
    List<KrKeyword> selectByPage(Map<String,Object> params);
    		
    //List<KrKeyword> selectByParam(@Param("startIndex") Integer startIndex, @Param("pageSize")Integer pageSize, @Param("wxPubOriginId")String wxPubOriginId  , @Param("keywords") String keywords  );

    Integer count(Map<String,Object> params);
    
    List<KrKeyword> selectAll();
    
    List<KrKeyword> selectByResponseId(@Param("responseId") Integer responseId);

    int updateByPrimaryKeySelective(KrKeyword record);

    int updateByPrimaryKey(KrKeyword record);
    
    Integer countByResponseId(@Param("responseId") Integer responseId);
}