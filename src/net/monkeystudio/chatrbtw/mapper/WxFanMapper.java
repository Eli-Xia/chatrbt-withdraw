package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxFan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 2017/12/5.
 */
public interface WxFanMapper {
    Integer insert(WxFan wxFan);

    Integer update(WxFan wxFan);

    List<WxFan> select(@Param("wxPubOriginId") String wxPubOriginId , @Param("wxFanOpenId")  String wxFanOpenId);

    WxFan selectById(Integer id);

    List<WxFan> selectListByWxPubOriginId(@Param("wxPubOriginId") String wxPubOriginId);
}
