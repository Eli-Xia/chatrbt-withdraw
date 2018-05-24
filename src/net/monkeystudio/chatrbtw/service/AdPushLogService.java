package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.TimeUtil;
import net.monkeystudio.chatrbtw.entity.AdPushLog;
import net.monkeystudio.chatrbtw.mapper.AdPushLogMapper;
import net.monkeystudio.chatrbtw.mapper.bean.adpushLog.AdPushLogDailyCount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

/**
 * Created by bint on 2017/12/1.
 */
@Service
public class AdPushLogService {

    @Autowired
    private AdPushLogMapper adPushLogMapper;

    /**
     * 保存推送记录
     * @param adPushLog
     * @return
     */
    public Integer save(AdPushLog adPushLog){
        return adPushLogMapper.insert(adPushLog);
    }

    public List<AdPushLogDailyCount> countDaily(Date startDate ,Date endDate,Integer adId){

        Long startTime = TimeUtil.getCurrentTimestamp(startDate);
        Long endTime = TimeUtil.getCurrentTimestamp(endDate);

        List<AdPushLogDailyCount> list = this.countDaily(startTime , endTime ,adId);

        Map<String, AdPushLogDailyCount> map = new HashMap<>();

        for(AdPushLogDailyCount adPushLogDailyCount : list){
            Date date = adPushLogDailyCount.getDate();
            String key = String.valueOf(TimeUtil.getCurrentTimestamp(date));

            map.put(key, adPushLogDailyCount);
        }

        Long pointer = startTime;

        List<AdPushLogDailyCount> result = new ArrayList<>();
        while (pointer <= endTime) {

            String pointerStr = String.valueOf(pointer);
            AdPushLogDailyCount adPushLogDailyCount = map.get(pointerStr);

            //没有数据则补0
            if(adPushLogDailyCount == null){
                adPushLogDailyCount = new AdPushLogDailyCount();

                adPushLogDailyCount.setCount(0);

                Date date = TimeUtil.getDate(pointer);
                adPushLogDailyCount.setDate(date);
            }
            else {
                System.out.println();
            }
            result.add(adPushLogDailyCount);

            pointer = pointer + TimeUtil.A_DAY_SECONT_COUNT;
        }

        return result;
    }

    private List<AdPushLogDailyCount> countDaily(Long startTime ,Long endTime,Integer adId){
        return adPushLogMapper.countDaily(startTime,endTime,adId);
    }

    /**
     * 统计指定广告推送给粉丝的次数
     * @param wxPubAppId
     * @param wxFanOpenId
     * @param adId
     * @return
     */
    public Integer countAdPushAmount4WxFan(String wxPubAppId,String wxFanOpenId,Integer adId){
        return this.adPushLogMapper.countAdPushCount4WxFan(wxPubAppId, wxFanOpenId, adId);
    }

}
