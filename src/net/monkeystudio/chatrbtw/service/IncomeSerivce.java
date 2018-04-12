package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.utils.ArithmeticUtils;
import net.monkeystudio.base.utils.BigDecimalUtils;
import net.monkeystudio.base.utils.DateUtils;
import net.monkeystudio.base.utils.ListUtil;
import net.monkeystudio.chatrbtw.entity.Ad;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.service.bean.income.*;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.wx.service.WxPubService;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 结算统计
 * Created by bint on 08/03/2018.
 */
@Service
public class IncomeSerivce {

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private AdClickLogService adClickLogService;

    @Autowired
    private AdService adService;

    private final static Integer PAGE_SIZE = 7;

    /**
     * 获得用户当天的收益
     * @param userId
     * @param date
     * @return
     */
    public BigDecimal getUserDailyIncome(Integer userId ,Date date){

        BigDecimal totalIncomeBD = new BigDecimal(0);

        Map<String,WxPubDailyIncome> wxPubDailyIncomeMap = this.getDailyUserWxpubDetail(userId, date);

        if(wxPubDailyIncomeMap == null){
            return totalIncomeBD;
        }

        Set<Map.Entry<String,WxPubDailyIncome>> set = wxPubDailyIncomeMap.entrySet();

        for(Map.Entry<String,WxPubDailyIncome> entry : set){

            WxPubDailyIncome wxPubDailyIncome = entry.getValue();
            BigDecimal dailyIncome = wxPubDailyIncome.getDailyIncome();

            totalIncomeBD = totalIncomeBD.add(dailyIncome);
        }

        return totalIncomeBD;
    }

    /**
     * 获取用户当天的收益详细
     * @param userId
     * @param date
     * @return
     */
    public UserDailyIncomeDetail getUserDailyIncomeDetail(Integer userId , Date date){

        BigDecimal totalIncomeBD = new BigDecimal(0);

        UserDailyIncomeDetail userDailyIncomeDetail = new UserDailyIncomeDetail();

        Map<String,WxPubDailyIncome> wxPubDailyIncomeMap = this.getDailyUserWxpubDetail(userId, date);

        if(wxPubDailyIncomeMap != null){
            Set<Map.Entry<String,WxPubDailyIncome>> set = wxPubDailyIncomeMap.entrySet();

            List<WxPubDailyIncomeOverview> wxPubDailyIncomeOverviewList = new ArrayList<>();

            for(Map.Entry<String,WxPubDailyIncome> entry : set){

                WxPubDailyIncome wxPubDailyIncome = entry.getValue();
                WxPub wxPub = wxPubDailyIncome.getWxPub();
                BigDecimal dailyIncome = wxPubDailyIncome.getDailyIncome();

                WxPubDailyIncomeOverview wxPubDailyIncomeOverview = new WxPubDailyIncomeOverview();
                wxPubDailyIncomeOverview.setWxDailyIncome(dailyIncome.floatValue());
                wxPubDailyIncomeOverview.setWxPubName(wxPub.getNickname());
                wxPubDailyIncomeOverview.setWxPubHeadImgUrl(wxPub.getHeadImgUrl());

                wxPubDailyIncomeOverviewList.add(wxPubDailyIncomeOverview);

                //计算当天的总收益
                totalIncomeBD = totalIncomeBD.add(dailyIncome);
            }
            userDailyIncomeDetail.setWxPubDailyIncomeOverviewList(wxPubDailyIncomeOverviewList);
        }

        userDailyIncomeDetail.setDate(date);
        userDailyIncomeDetail.setDailySumIncome(totalIncomeBD.floatValue());

        return userDailyIncomeDetail;
    }

    /**
     * 获取用户日收益详细
     * @param userId
     * @param date
     * @return
     */
    public Map<String,WxPubDailyIncome> getDailyUserWxpubDetail(Integer userId , Date date){

        //获取用户所拥有的微信公众号
        List<WxPub> wxPubList = wxPubService.getWxPubsByUserId(userId);

        //如果没有公众号则没有收益
        if(wxPubList == null || wxPubList.size() == 0){
            return null;
        }

        Map<String, WxPubDailyIncome> map = new HashMap<>();

        for(WxPub wxPub : wxPubList){

            WxPubDailyIncome wxPubDailyIncome = new WxPubDailyIncome();

            wxPubDailyIncome.setDate(date);

            String wxPubOriginId = wxPub.getOriginId();
            wxPubDailyIncome.setWxPub(wxPub);


            BigDecimal dailyIncome = this.getWxPubDailyIncome(wxPubOriginId, date ,userId);
            wxPubDailyIncome.setDailyIncome(dailyIncome);

            map.put(wxPub.getOriginId(), wxPubDailyIncome);
        }

        return map;
    }

    /**
     * 获取公众昨日的收益
     * @param wxPubOriginId
     * @return
     */
    public BigDecimal getWxPubYesterdayIncome(String wxPubOriginId ,Integer userId){
        Date yesterday = DateUtils.getYesterday(new Date());

        BigDecimal bigDecimal = this.getWxPubDailyIncome(wxPubOriginId, yesterday ,userId);

        return bigDecimal;
    }


    /**
     * 获取用户昨日总收益
     * @param userId
     * @return
     */
    public BigDecimal getUserYesterdayIncome(Integer userId){
        Date yesterday = DateUtils.getYesterday(new Date());

        return this.getUserDailyIncome(userId, yesterday);

    }

    /**
     * 获得公众号当天的收益
     * @param wxPubOriginId
     * @param date
     * @return
     */
    private BigDecimal getWxPubDailyIncome(String wxPubOriginId ,Date date ,Integer userId){

        BigDecimal dailyIncome = new BigDecimal(0);
        List<Integer> adIdList = adClickLogService.getAdIdList(wxPubOriginId,date);

        for(Integer adId : adIdList){

            Ad ad = adService.getAdById(adId);

            if(ad == null){
                continue;
            }

            //获得点击的单价
            Float adClickIncome = ad.getIncome();
            BigDecimal adClickIncomeBD = BigDecimalUtils.getInstance(adClickIncome);

            Integer adClickCount = adClickLogService.countDailyAdClick(wxPubOriginId,date,adId ,userId);

            BigDecimal dayTotal = ArithmeticUtils.multiply(adClickIncomeBD, new BigDecimal(adClickCount));

            dailyIncome = dailyIncome.add(dayTotal);
        }

        return dailyIncome;
    }

    /**
     * 获得某个公众号下的指定广告的历史总收益
     * @param adId
     * @param wxPubOriginId
     * @return
     */
    private BigDecimal getAdTotalIncome(Integer adId , String wxPubOriginId ,Integer userId) throws BizException {
        Ad ad = adService.getAdById(adId);

        if(ad == null){
            throw new BizException("无法找到该广告");
        }

        //获得点击的单价
        Float adClickIncome = ad.getIncome();
        BigDecimal adClickIncomeBD = BigDecimalUtils.getInstance(adClickIncome);

        Integer adClickCount = adClickLogService.countDailyAdClick(wxPubOriginId,adId ,userId);

        BigDecimal total = ArithmeticUtils.multiply(adClickIncomeBD, new BigDecimal(adClickCount));

        return total;
    }



    /**
     * 得到用户的历史总收益
     * @param userId
     * @return
     */
    public BigDecimal getHistoryTotalIncome(Integer userId) throws BizException {

        BigDecimal totalIncomeBD = new BigDecimal(0);

        //获取用户所拥有的微信公众号
        List<WxPub> wxPubList = wxPubService.getWxPubsByUserId(userId);

        //如果没有公众号则没有收益
        if(wxPubList == null || wxPubList.size() == 0){
            return totalIncomeBD;
        }

        for(WxPub wxPub : wxPubList){
            String wxPubOriginId = wxPub.getOriginId();

            BigDecimal wxPubHistoryTotalIncome = this.getWxPubHistoryTotalIncome(wxPubOriginId,userId);
            totalIncomeBD = totalIncomeBD.add(wxPubHistoryTotalIncome);
        }

        return totalIncomeBD;
    }

    public BigDecimal getHistoryTotalIncomeDetail2(Integer userId){
        List<Integer> adList = adClickLogService.getAdListByUserId(userId);

        BigDecimal total = new BigDecimal(0);
        for(Integer adId : adList){
            BigDecimal adIncome = this.getAdUserIncomeByUserId(userId, adId);


            total = total.add(adIncome);
        }

        return total;
    }


    /**
     * 获取指定广告下的用户收益
     * @param userId
     * @return
     */
    public BigDecimal getAdUserIncomeByUserId(Integer userId ,Integer adId){

        Integer count = adClickLogService.countTotalUserAd(userId, adId);

        Ad ad = adService.getAdById(adId);

        BigDecimal income = new BigDecimal(0);


        BigDecimal countBD = new BigDecimal(count);
        BigDecimal clickIncome = new BigDecimal(ad.getIncome());

        income = ArithmeticUtils.multiply(countBD, clickIncome);

        return income;
    }


    /**
     * 获得用户的历史总收益
     * @param userId
     * @return
     */
    public UserTotalIncomeDetail getHistoryTotalIncomeDetail (Integer userId) throws BizException {
        BigDecimal totalIncomeBD = new BigDecimal(0);

        UserTotalIncomeDetail userTotalIncomeDetail = new UserTotalIncomeDetail();

        //获取用户所拥有的微信公众号
        List<WxPub> wxPubList = wxPubService.getWxPubsByUserId(userId);

        //如果没有公众号则没有收益
        if(wxPubList == null || wxPubList.size() == 0){
            return null;
        }

        List<WxPubTotalIncomeOverview> wxPubTotalIncomeOverviewList = new ArrayList<>();

        for(WxPub wxPub : wxPubList){
            String wxPubOriginId = wxPub.getOriginId();

            List<WxPubAdIncomeOverview> wxPubAdIncomeOverviewList = this.getWxPubTotalDetailIncome(wxPubOriginId,userId);

            WxPubTotalIncomeOverview WxPubTotalIncomeOverview = new WxPubTotalIncomeOverview();
            WxPubTotalIncomeOverview.setWxPubAdIncomeOverviewList(wxPubAdIncomeOverviewList);

            String wxPubNickmame = wxPub.getNickname();
            WxPubTotalIncomeOverview.setWxPubNickName(wxPubNickmame);

            wxPubTotalIncomeOverviewList.add(WxPubTotalIncomeOverview);

            for(WxPubAdIncomeOverview wxPubAdIncomeOverview : wxPubAdIncomeOverviewList){
                Double income = wxPubAdIncomeOverview.getIncome();

                BigDecimal incomeBD = BigDecimalUtils.getInstance(income);

                totalIncomeBD = totalIncomeBD.add(incomeBD);
            }
        }

        userTotalIncomeDetail.setUserTotal(totalIncomeBD.doubleValue());

        userTotalIncomeDetail.setWxPubTotalIncomeOverviewList(wxPubTotalIncomeOverviewList);
        return userTotalIncomeDetail;
    }

    /**
     * 获取公众号的昨日和历史总收益
     * @param wxPubOriginId
     * @return
     */
    public WxPubIncomeCountInfoResp wxPubIncomeCount(String wxPubOriginId ,Integer userId) throws BizException {

        WxPubIncomeCountInfoResp wxPubIncomeCountInfoResp = new WxPubIncomeCountInfoResp();

        BigDecimal historyTotalIncome = this.getWxPubHistoryTotalIncome(wxPubOriginId ,userId);
        wxPubIncomeCountInfoResp.setHistoryTotalIncome(historyTotalIncome.doubleValue());

        BigDecimal yesterdayIncome = this.getWxPubYesterdayIncome(wxPubOriginId ,userId);
        wxPubIncomeCountInfoResp.setYesterdayIncome(yesterdayIncome.floatValue());

        return wxPubIncomeCountInfoResp;

    }

    /**
     * 获取公众号历史总收益
     * @param wxPubOriginId
     * @return
     */
    public BigDecimal getWxPubHistoryTotalIncome(String wxPubOriginId ,Integer userId) throws BizException {

        BigDecimal historyTotalIncome = new BigDecimal(0);

        List<Integer> adIdList = adClickLogService.getAdIdList(wxPubOriginId);

        for(Integer adId : adIdList){
            BigDecimal adTatal = this.getAdTotalIncome(adId, wxPubOriginId ,userId);

            historyTotalIncome = historyTotalIncome.add(adTatal);
        }

        return historyTotalIncome;
    }


    /**
     * 获取公众号下面的所有的广告总收益
     * @param wxPubOriginId
     * @return
     * @throws BizException
     */
    public List<WxPubAdIncomeOverview> getWxPubTotalDetailIncome(String wxPubOriginId , Integer userId) throws BizException {

        List<WxPubAdIncomeOverview> wxPubAdIncomeOverviewList = new ArrayList<>();

        List<Integer> adIdList = adClickLogService.getAdIdList(wxPubOriginId);

        for(Integer adId : adIdList){
            WxPubAdIncomeOverview wxPubAdIncomeOverview = new WxPubAdIncomeOverview();
            BigDecimal adTatal = this.getAdTotalIncome(adId, wxPubOriginId ,userId);

            Ad ad = adService.getAdById(adId);
            wxPubAdIncomeOverview.setAdId(adId);
            wxPubAdIncomeOverview.setAlias(ad.getAlias());

            wxPubAdIncomeOverview.setIncome(adTatal.doubleValue());
            wxPubAdIncomeOverviewList.add(wxPubAdIncomeOverview);
        }

        return wxPubAdIncomeOverviewList;
    }


    /**
     * 获得公众号的每日收益列表
     * @param wxPubOriginId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<WxPubDailyIncomeItem> getDailyListWxPubIncome(String wxPubOriginId , Date startDate , Date endDate ,Integer page ,Integer userId) throws BizException{

        if(!startDate.before(endDate)){
            throw  new BizException("param error!");
        }

        Long startTimestamp = startDate.getTime();
        Long endTiemstamp = endDate.getTime();

        List<WxPubDailyIncomeItem> wxPubDailyIncomeItemList = null;

        //如果大于七天,则只最多给七天
        if(endTiemstamp.longValue() - startTimestamp.longValue() > PAGE_SIZE * DateUtils.DAY_MILLISECOND){

            Long startTimestampShow = startTimestamp + PAGE_SIZE * (page-1) * DateUtils.DAY_MILLISECOND;

            if(startTimestampShow.longValue() > endTiemstamp.longValue()){
                throw new BizException("param error!");
            }

            Long endTimestampShow  = startTimestampShow + PAGE_SIZE * DateUtils.DAY_MILLISECOND;

            if(endTimestampShow.longValue() > endTiemstamp.longValue()){
                endTimestampShow = endTiemstamp;
            }

            wxPubDailyIncomeItemList = this.getWxPubDailyIncomeList(wxPubOriginId,new Date(startTimestampShow),new Date(endTimestampShow) ,userId);
        }else {
            wxPubDailyIncomeItemList = this.getWxPubDailyIncomeList(wxPubOriginId,startDate,endDate ,userId);
        }

        if(ListUtil.isNotEmpty(wxPubDailyIncomeItemList)){
            Collections.reverse(wxPubDailyIncomeItemList);
        }

        return wxPubDailyIncomeItemList;
    }


    /**
     * 获取公众号期间的收益
     * @param wxPubOriginId
     * @param startDate
     * @param endDate
     * @return
     */
    private List<WxPubDailyIncomeItem> getWxPubDailyIncomeList(String wxPubOriginId , Date startDate, Date endDate ,Integer userId){

        List<WxPubDailyIncomeItem> wxPubDailyIncomeItemList = new ArrayList<>();

        WxPub wxPub = wxPubService.getByOrginId(wxPubOriginId);
        if(wxPub == null){
            return null;
        }

        Long pointer = startDate.getTime();
        while(pointer.longValue() <= endDate.getTime()){

            WxPubDailyIncomeItem wxPubDailyIncomeItem = new WxPubDailyIncomeItem();


            Date date = new Date(pointer);
            wxPubDailyIncomeItem.setDate(date);

            BigDecimal income = this.getWxPubDailyIncome(wxPubOriginId, date , userId);
            wxPubDailyIncomeItem.setIncome(income.floatValue());

            wxPubDailyIncomeItemList.add(wxPubDailyIncomeItem);

            pointer = pointer + DateUtils.DAY_MILLISECOND;
        }

        return wxPubDailyIncomeItemList;
    }

    /**
     * 获取用户日收益列表
     * @param userId
     * @param startDate
     * @param endDate
     * @param page
     * @return
     * @throws BizException
     */
    public UserDailyIncome getUserDailyIncome(Integer userId ,Date startDate ,Date endDate,Integer page) throws BizException{
        if(!startDate.before(endDate)){
            throw  new BizException("param error!");
        }

        Long startTimestamp = startDate.getTime();
        Long endTiemstamp = endDate.getTime();

        List<UserDaliyInocmeOverview> userDaliyInocmeOverviewList = null;

        BigDecimal yesterdayIncome = this.getUserYesterdayIncome(userId);

        //如果大于七天,则只最多给七天
        if(endTiemstamp.longValue() - startTimestamp.longValue() > PAGE_SIZE * DateUtils.DAY_MILLISECOND){

            Long startTimestampShow = startTimestamp + PAGE_SIZE * (page-1) * DateUtils.DAY_MILLISECOND;

            if(startTimestampShow.longValue() > endTiemstamp.longValue()){
                throw new BizException("param error!");
            }

            Long endTimestampShow  = startTimestampShow + PAGE_SIZE * DateUtils.DAY_MILLISECOND;

            if(endTimestampShow.longValue() > endTiemstamp.longValue()){
                endTimestampShow = endTiemstamp;
            }

            userDaliyInocmeOverviewList = this.getUserDaliyInocmeOverviewList(userId,new Date(startTimestampShow),new Date(endTimestampShow));
        }else {
            userDaliyInocmeOverviewList = this.getUserDaliyInocmeOverviewList(userId,startDate,endDate);
        }

        UserDailyIncome userDailyIncome = new UserDailyIncome();
        userDailyIncome.setYesterdayIncome(yesterdayIncome.floatValue());
        userDailyIncome.setUserDaliyInocmeOverviewList(userDaliyInocmeOverviewList);

        return userDailyIncome;
    }


    /**
     * 获取用户时间区间内的日收益
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    private List<UserDaliyInocmeOverview> getUserDaliyInocmeOverviewList(Integer userId,Date startDate ,Date endDate){
        Long pointer = startDate.getTime();

        List<WxPub> wxPubList = wxPubService.getWxPubsByUserId(userId);

        if(wxPubList == null || wxPubList.size() == 0){
            return null;
        }

        List<UserDaliyInocmeOverview> userDaliyInocmeOverviewList = new ArrayList<>();

        while(pointer.longValue() <= endDate.getTime()){

            UserDaliyInocmeOverview userDaliyInocmeOverview = new UserDaliyInocmeOverview();

            Date date = new Date(pointer);
            userDaliyInocmeOverview.setDate(date);

            UserDailyIncomeDetail dailyIncomeDetail = this.getUserDailyIncomeDetail(userId, date);
            userDaliyInocmeOverview.setIncome(dailyIncomeDetail.getDailySumIncome());

            userDaliyInocmeOverviewList.add(userDaliyInocmeOverview);

            pointer = pointer + DateUtils.DAY_MILLISECOND;
        }

        Collections.reverse(userDaliyInocmeOverviewList);

        return userDaliyInocmeOverviewList;
    }

}
