package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.admin.controller.req.AddUpdateWxPubTag;
import net.monkeystudio.base.req.ListPaginationReq;
import net.monkeystudio.chatrbtw.entity.*;
import net.monkeystudio.chatrbtw.mapper.RAdWxPubMapper;
import net.monkeystudio.chatrbtw.mapper.RAdWxPubTagMapper;
import net.monkeystudio.chatrbtw.mapper.RWxPubTagMapper;
import net.monkeystudio.chatrbtw.mapper.WxPubTagMapper;
import net.monkeystudio.wx.service.WxPubService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author xiaxin
 */
@Service
public class WxPubTagService {

    @Autowired
    private WxPubTagMapper wxPubTagMapper;

    @Autowired
    private RWxPubTagMapper rWxPubTagMapper;

    @Autowired
    private WxPubService wxPubService;

    @Autowired
    private RAdWxPubTagMapper rAdWxPubTagMapper;

    @Autowired
    private RAdWxPubMapper rAdWxPubMapper;

    @Autowired
    private AdService adService;

    /**
     * 获取标签分页
     * @param paginationReq
     * @return
     */
    public List<WxPubTag> getTagsByPageList(ListPaginationReq paginationReq){

        Map<String, Object> map = paginationReq.getMap();

        return wxPubTagMapper.selectByPage((Integer) map.get("startIndex"), (Integer) map.get("pageSize"));
    }
    /**
     * 获取所有标签
     * @return
     */
    public List<WxPubTag> getAllTags(){
        return wxPubTagMapper.selectAll();
    }

    /**
     * 根据微信公众号id获取其标签集
     * @param wxPubId
     * @return
     */
    public List<WxPubTag> getTagsByWxPubId(Integer wxPubId){
        return wxPubTagMapper.selectTagsByWxPubId(wxPubId);
    }

    /**
     * 根据wxPubAppId获得标签
     * @param wxPubAppId
     * @return
     */
    public List<WxPubTag> getByWxPubAppId(String wxPubAppId){
        WxPub wxPub = wxPubService.getWxPubByAppId(wxPubAppId);

        Integer wxPubId = wxPub.getId();

        return this.getTagsByWxPubId(wxPubId);
    }

    /**
     * 指定公众号是否有该标签
     * @return
     */
    public Boolean hasTag(String tagName , String wxPubAppId){

        List<WxPubTag> wxPubTagList = this.getByWxPubAppId(wxPubAppId);

        if(wxPubTagList == null || wxPubTagList.size() == 0){
            return false;
        }

        for(WxPubTag wxPubTag : wxPubTagList){

            if(tagName.equals(wxPubTag.getName())){
                return true;
            }
        }

        return false;
    }

    /**
     * 为公众号编辑标签分类
     */
    @Transactional
    public void cudTagsForWxPub(Integer wxPubId,List<Integer> tagIds){
        if(CollectionUtils.isEmpty(tagIds)){
            return;
        }
        //wePubId->WxPub
        WxPub wxPub = wxPubService.getWxPubById(wxPubId);

        List<WxPubTag> wxPubTags = wxPubTagMapper.selectTagsByWxPubId(wxPubId);
        if(wxPubTags.size() > 0){

            rWxPubTagMapper.deleteTagsByWxPubId(wxPubId);
        }
        rWxPubTagMapper.saveTagsForWxPub(wxPubId,tagIds);//公众号编辑标签

        //标签对应广告集 根据广告id排重
        Set<RAdWxPubTag> rs = new HashSet<>();

        for(Integer tagId:tagIds){

            Map<String,Object> param = new HashMap<>();

            param.put("tagId",tagId);

            List<RAdWxPubTag> list = this.rAdWxPubTagMapper.selectByParamMap(param);

            rs.addAll(list);
        }

        if(CollectionUtils.isEmpty(rs)){
            return;
        }

        //广告-公众号关系集
        List<RAdWxPub> list = rAdWxPubMapper.selectAll();

        for(RAdWxPubTag obj:rs){
            Ad ad = adService.getAdById(obj.getAdId());

            Integer pushState = adService.getAdPushState(ad);
            //1,认证公众号  2,广告正在投放
            if(wxPubService.WX_PUB_VERIFY_TYPE_WX_VERIFY == wxPub.getVerifyTypeInfo()
                    &&
                    adService.AD_PUSH_OPEN.equals(ad.getIsOpen())
                    &&
                    adService.AD_PUSH_STATE_PUSHING == pushState){

                RAdWxPub rAdWxPub = new RAdWxPub();

                rAdWxPub.setWxPubId(wxPubId);

                rAdWxPub.setAdId(obj.getAdId());

                //若新修改的广告-公众号已存在,应保留公众号主的操作结果.
                if(!list.contains(rAdWxPub)){

                    rAdWxPub.setState(adService.WX_PUB_AD_PUSH_STATE_OPEN);

                    rAdWxPub.setIsExclude(adService.WX_PUB_AD_PUSH_NOT_EXCLUDE);

                    this.rAdWxPubMapper.insert(rAdWxPub);
                }
            }
        }
    }


    public void delete(Integer id) {
        wxPubTagMapper.deleteByPrimaryKey(id);
    }

    public void save(AddUpdateWxPubTag tag){
        WxPubTag wxPubTag = new WxPubTag();
        BeanUtils.copyProperties(tag,wxPubTag);
        wxPubTagMapper.insert(wxPubTag);
    }

    public void update(Integer id,AddUpdateWxPubTag tag){
        WxPubTag wxPubTag = new WxPubTag();
        BeanUtils.copyProperties(tag,wxPubTag);
        wxPubTag.setId(id);
        wxPubTagMapper.updateByPrimaryKey(wxPubTag);
    }

    public WxPubTag getWxPubTagById(Integer id) {
        return wxPubTagMapper.selectByPrimaryKey(id);
    }

    public Integer getCount(){
        return wxPubTagMapper.count();
    }
}
