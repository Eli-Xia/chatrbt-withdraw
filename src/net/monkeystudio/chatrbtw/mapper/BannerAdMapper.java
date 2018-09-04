package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.BannerAd;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/9/4.
 */
public interface BannerAdMapper {
    List<BannerAd> selectByOnlineTime(@Param("onlineTime") Date onlineTime ,@Param("state") Integer state );

    List<BannerAd> selectByPage(@Param("page") Integer page ,@Param("pageSize") Integer pageSize);

    BannerAd selectById(Integer id);

    int insert(BannerAd bannerAd);

    int update(BannerAd bannerAd);

    int increaseClickCount(@Param("id") Integer id ,@Param("change") Integer change);
}
