package net.monkeystudio.base.utils;

import net.monkeystudio.base.exception.ValidateBizException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateTool {

	/**
     * 验证邮箱格式
     * @param email
     * @return
	 * @throws ValidateBizException 
     */
    public static void checkEmail(String email) throws ValidateBizException {

    	if ( email == null){
    		throw new ValidateBizException("Email为空。");
    	}
    	
        String reg = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if(checkByReg(reg, email)){
        	return;
        }else{
        	throw new ValidateBizException("Email格式错误。");
        }
    }
    
    /**
     * 验证昵称格式
     * @param nickname
     * @return
     * @throws ValidateBizException 
     */
    public static void checkNickname(String nickname) throws ValidateBizException{
    	
    	if ( nickname == null ){
    		throw new ValidateBizException("昵称不能为空。");
    	}
    	
    	String reg = "^([a-z0-9A-Z\\u4e00-\\u9fa5]){2,16}";
    	if(checkByReg(reg, nickname)){
        	return;
        }else{
        	throw new ValidateBizException("用户昵称由2-16个中英文字符或数字组成。");
        }
    }
    
    /**
     * 验证用户名格式
     * @param username
     * @return
     * @throws ValidateBizException 
     */
    public static void checkUsername(String username) throws ValidateBizException{
    	
    	if ( username == null ){
    		throw new ValidateBizException("用户名不能为空。");
    	}
    	
    	String reg = "^([a-z0-9A-Z]){3,16}";
    	if(checkByReg(reg, username)){
        	return;
        }else{
        	throw new ValidateBizException("用户名由3-16个英文字符或数字组成。");
        }
    }
    
    /**
     * 验证密码格式
     * @param password
     * @return
     * @throws ValidateBizException 
     */
    public static void checkPassword(String password) throws ValidateBizException{
    	
    	if ( password == null ){
    		throw new ValidateBizException("密码不能为空。");
    	}
    	
    	String reg = "^([a-z0-9A-Z]){6,16}";
    	if(checkByReg(reg,password)){
    		return;
    	}else{
    		throw new ValidateBizException("用户蜜码由6-16个英文字母或数字组成。");
    	}
    }
    
//    public static void main(String[] args){
//    	System.out.println(checkNickname("a").getRetMsg());
//    	System.out.println(checkNickname("aaaaaa").getRetMsg());
//    	System.out.println(checkNickname("a534342").getRetMsg());
//    	System.out.println(checkNickname("a23232323-").getRetMsg());
//    	System.out.println(checkNickname("a44444444444444444444444444").getRetMsg());
//    	System.out.println(checkNickname("中").getRetMsg());
//    	System.out.println(checkNickname("中中中中中中").getRetMsg());
//    	System.out.println(checkNickname("中中中中中中中中中中中中").getRetMsg());
//    }
//    
//    public static CheckResult success(){
//		CheckResult cr = new CheckResult();
//		cr.setRetCode(CheckResult.SUCCESS);
//		cr.setRetMsg("校验通过。");
//		
//		return cr;
//	}
//	
//	public static CheckResult fail(String retMsg){
//		CheckResult cr = new CheckResult();
//		cr.setRetCode(CheckResult.FAIL);
//		cr.setRetMsg(retMsg);
//		
//		return cr;
//	}
	
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
	
//	/**
//	 * 返回信息结构
//	 * @author hebo
//	 *
//	 */
//	public static class CheckResult {
//		
//		public static final int SUCCESS = 0;
//		public static final int FAIL = -1;
//		
//		private int retCode;
//		private String retMsg;
//		
//		public boolean success(){
//			if ( retCode == SUCCESS ){
//				return true;
//			}else{
//				return false;
//			}
//		}
//		
//		public int getRetCode() {
//			return retCode;
//		}
//		public void setRetCode(int retCode) {
//			this.retCode = retCode;
//		}
//		public String getRetMsg() {
//			return retMsg;
//		}
//		public void setRetMsg(String retMsg) {
//			this.retMsg = retMsg;
//		}
//	}
}
