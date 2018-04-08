package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("rbtwUserRoleMapper")
public interface UserRoleMapper {
	
    int insert(UserRole record);

    int insertSelective(UserRole record);
    
    List<UserRole> selectByUserId(Integer userId);
    
    void deleteByUserId(Integer userId);

    int batchInsert (List<UserRole> records);
}