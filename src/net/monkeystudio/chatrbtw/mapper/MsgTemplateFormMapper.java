package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.MsgTemplateForm;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Created by bint on 2018/7/31.
 */
public interface MsgTemplateFormMapper {

    MsgTemplateForm selectByWxFanIdAndCreateDate(@Param("wxFanId") Integer wxFanId ,@Param("createTime") Date createTime ,@Param("state") Integer state);

    int updateState(@Param("id") Integer id ,@Param("usageTime") Date usageTime  , @Param("state") Integer state );

    Integer insert(MsgTemplateForm msgTemplateForm);

}
