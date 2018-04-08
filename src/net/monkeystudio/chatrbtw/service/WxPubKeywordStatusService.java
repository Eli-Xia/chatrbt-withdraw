package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxPubKeywordStatus;
import net.monkeystudio.chatrbtw.mapper.WxPubKeywordStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WxPubKeywordStatusService {
    @Autowired
    private WxPubKeywordStatusMapper wxPubKeywordStatusMapper;
    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    //公众号关键字回复控制关闭状态
    public static final Integer WXPUB_KEYWORD_STATUS_OFF = 0;

    //公众号关键字回复控制开启状态
    public static final Integer WXPUB_KEYWORD_STATUS_ON = 1;

   /* @PostConstruct
    public void init() {
        Log.i("=========>>>> init WxPubKeywordStatus !!");
        initWxPubKeywordStatus();

    }*/

    /**服务器启动加载数据
     *
     * 组装e_wx_pub_keyword_status表的数据,status值默认为1,即关键字回复开关为开启状态
     * 并将数据放入cache中
     */
    /*public void initWxPubKeywordStatus(){
        List<Map<String, String>> originIdsFromWxPub = wxPubKeywordStatusMapper.getOriginIdListFromWxPub();
        List<Map<String, String>> originIdsFromWxPubKwStatus = wxPubKeywordStatusMapper.getOriginIdListFromWxPubKwStatus();
        List<String> originIds = new ArrayList<String>();
        for(Map<String,String> param:originIdsFromWxPub){
            originIds.add(param.get("originid"));
        }
        //新建表才执行
        if(CollectionUtils.isEmpty(originIdsFromWxPubKwStatus)){
            //批量插入db
            List<WxPubKeywordStatus> list = new ArrayList<>();
            for(String originId: originIds){
               WxPubKeywordStatus wxPubKeywordStatus = WxPubKeywordStatus.builder().switchStatus(1).originId(originId).build();
                list.add(wxPubKeywordStatus);
            }
            int count = wxPubKeywordStatusMapper.batchInsert(list);
            if(count <= 0 ){
                Log.e("========>>> 初始化数据批量插入失败!!!");
            }
            //批量插入cache
            Pipeline pipeline = redisCacheTemplate.getPipelined().getPipeline();
            for(String originId: originIds){
                pipeline.hset(getStatusCacheKey(),originId,"1");//默认status值为1
            }
            pipeline.sync();
        }
    }*/

    //调用后门接口,向e_wx_pub_keyword_status表中批量插入数据
    public synchronized  void synDataFromWxPub(){
        List<Map<String, String>> originIdsFromWxPub = wxPubKeywordStatusMapper.getOriginIdListFromWxPub();
        List<Map<String, String>> originIdsFromWxPubKwStatus = wxPubKeywordStatusMapper.getOriginIdListFromWxPubKwStatus();
        if(originIdsFromWxPub.size()==originIdsFromWxPubKwStatus.size()){//数据已经全部插入
            return ;
        }
        List<String> fromList = new ArrayList<String>();
        List<String> toList  = new ArrayList<String>();
        for(Map<String,String> param:originIdsFromWxPub){
            fromList.add(param.get("originid"));
        }
        for (Map<String,String> param:originIdsFromWxPubKwStatus){//如果是空表不会进入循环 toList集合是空集
            toList.add(param.get("originid"));
        }
        fromList.removeAll(toList);//此时fromList中的originid就是还未批量插入的
        List<WxPubKeywordStatus> statuses = new ArrayList<>();
        for(String originId:fromList){
            WxPubKeywordStatus wxPubKeywordStatus = new WxPubKeywordStatus();
            wxPubKeywordStatus.setSwitchStatus(1);
            wxPubKeywordStatus.setOriginId(originId);
            statuses.add(wxPubKeywordStatus);
        }
        //批量插入
        int count = wxPubKeywordStatusMapper.batchInsert(statuses);
        if(count <= 0 ){
            Log.e("========>>> 初始化数据批量插入失败  <<<=========");
        }

    }

    //获取缓存key  hash
    public String getStatusCacheKey() {
        return RedisTypeConstants.KEY_HASH_TYPE_PREFIX + "WxPubKeywordStatus";
    }

    /*public void putDataIntoCache() {
        //hset   key:WxPubkeywordStatus   field:originId  value:status
        Long count = redisCacheTemplate.hLenByKey(this.getStatusCacheKey());
        //只加载一次
        if(count > 0 ){
            return ;
        }
        //select from db
        List<WxPubKeywordStatus> list = wxPubKeywordStatusMapper.selectAll();
        for (WxPubKeywordStatus obj : list) {
            redisCacheTemplate.hset(this.getStatusCacheKey(), obj.getOriginId(), String.valueOf(obj.getSwitchStatus()));
        }
    }*/

    //根据originId查找status
    public Integer selectStatusByOriginIdFromDb(String originId) {
        return wxPubKeywordStatusMapper.getKeywordSwitchByOriginId(originId);
    }

    public Integer selectStatusByOriginIdFromCache(String originId) {
        String status = redisCacheTemplate.hget(getStatusCacheKey(), originId);
        if (status != null ) {//hget key field value,当field不存在时返回nil
            return Integer.parseInt(status);
        }
        return null;
    }


    //   从cache中找-->从db中找并放入cache
    public Integer getStatusByOriginId(String originId) {
        Integer status = this.selectStatusByOriginIdFromCache(originId);
        if (status == null) {
            status = this.selectStatusByOriginIdFromDb(originId);
            redisCacheTemplate.hset(getStatusCacheKey(), originId, String.valueOf(status));
        }
        return status;
    }

    public Integer insert(WxPubKeywordStatus wxPubKeywordStatus) {
        Integer count = wxPubKeywordStatusMapper.insert(wxPubKeywordStatus);
        redisCacheTemplate.hset(getStatusCacheKey(), wxPubKeywordStatus.getOriginId(), String.valueOf(wxPubKeywordStatus.getSwitchStatus()));
        return count;
    }

    public Integer update(WxPubKeywordStatus wxPubKeywordStatus) {
        Integer count = wxPubKeywordStatusMapper.updateByPrimaryKey(wxPubKeywordStatus);
        if (count <= 0) {
            Log.e("更新失败");
        }
        redisCacheTemplate.hset(getStatusCacheKey(), wxPubKeywordStatus.getOriginId(), String.valueOf(wxPubKeywordStatus.getSwitchStatus()));
        return count;
    }

    //根据originId来更新status
    public void updateByOriginId(String originId, Integer status) {
        Integer count = wxPubKeywordStatusMapper.updateByOriginId(originId, status);
        if (count <= 0) {
            Log.e("根据wxOriginId更新公众号关键字回复开关状态失败!!");
        }
        redisCacheTemplate.hset(getStatusCacheKey(), originId, String.valueOf(status));
    }


}








