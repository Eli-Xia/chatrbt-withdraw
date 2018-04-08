package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.PushMessageConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bint on 2017/11/13.
 */
public interface PushMessageConfigMapper {

    PushMessageConfig selectByKey(String key);

    List<PushMessageConfig> selectAll();

    Integer updateItem(@Param("key") String key ,@Param("value") String value);
}
