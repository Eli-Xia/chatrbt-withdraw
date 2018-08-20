package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.UserIdempotent;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/8/13.
 */
public interface UserIdempotentMapper {

    int insert(UserIdempotent userIdempotent);

    int updateState(@Param("id") Integer id, @Param("state") Integer state);

}
