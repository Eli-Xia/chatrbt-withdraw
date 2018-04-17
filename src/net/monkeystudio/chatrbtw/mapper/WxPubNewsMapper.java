package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxPubNews;
import net.monkeystudio.chatrbtw.entity.WxPubNewsWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WxPubNewsMapper {
	
    int deleteByPrimaryKey(Integer id);
    
    int deleteByMaterialId(Integer materialId);
    
    int deleteByWxPubOriginId(String wxPubOriginId);

    int insert(WxPubNewsWithBLOBs record);

    int insertSelective(WxPubNewsWithBLOBs record);

    WxPubNewsWithBLOBs selectByPrimaryKey(Integer id);
    
    List<WxPubNews> selectByPage(Map<String,Object> params);
    
    int count(Map<String,Object> params);

    List<WxPubNews> selectByTitle(@Param("wxPubOriginId") String wxPubOriginId, @Param("title") String title, @Param("maxRetCount") Integer maxRetCount);

    //List<WxPubNews> selectByPage(@Param("wxPubOriginId") String wxPubOriginId, @Param("title") String title, @Param("startIndex") Integer startIndex,@Param("pageSize")Integer pageSize);

    List<WxPubNews> selectAllByTitle(@Param("wxPubOriginId") String wxPubOriginId, @Param("title") String title);

    int updateByPrimaryKeySelective(WxPubNewsWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(WxPubNewsWithBLOBs record);

    int updateByPrimaryKey(WxPubNews record);
}