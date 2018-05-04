package net.monkeystudio.chatrbtw.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import net.monkeystudio.base.local.Msg;
import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.utils.CommonUtils;
import net.monkeystudio.base.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Autowired
	private CfgService cfgService;

	//用于缓存发送结果信息（该数据内存不清理，需重启服务释放）
	private static Map<String,Integer> sendResultMap = new HashMap<String,Integer>(); //0未成完，1成功，2失败
	public final static int RESULT_UNKNOW = 0;
	public final static int RESULT_SUCCESS = 1;
	public final static int RESULT_FAIL = 2;
	
	/**
	 * 获取email发送结果
	 * @param tag
	 * @return
	 */
	public Integer getSendResult(String tag){
		return sendResultMap.get(tag);
	}
	
	/**asynchronous transmission 
	 * @param request
	 * @param destEmail 接收的邮箱地址
	 * @param content 邮件正文
	 * @param subject 邮件主题
	 * @return
	 * @throws Exception
	 */
	public String asyncEmail(String username, String destEmail,String subject, String content) throws Exception {

		Log.d("SendEmail, username=[" + username + "],destEmail:[" + destEmail + "],subject=[" + subject + "],content=[" + content + "].");
		
		String tag = destEmail + CommonUtils.getRandom(3);
		sendResultMap.put(tag, EmailService.RESULT_UNKNOW);
		threadPoolTaskExecutor.execute(new EmailValiationTask(username, destEmail, subject, content, tag));
		return tag;
	}
	
	
	private class EmailValiationTask implements Runnable{
		
		private String destEmail;
		private String content;
		private String subject;
		private String name;
		private String tag;
	    	
    	public EmailValiationTask(String name, String destEmail,String subject, String content, String tag) {
    		this.content = content;
    		this.destEmail = destEmail;
    		this.subject = subject;
    		this.name = name;
    		this.tag = tag;
    	}
    	
		@Override
		public void run() {
			
	        mailSender.setHost(cfgService.get("EMAIL_HOST"));
	        mailSender.setPassword(cfgService.get("EMAIL_PASSWORD"));
	        mailSender.setUsername(cfgService.get("EMAIL_ADDRESS"));
	        mailSender.setPort((cfgService.getInteger("EMAIL_PORT")));
	        mailSender.setProtocol(cfgService.get("EMAIL_POTOCOL"));
	        mailSender.setDefaultEncoding("utf-8");
	        MimeMessage msg = mailSender.createMimeMessage();
	        
	        try {
	        	MimeMessageHelper helper = new MimeMessageHelper(msg, true);
	        	helper.setFrom(mailSender.getUsername());
				helper.setTo(destEmail);
		        helper.setText(content,true);
		        helper.setSubject(subject);
		        
		        mailSender.send(msg);
		        EmailService.sendResultMap.put(tag, EmailService.RESULT_SUCCESS);
			} catch (MessagingException e) {
				e.printStackTrace();
				Log.e(Msg.text("email.send.fail"));
				sendResultMap.put(tag, EmailService.RESULT_FAIL);
			} catch ( MailException e ){
				e.printStackTrace();
				Log.e(Msg.text("email.send.fail"));
				EmailService.sendResultMap.put(tag, EmailService.RESULT_FAIL);
			}

		}
	    	
    }
	
}
