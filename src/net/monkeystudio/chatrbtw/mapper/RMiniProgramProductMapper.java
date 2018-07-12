package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.RMiniProgramProduct;

import java.util.List;

/**
 * Created by bint on 2018/7/12.
 */
public interface RMiniProgramProductMapper {

    List<RMiniProgramProduct> selectByProductId(Integer miniProgramId);

}
