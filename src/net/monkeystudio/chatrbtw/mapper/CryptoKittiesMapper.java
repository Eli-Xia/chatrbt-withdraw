package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.CryptoKitties;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/4/21.
 */
public interface CryptoKittiesMapper {
    Integer updateMinItem(@Param("wxPubOriginId") String wxPubOriginId ,@Param("wxFanOpenId") String wxFanOpenId);

    CryptoKitties selectByWxFan(@Param("wxPubOriginId") String wxPubOriginId ,@Param("wxFanOpenId") String wxFanOpenId);
}
