package net.monkeystudio.chatrbtw.service;

import net.monkeystudio.chatrbtw.entity.RWxPubProduct;
import net.monkeystudio.chatrbtw.mapper.RWxPubProductMapper;
import net.monkeystudio.exception.BizException;
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
     * 指定的公众号对应的产品是否启动
     * @param productId
     * @param wxPubOriginId
     * @return
     */
    public Boolean isEnable(Integer productId , String wxPubOriginId){
        RWxPubProduct rWxPubProduct = rWxPubProductMapper.selectByWxPubAndProduct(wxPubOriginId, productId);

        if(rWxPubProduct == null ){

            if(productId.intValue() == ProductService.SMART_CHAT.intValue()){
                rWxPubProduct = new RWxPubProduct();

                rWxPubProduct.setStatus(ENABLE_STATUS);
                rWxPubProduct.setProductId(productId);
                rWxPubProduct.setWxPubOriginId(wxPubOriginId);

                rWxPubProductMapper.insert(rWxPubProduct);
                return true;
            }

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
