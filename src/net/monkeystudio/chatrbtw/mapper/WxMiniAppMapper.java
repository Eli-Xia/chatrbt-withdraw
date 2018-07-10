package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxMiniApp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WxMiniAppMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxMiniApp record);

    int insertSelective(WxMiniApp record);

    WxMiniApp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WxMiniApp record);

    int updateByPrimaryKey(WxMiniApp record);

    WxMiniApp selectByAppId(String appId);

    List<WxMiniApp> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);


}