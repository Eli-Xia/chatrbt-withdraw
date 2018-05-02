package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.RWxPubProduct;
import net.monkeystudio.chatrbtw.mapper.RWxPubProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bint on 27/03/2018.
 */
@Service
public class RWxPubProductService {

    public final static Integer ENABLE_STATUS = 0;
    public final static Integer UNENABLE_STATUS = -1;

    @Autowired
    private RWxPubProductMapper rWxPubProductMapper;



    public Integer insert(RWxPubProduct rWxPubProduct) {

        return rWxPubProductMapper.insert(rWxPubProduct);
    }


    /**
     * 开通产品
     * @param wxPubOriginId
     * @param productId 产品id
     */
    public void openProduct(String wxPubOriginId , Integer productId){

        RWxPubProduct smartChatRWxPubProduct = new RWxPubProduct();
        smartChatRWxPubProduct.setWxPubOriginId(wxPubOriginId);
        smartChatRWxPubProduct.setStatus(ENABLE_STATUS);
        smartChatRWxPubProduct.setProductId(productId);

        this.insert(smartChatRWxPubProduct);
    }


    /**
     * 指定的公众号对应的产品是否启动
     * @param productId
     * @param wxPubOriginId
     * @return
     */
    public Boolean isEnable(Integer productId , String wxPubOriginId){
        RWxPubProduct rWxPubProduct = rWxPubProductMapper.selectByWxPubAndProduct(wxPubOriginId, productId);

        if(rWxPubProduct == null ){
            return false;
        }

        Integer status = rWxPubProduct.getStatus();

        if(status.intValue() == UNENABLE_STATUS.intValue()){
            return false;
        }

        if(status.intValue() == ENABLE_STATUS.intValue()){
            return true;
        }
        return null;
    }


    /**
     * 是否禁用指定产品
     * @param productId
     * @param wxPubOriginId
     * @return
     */
    public Boolean isUnable(Integer productId , String wxPubOriginId){
        return !isEnable(productId, wxPubOriginId);
    }
}
