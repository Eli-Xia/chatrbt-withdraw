package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.EthnicGroups;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 2018/4/10.
 */
public interface EthnicGroupsMapper {

    EthnicGroups selectByWxPubAndSecondFunder(@Param("wxPubOriginId") String wxPubOriginId , @Param("secondFounderId") Integer secondFounderId);

    List<EthnicGroups> selectFounderByWxPub(@Param("wxPubOriginId") String wxPubOriginId , @Param("codeType") Integer codeType);

    int insert(EthnicGroups ethnicGroupsCode);

    int countByCode(String code);
}
