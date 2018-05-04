package net.monkeystudio.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数校验工具
 * @author hebo
 *
 */
public class ValidateUtils {

	/**
	 * 正则匹配
	 * @param reg
	 * @param str
	 * @return
	 */
	public static boolean checkByReg(String reg, String str){
		
		if ( str == null ){
			return false;
		}
		
		try{       	
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();          
        }catch(Exception e){      	
            return false;
        }
	}
	
	/**
     * 验证手机格式（11-14位中国大陆手机号）
     * @param mobile 
     * @return
     */
    public static boolean checkMobile(String mobile){

    	if ( mobile == null){
    		return false;
    	}
    	
    	String reg = "^[0-9]{11,14}$";
    	return checkByReg(reg, mobile);
    }
    
    /**
     * 验证手机格式（11-14位中国大陆手机号）
     * @param mobiles 英文逗号分隔
     * @return
     */
    public static boolean checkMobiles(String mobiles){

    	if ( mobiles == null){
    		return false;
    	}
    	
    	String reg = "^([0-9]{11,14})(,[0-9]{11,14})*$";
    	//String reg = "^(\\d{6,14})(,\\d{6,14})*$";
    	return checkByReg(reg, mobiles);
    }
	
	/**
     * 验证邮箱格式
     * @param email
     * @return
     */
    public static boolean checkEmail(String email){

    	if ( email == null){
    		return false;
    	}
    	
        String reg = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return checkByReg(reg, email);
    }
    
    /**
     * 用户名格式,6-18位大小写字母或数字组成。
     * @param username
     * @return
     */
    public static boolean checkUsername(String username){
    	
    	if ( username == null ){
    		return false;
    	}
    	
    	String reg = "^[a-z0-9A-Z]{6,18}$"; 
        return checkByReg(reg, username);
    }
    
    
    /**
     * 密码格式,6-18位大小写字母、数字或!@#$^&字符组成。
     * @param password
     * @return
     */
    public static boolean checkPassword(String password){
    	
    	if ( password == null ){
    		return false;
    	}
    	
    	String reg = "^[a-z0-9A-Z!@#$^&]{6,18}$"; 
        
    	return checkByReg(reg, password);
    }
   
}
