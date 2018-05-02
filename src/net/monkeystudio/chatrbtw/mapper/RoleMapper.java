package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("rbtwRoleMapper")
public interface RoleMapper {

    int insert(Role record);

    int insertSelective(Role record);

    List<Role> selectByUserName(@Param("username") String username);

    List<Role> selectAll();

    Integer update(@Param("role") Role role,@Param("oriCode") String oriCode);

    Integer delete(String code);

    Role selectByCode(@Param("code") String code);

    List<Role> select();
}