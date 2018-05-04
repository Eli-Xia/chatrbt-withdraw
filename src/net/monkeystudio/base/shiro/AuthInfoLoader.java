package net.monkeystudio.base.shiro;

import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.chatrbtw.entity.Res;
import net.monkeystudio.chatrbtw.mapper.ResMapper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujinhua on 2017/4/19.
 */
public class AuthInfoLoader {
	
    @Autowired
    ResMapper resMapper ;

    public String loadAuthInfo(){
    	
        List<Res> list = resMapper.selectAll();
        
        List<Res> resList = new ArrayList<Res>();
        if ( list != null ){
        	for ( Res res : list ){
        		if ( res.getRes().indexOf("*") < 0){
        			resList.add(res);
        		}
        	}
        	
        	for ( Res res : list ){
        		if ( res.getRes().indexOf("*") >= 0){
        			resList.add(res);
        		}
        	}
        }
        
        StringBuilder sb = new StringBuilder();
        for(Res res : resList){
            sb.append(res.getRes()).append("=authc");
            List<String> roles = CommonUtils.splitString2List(res.getAllowRoles(), ",");
            if(roles.size() > 0){

                sb.append(",roles[");
                for(int i = 0 ; i < roles.size() ; i ++){
                    sb.append("\""+roles.get(i)+"\"");
                    if(i != roles.size() -1){
                        sb.append(",");
                    }
                }
                sb.append("]");
            }
            sb.append("\r\n");
        }
        return  sb.toString();
    }
    
}
