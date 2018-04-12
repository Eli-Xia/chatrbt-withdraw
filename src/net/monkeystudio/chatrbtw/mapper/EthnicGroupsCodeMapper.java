package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.EthnicGroupsCode;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/4/10.
 */
public interface EthnicGroupsCodeMapper {

    EthnicGroupsCode selectByWxPubAndCode(@Param("wxPubOriginId") String wxPubOriginId ,@Param("code") String code);

    int insert(EthnicGroupsCode ethnicGroupsCode);

    int countByCode(String code);
}
