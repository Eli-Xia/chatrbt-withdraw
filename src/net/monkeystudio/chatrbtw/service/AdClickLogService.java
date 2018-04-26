package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.entity.AdClickLog;
import net.monkeystudio.chatrbtw.mapper.AdClickLogMapper;
import net.monkeystudio.chatrbtw.mapper.bean.adclicklog.AdClickLogDailyCount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by bint on 08/03/2018.
 */
@Service
public class AdClickLogService {

    @Autowired
    private AdClickLogMapper adClickLogMapper;

    /**
     * 获得指定公众号的id和时间对应的产生的广告id列表
     * @param wxPubOriginId
     * @param date
     * @return
     */
    public List<Integer> getAdIdList(String wxPubOriginId , Date date){

        //如果时间为空,则取得历史总的
        if(date == null){
            return adClickLogMapper.selectAdId(null, null, wxPubOriginId);
        }

        Date startTime = DateUtils.getBeginDate(date);
        Date endTime = DateUtils.getEndDate(date);

        List<Integer> adIdList = adClickLogMapper.selectAdId(startTime, endTime, wxPubOriginId);

        return adIdList;
    }

    /**
     * 获得指定公众号的id产生的广告id列表
     * @param wxPubOriginId
     * @return
     */
    public List<Integer> getAdIdList(String wxPubOriginId){

        List<Integer> adIdList = this.getAdIdList(wxPubOriginId, null);

        return adIdList;
    }


    public Integer countTotalUserAd(Integer userId , Integer adId){

        return adClickLogMapper.countTotalAdClick(userId,adId);

    }


    /**
     * 根据用户id获取统计
     * @param userId
     * @return
     */
    public List<Integer> getAdListByUserId(Integer userId){
        return this.getAdListByUserId(userId, null, null);
    }

    /**
     * 更具用户id获取统计
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Integer> getAdListByUserId(Integer userId,Date startDate ,Date endDate){
        return adClickLogMapper.selectAdIdByUserId(userId, startDate, endDate);
    }


    /**
     * 得到公众号下的指定日期的广告点击数
     * @param wxPubOriginId
     * @param date 指定日期
     * @param adId
     * @return
     */
    public Integer countDailyAdClick(String wxPubOriginId ,Date date ,Integer adId ,Integer userId){

        Date startTime = DateUtils.getBeginDate(date);
        Date endTime = DateUtils.getEndDate(date);

        return adClickLogMapper.countDayAdClick(startTime, endTime, wxPubOriginId, adId ,userId);
    }

    /**
     * 得到指定公众号的指定广告的总点击数
     * @param wxPubOriginId
     * @param adId
     * @return
     */
    public Integer countDailyAdClick(String wxPubOriginId ,Integer adId ,Integer userId){

        return adClickLogMapper.countDayAdClick(null, null, wxPubOriginId, adId ,userId);
    }

    /**
     * 得到广告点击的列表
     * @param page
     * @param pageSize
     * @return
     */
    public List<AdClickLog> getAdClickLog(Integer page ,Integer pageSize){

        Integer startIndex = (page-1) * pageSize;
        List<AdClickLog> adClickLogList = adClickLogMapper.selectByPage(startIndex, pageSize);

        Collections.reverse(adClickLogList);

        return adClickLogList;
    }

    public Integer count(){
        return adClickLogMapper.count();
    }

    private List<AdClickLogDailyCount> countDailyFromDb(Date startDate ,Date endDate ,Integer adId){
        return adClickLogMapper.countDaily(startDate, endDate , adId);
    }

    /**
     * 查看广告总点击
     * @param adId
     * @return
     */
    public Integer getAdTotalClick(Integer adId){
        return adClickLogMapper.countTotalByAdId(adId);
    }

    /**
     * 获取按天统计的广告点击数
     * @param startDate
     * @param endDate
     * @param adId
     * @return
     */
    public List<AdClickLogDailyCount> countDaily(Date startDate ,Date endDate ,Integer adId){
        List<AdClickLogDailyCount> list = this.countDailyFromDb(startDate, endDate, adId);

        List<AdClickLogDailyCount> result = new ArrayList<>();

        Map<String,AdClickLogDailyCount> map = new HashMap();
        for(AdClickLogDailyCount adClickLogDailyCount : list){

            String key = String.valueOf(TimeUtil.getCurrentTimestamp(adClickLogDailyCount.getClickDate()));
            map.put(key, adClickLogDailyCount);
        }

        Long startTime = TimeUtil.getCurrentTimestamp(startDate);
        Long endTime = TimeUtil.getCurrentTimestamp(endDate);

        while (startTime.longValue() <= endTime.longValue()){

            AdClickLogDailyCount adClickLogDailyCount = map.get(String.valueOf(startTime));

            if(adClickLogDailyCount == null){
                adClickLogDailyCount = new AdClickLogDailyCount();

                adClickLogDailyCount.setClickCount(0);

                Date date = TimeUtil.getDate(startTime);
                adClickLogDailyCount.setClickDate(date);
            }

            result.add(adClickLogDailyCount);
            startTime = startTime + TimeUtil.A_DAY_SECONT_COUNT;
        }

        return result;

    }
}
