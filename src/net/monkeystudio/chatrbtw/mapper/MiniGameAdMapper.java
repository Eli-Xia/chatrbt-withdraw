package net.monkeystudio.chatrbtw.mapper;

import net.monkeystudio.chatrbtw.entity.BannerAd;
import net.monkeystudio.chatrbtw.entity.MiniGameAd;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/9/4.
 */
public interface MiniGameAdMapper {

    MiniGameAd selectById(Integer id);

    int insert(MiniGameAd bannerAd);

    int update(MiniGameAd bannerAd);

    List<MiniGameAd> selectAll();


}
