package net.monkeystudio.chatrbtw.mapper;

import org.apache.ibatis.annotations.Param;

import net.monkeystudio.chatrbtw.entity.WxPubMaterial;

public interface WxPubMaterialMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(WxPubMaterial record);

    int insertSelective(WxPubMaterial record);

    WxPubMaterial selectByPrimaryKey(Integer id);
    
    WxPubMaterial selectByOriginIdAndMediaId(@Param("wxPubOriginId") String wxPubOriginId, @Param("mediaId") String mediaId);

    int updateByPrimaryKeySelective(WxPubMaterial record);

    int updateByPrimaryKey(WxPubMaterial record);
}