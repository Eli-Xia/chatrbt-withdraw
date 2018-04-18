package net.monkeystudio.wx.service;

import net.monkeystudio.base.redis.RedisCacheTemplate;
import net.monkeystudio.base.redis.constants.RedisTypeConstants;
import net.monkeystudio.base.utils.HttpsHelper;
import net.monkeystudio.base.utils.JsonUtil;
import net.monkeystudio.base.utils.Log;
import net.monkeystudio.chatrbtw.entity.WxPub;
import net.monkeystudio.chatrbtw.entity.WxPubTag;
import net.monkeystudio.chatrbtw.mapper.WxPubMapper;
import net.monkeystudio.chatrbtw.mapper.WxPubTagMapper;
import net.monkeystudio.chatrbtw.service.ChatLogService;
import net.monkeystudio.chatrbtw.service.IncomeSerivce;
import net.monkeystudio.chatrbtw.service.bean.wxpub.WxPubCountBaseInfo;
import net.monkeystudio.chatrbtw.service.bean.wxpub.WxPubResp;
import net.monkeystudio.entity.User;
import net.monkeystudio.exception.BizException;
import net.monkeystudio.service.UserService;
import net.monkeystudio.utils.CommonUtils;
import net.monkeystudio.wx.utils.WxApiUrlUtil;
import net.monkeystudio.wx.vo.pub.autoreply.CurrentAutoReplyInfo;
import net.monkeystudio.wx.vo.pub.autoreply.KeywordAutoreplyInfo;
import net.monkeystudio.wx.vo.pub.autoreply.KeywordAutoreplyInfoListItem;
import net.monkeystudio.wx.vo.pub.autoreply.KeywordListInfoItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bint on 2017/11/2.
 */
@Service
public class WxPubService {

	@Autowired
	private WxAuthApiService wxAuthApiService;

	@Autowired
	private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private WxPubMapper wxPubMapper;

    @Autowired
	private ChatLogService chatLogService;
    
    @Autowired
    private UserService userService;

    @Autowired
	private WxPubTagMapper wxPubTagMapper;

	@Autowired
	private IncomeSerivce incomeSerivce;

	@Autowired
	private WxPubAuthorizerRefreshTokenService wxPubAuthorizerRefreshTokenService;

    private final static Integer WX_PUB_INFO_CAHCE_SURVIVAL_PERIOD = 60 * 30;
    private final static Integer WX_PUB_AOTO_REPLY_INFO_PERIOD = 60 * 30;

    //微信认证
    public final static Integer WX_PUB_VERIFY_TYPE_WX_VERIFY = 0;
    //未微信认证
    public final static Integer WX_PUB_VERIFY_TYPE_UN_VERIFY = -1;


	/**
	 * 是否拥有该公众号
	 * @param wxPubOriginId
	 * @param userId
	 * @return
	 */
    public Boolean hasPub(String wxPubOriginId ,Integer userId){
        WxPub wxPub = wxPubMapper.selectByOrginId(wxPubOriginId);
        if(wxPub != null){

            Integer wxPubUserId = wxPub.getUserId();

            if(wxPubUserId == null){
                return false;
            }
            if(wxPubUserId.intValue()== userId.intValue()){
                return true;
            }
            return false;
        }

        return false;
    }


    /**
	 * 通过原始Id获取公众号信息
	 * @param orginId
	 * @return
	 */
	public WxPub getByOrginId(String orginId){
		WxPub wxPub = this.getFromCacheByOrginId(orginId);

		if(wxPub != null){
			return wxPub;
		}

		wxPub = this.getFromDbByOrginId(orginId);

		String cacheKey = this.getWxPubInfoCacheKeyByOriginId(orginId);
		redisCacheTemplate.setObject(cacheKey,wxPub);
		redisCacheTemplate.expire(cacheKey,WX_PUB_INFO_CAHCE_SURVIVAL_PERIOD);

		return wxPub;
	}

    /**
     * 通过原始账号获得对应的公众号AppId
     * @param originId
     * @return
     */
	public String getWxPubAppIdByOrginId(String originId){
        WxPub wxPub = this.getByOrginId(originId);

        if(wxPub == null){
            return null;
        }

        String wxPubOpenId = wxPub.getAppId();
        return wxPubOpenId;
    }


    public String getWxPubOriginIdByAppId(String wxPubAppId){
		WxPub wxPub = this.getWxPubByAppId(wxPubAppId);

		if(wxPub != null){
			return wxPub.getAppId();
		}

		return null;
	}

	/**
	 * 通过appId获取微信公众号数据
	 * @param wxPubAppId
	 * @return
	 */
    public WxPub getWxPubByAppId(String wxPubAppId){
		WxPub wxPub = this.getFromCacheByAppId(wxPubAppId);

		if(wxPub != null){
			return wxPub;
		}

		wxPub = this.getFromDbByAppId(wxPubAppId);

		String key = this.getCacheKeyByAppId(wxPubAppId);
		redisCacheTemplate.setObject(key,wxPub);
		redisCacheTemplate.expire(key,WX_PUB_INFO_CAHCE_SURVIVAL_PERIOD);

		return wxPub;
	}

	private WxPub getFromCacheByAppId(String wxPubAppId){
		String key = getCacheKeyByAppId(wxPubAppId);

		WxPub wxPub = redisCacheTemplate.getObject(key);

		return wxPub;
	}

	private WxPub getFromDbByAppId(String wxPubAppId){
    	return wxPubMapper.selectByAppId(wxPubAppId);
	}

    private WxPub getFromDbByOrginId(String orginId){
		return wxPubMapper.selectByOrginId(orginId);
	}

	private WxPub getFromCacheByOrginId(String orginId){
		String key = this.getWxPubInfoCacheKeyByOriginId(orginId);

		WxPub wxPub = (WxPub)redisCacheTemplate.getObject(key);

		return wxPub;
	}

    public Integer save(WxPub wxPub){
        return wxPubMapper.insert(wxPub);
    }

	/**
	 * 更新微信公众号信息
	 * @param wxPub
	 * @return
	 */
	public Integer update(WxPub wxPub){
		Integer result = wxPubMapper.updateByOriginId(wxPub);

		this.updateWxPubCache(wxPub.getOriginId());

		return result;

    }
    
    /**
	 * 获取公众号列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<WxPub> getWxPubs(Integer page, Integer pageSize){

		Integer startIndex = CommonUtils.page2startIndex(page, pageSize);
		
		return wxPubMapper.selectByPage(startIndex, pageSize);
	}

	/**
	 * 获取公众号列表 + 公众号对应的标签
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<WxPubResp> getWxPubDtos(Integer page, Integer pageSize){
		Integer startIndex = CommonUtils.page2startIndex(page, pageSize);
		List<WxPub> wxPubs = wxPubMapper.selectByPage(startIndex, pageSize);

		List<WxPubResp> dtos = new ArrayList<>();
		for(WxPub wxPub : wxPubs){
			WxPubResp wxPubResp = new WxPubResp();
			BeanUtils.copyProperties(wxPub,wxPubResp);
			List<WxPubTag> wxPubTags = wxPubTagMapper.selectTagsByWxPubId(wxPub.getId());
			wxPubResp.setTags(wxPubTags);

			Boolean isEanble = wxPubAuthorizerRefreshTokenService.checkRefreshToken(wxPub.getAppId());

			if(isEanble){
				wxPubResp.setStatus(wxPubAuthorizerRefreshTokenService.AUTHORIZED_STATUS);
			}else {
				wxPubResp.setStatus(wxPubAuthorizerRefreshTokenService.UN_AUTHORIZED_STATUS);
			}

			User user = null;
			try {
				user = userService.getUser(wxPub.getUserId());
			} catch (BizException e) {
				Log.e(e.getBizExceptionMsg());
			}

			if(user != null){
				wxPubResp.setUserNickName(user.getNickname());
			}

			dtos.add(wxPubResp);
		}

		return dtos;
	}


	/**
	 * 获取指定用户的公众号列表
	 * @param userId
	 * @param wxPubNickName
	 * @return
	 */
	public List<WxPub> getWxPubs(Integer userId ,String wxPubNickName){

		List<WxPub> wxPubList = wxPubMapper.selectByUserIdAndWxPubNickname(userId ,wxPubNickName);

		List<WxPub> result = new ArrayList<>();

		for(WxPub wxPub : wxPubList){

			Boolean isEnable = wxPubAuthorizerRefreshTokenService.checkRefreshToken(wxPub.getAppId());

			if(isEnable){
				result.add(wxPub);
			}
		}

		return result;
	}

	/**
	 * 更新微信公众号信息的缓存
	 * @param wxPubOriginId
	 */
	private void updateWxPubCache(String wxPubOriginId){

		WxPub wxPub = this.getFromDbByOrginId(wxPubOriginId);

		//更新wxPubOriginId的缓存
		String wxPubOriginIdKey = this.getWxPubInfoCacheKeyByOriginId(wxPubOriginId);

		redisCacheTemplate.setObject(wxPubOriginIdKey,wxPub);
		redisCacheTemplate.expire(wxPubOriginIdKey,WX_PUB_INFO_CAHCE_SURVIVAL_PERIOD);

		//更新wxPubAppId的缓存
		String wxPubAppIdKey = this.getCacheKeyByAppId(wxPub.getAppId());

		redisCacheTemplate.setObject(wxPubAppIdKey,wxPub);
		redisCacheTemplate.expire(wxPubAppIdKey,WX_PUB_INFO_CAHCE_SURVIVAL_PERIOD);

	}

	/**
	 * 获取指定用户的公众号列表
	 * @param userId
	 * @return
	 */
	public List<WxPub> getWxPubsByUserId(Integer userId){
		return wxPubMapper.selectByUserId(userId );
	}

    /**
     * 判断是否为公众号的关键字
     * @param content
     * @param wxPubOpenId
     * @return
     */
	public Boolean isAuotReplyKeyword(String content ,String wxPubOpenId) throws BizException {

	    if(content == null){
	        return null;
        }

        CurrentAutoReplyInfo currentAutoReplyInfo = this.getCurrentAutoReplyInfo(wxPubOpenId);

        if(currentAutoReplyInfo == null){
            return null;
        }

        KeywordAutoreplyInfo keywordAutoreplyInfo = currentAutoReplyInfo.getKeywordAutoreplyInfo();

        if(keywordAutoreplyInfo == null){
        	return false;
		}

        List<KeywordAutoreplyInfoListItem> keywordAutoreplyInfoList = keywordAutoreplyInfo.getList();

        for(KeywordAutoreplyInfoListItem keywordAutoreplyInfoListItem : keywordAutoreplyInfoList){

            List<KeywordListInfoItem> keywordListInfoList = keywordAutoreplyInfoListItem.getKeywordListInfo();
            for(KeywordListInfoItem keywordListInfoItem : keywordListInfoList){

                String keywordContent = keywordListInfoItem.getContent();

                if(content.equals(keywordContent)){
                    return true;
                }
            }
        }

        return false;
    }


    private CurrentAutoReplyInfo getCurrentAutoReplyInfo(String wxPubOpenId) throws BizException {

        String key = this.getWxPubAutoReplyInfoKey(wxPubOpenId);
        CurrentAutoReplyInfo currentAutoReplyInfo = redisCacheTemplate.getObject(key);

        if(currentAutoReplyInfo != null){
            return currentAutoReplyInfo;
        }

        currentAutoReplyInfo = this.fetchCurrentAutoReplyInfo(wxPubOpenId);

        redisCacheTemplate.setObject(key,currentAutoReplyInfo);
        redisCacheTemplate.expire(key,WX_PUB_AOTO_REPLY_INFO_PERIOD);

        return currentAutoReplyInfo;

    }

	/**
	 * 获取公众号的自动回复信息
	 * @param wxPubOpenId
	 * @return
	 */
	public CurrentAutoReplyInfo fetchCurrentAutoReplyInfo(String wxPubOpenId) throws BizException {

		String wxPubAccessToken = wxAuthApiService.getAuthorizerAccessToken(wxPubOpenId);

		String url = WxApiUrlUtil.getFetchAutoreplyIntoUrl(wxPubAccessToken);

		String response = HttpsHelper.get(url);

		if(response == null){
			return null;
		}

		CurrentAutoReplyInfo CurrentAutoReplyInfo = JsonUtil.readValue(response, CurrentAutoReplyInfo.class);

		return CurrentAutoReplyInfo;
	}

	
	/**
	 * 获取所有公众号数
	 * @return
	 */
	public Integer getCount(){
		
		return wxPubMapper.count();
	}

	public WxPubCountBaseInfo getWxPubCountBaseInfo(String originId ,Integer userId){

        WxPubCountBaseInfo wxPubCountBaseInfo = new WxPubCountBaseInfo();

        WxPub wxPub = this.getByOrginId(originId);

        wxPubCountBaseInfo.setWxPubNickname(wxPub.getNickname());

        wxPubCountBaseInfo.setWxPubHeadImgUrl(wxPub.getHeadImgUrl());

        wxPubCountBaseInfo.setYesterdayChatMan(chatLogService.getTotalChatMan(originId));

        wxPubCountBaseInfo.setYesterdayChatNum(chatLogService.getTotalChatNum(originId));


		BigDecimal yesterdayIncome =  incomeSerivce.getWxPubYesterdayIncome(originId,userId);
		wxPubCountBaseInfo.setYesterdayIncome(yesterdayIncome.floatValue());

        return wxPubCountBaseInfo;
    }



	private String getWxPubInfoCacheKeyByOriginId(String orginId){
		return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "WxPubInfo:" + orginId;
	}

	private String getWxPubAutoReplyInfoKey(String wxPubOpenId){
        return RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "WxPubAutoReplyInfo" + wxPubOpenId;
    }

	/**
	 * 设置公众号管理员
	 * @param wxPubId
	 * @param admUserId
	 * @throws BizException 
	 */
	public void setPubAdm(Integer wxPubId, Integer admUserId) throws BizException{
		
		WxPub wxPub = wxPubMapper.selectByPrimaryKey(wxPubId);
		if ( wxPub == null ){
			throw new BizException("WxPub not found, wxPubId=" + wxPubId);
		}
		
		User user = userService.getUser(admUserId);
		if ( user == null ){
			throw new BizException("User not found, userId=" + admUserId);
		}
		
		wxPub.setUserId(admUserId);
		wxPubMapper.updateByPrimaryKey(wxPub);
		
	}

	private String getCacheKeyByAppId(String appId){
		String key = RedisTypeConstants.KEY_STRING_TYPE_PREFIX + "WxPub:AppId:" + appId;

		return key;
	}

	public List<WxPub> getWxPubsByTagId(Integer tagId){
		return wxPubMapper.selectByTagId(tagId);
	}

	public WxPub getWxPubById(Integer id){
		return this.wxPubMapper.selectByPrimaryKey(id);
	}

}
