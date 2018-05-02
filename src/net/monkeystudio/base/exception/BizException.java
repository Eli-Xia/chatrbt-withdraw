package net.monkeystudio.base.exception;

/**
 * 业务异常
 * @author hebo2
 *
 */
public class BizException extends ExceptionBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2733128541097418061L;
	
	/**
	 * 业务异常信息
	 */
	private String bizExceptionMsg;
	
	public BizException(String bizExceptionMsg){
		this.bizExceptionMsg = bizExceptionMsg;
	}

	public String getBizExceptionMsg() {
		return bizExceptionMsg;
	}

	public void setBizExceptionMsg(String bizExceptionMsg) {
		this.bizExceptionMsg = bizExceptionMsg;
	}
	
	
}
