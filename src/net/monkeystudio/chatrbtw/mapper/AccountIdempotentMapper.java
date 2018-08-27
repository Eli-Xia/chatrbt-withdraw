package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.AccountIdempotent;
import org.apache.ibatis.annotations.Param;

/**
 * Created by bint on 2018/8/13.
 */
public interface AccountIdempotentMapper {

    int insert(AccountIdempotent accountIdempotent);

    int updateState(@Param("id") Integer id, @Param("state") Integer state);

}
