package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.MsgTemplate;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/8/6.
 */
public interface MsgTemplateMapper {
    MsgTemplate selectByMiniProgramIdAndCode(@Param("miniProgramId")Integer miniProgramId , @Param("code") Integer code );
}
