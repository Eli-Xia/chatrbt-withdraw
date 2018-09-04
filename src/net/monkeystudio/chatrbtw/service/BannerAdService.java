package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.BeanUtils;
import net.monkeystudio.chatrbtw.entity.BannerAd;
import net.monkeystudio.chatrbtw.mapper.BannerAdMapper;
import net.monkeystudio.chatrbtw.service.bean.bannerad.AddBannerAd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by bint on 2018/9/4.
 */
@Service
public class BannerAdService {

    @Autowired
    private BannerAdMapper bannerAdMapper;

    public List<BannerAd> getShowBannerAd(){
        return bannerAdMapper.selectByOnlineTime(new Date(), AD_SHELVE_STATE);
    }

    public List<BannerAd> getPage(Integer startIndex ,Integer pageSize){
        return bannerAdMapper.selectByPage(startIndex, pageSize);
    }

    /**
     * 新增
     * @param addBannerAd
     * @return
     */
    public Integer add(AddBannerAd addBannerAd){
        BannerAd bannerAd = BeanUtils.copyBean(addBannerAd, BannerAd.class);

        bannerAd.setCreateTime(new Date());
        bannerAd.setState(AD_SHELVE_STATE);

        return this.save(bannerAd);
    }

    /**
     * 修改
     * @param bannerAd
     * @return
     */
    public Integer update(BannerAd bannerAd){
        return bannerAdMapper.update(bannerAd);
    }

    private Integer save(BannerAd bannerAd){
        return bannerAdMapper.insert(bannerAd);
    }

    /**
     * 广告下架
     * @param bannerAdId
     * @return
     */
    public Integer unshelve(Integer bannerAdId){

        BannerAd bannerAd = this.getById(bannerAdId);

        bannerAd.setState(AD_UNSHELVE_STATE);

        return this.update(bannerAd);
    }

    private BannerAd getById(Integer id){
        return bannerAdMapper.selectById(id);
    }


    public Integer increaseCount(Integer id){
        return bannerAdMapper.increaseClickCount(id ,1);
    }

    private final static Integer AD_SHELVE_STATE = 1;
    private final static Integer AD_UNSHELVE_STATE = 0;
}
