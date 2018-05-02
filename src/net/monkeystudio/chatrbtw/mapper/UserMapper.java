package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户表接口
 * @author hebo
 *
 */
public interface UserMapper {
	
    int insert(User user);

    int insertSelective(User user);
    
    public User selectByUsername(String username);
    
    public User selectByActiveCode(String activeCode);
    
    /**
     * 主键查询
     * @param id
     * @return
     */
    public User select(Integer id);
    
    public User selectByEmail(String email);
    
    public List<User> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize, @Param("nickname") String nickname);

    public List<User> selectByIdList(List<Integer> userIds);
    
    int updateByPrimaryKeySelective(User record);
    
    int updateByPrimaryKeySelectiveWithMapParams(Map<String, Object> params);

    int updateByPrimaryKey(User record); 
    
    Integer count();
    
    int deleteById(Integer id);
}