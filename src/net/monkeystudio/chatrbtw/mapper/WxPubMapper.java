package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.WxPub;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WxPubMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WxPub record);

    WxPub selectByOrginId(String originId);

    int updateByOriginId(WxPub record);

    int insertSelective(WxPub record);

    WxPub selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WxPub record);

    int updateByPrimaryKey(WxPub record);
    
    /**
     * 公页查询公众号列表
     * @param startIndex
     * @param pageSize
     * @return
     */
    List<WxPub> selectByPage(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
    
    /**
     * 公众号总数
     * @return
     */
    Integer count();

    WxPub selectByAppId(String wxPubAppId);

    List<WxPub> selectByUserIdAndWxPubNickname(@Param("userId") Integer userId ,@Param("wxPubNickname") String wxPubNickname);

    List<WxPub> selectByTagId(Integer tagId);

    List<WxPub> selectByUserId(@Param("userId") Integer userId);
}