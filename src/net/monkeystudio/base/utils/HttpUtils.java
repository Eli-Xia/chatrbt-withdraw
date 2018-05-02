package net.monkeystudio.base.utils;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public class HttpUtils {

	private static final int MAX_CONNETION_PER_HOST = 150;
    private static final int CONNETION_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 30000;
    private static org.apache.commons.httpclient.HttpClient httpClient;
    
    private static boolean isInit = false;
    
    private static void init() {
    	
    	if ( !isInit ){
    		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	        HttpConnectionManagerParams params = connectionManager.getParams();
	        params.setDefaultMaxConnectionsPerHost(MAX_CONNETION_PER_HOST);
	        params.setConnectionTimeout(CONNETION_TIMEOUT);
	        params.setSoTimeout(SOCKET_TIMEOUT);

	        HttpClientParams clientParams = new HttpClientParams();
	        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
	        httpClient = new org.apache.commons.httpclient.HttpClient(clientParams, connectionManager);
	        
	        isInit = true;
    	}
       
    }
    
    public static String postJson(String url, String body) {
    	
    	init();
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Content-Type","application/json;charset=utf-8");
        method.setRequestHeader("Accept-Charset","utf-8");
        
        method.setRequestBody(body);

        try {
        	httpClient.executeMethod(method);
        	return new String(method.getResponseBody(),"UTF-8");
            
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            method.releaseConnection();
        }
        return null;
    }
}
