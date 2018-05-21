package net.monkeystudio.base.utils;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.util.EntityUtils;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @TODO 未完全支持https
 */
public class HttpsHelper {

    private static int SocketTimeout = 3000;//3秒
    private static int ConnectTimeout = 3000;//3秒
    private static Boolean SetTimeOut = true;


    public String post(String data,String url) {
        String receivedData = null;
        try {

            Map<String, String> paramsData = new HashMap<String, String>();
            paramsData.put("data", data);
            receivedData = post(url, paramsData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivedData;
    }

    public static String post(String url, Map<String, String> paramsMap) {
        String result = null;
        PostMethod postMethod = null;
        HttpClient httpClient = new HttpClient();

        httpClient.getParams().setParameter(
                HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        postMethod = new PostMethod(url);

        if (paramsMap != null && paramsMap.size() > 0) {
            NameValuePair[] datas = new NameValuePair[paramsMap.size()];
            int index = 0;
            for (String key : paramsMap.keySet()) {
                datas[index++] = new NameValuePair(key, paramsMap.get(key));
            }
            postMethod.setRequestBody(datas);

        }

        HttpClientParams httparams = new HttpClientParams();
        httparams.setSoTimeout(60000);
        postMethod.setParams(httparams);

        try {
            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                result = postMethod.getResponseBodyAsString();
            }
        } catch (org.apache.commons.httpclient.HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }

        return result;
    }


    public static String postJson(String url  ,Map<String,String> map){

        JSONObject jsonParam = JSONObject.fromObject(map);

        Log.i("postJson:" + url);

        return postJsonByJsonObject(url,jsonParam);
    }


    public static String postJsonByStr(String url  ,String str){

        Log.d("postJsonByStr : " + str);

        JSONObject jsonObj = JSONObject.fromObject(str);;

        return postJsonByJsonObject(url,jsonObj);
    }


    public static String postJsonByJsonObject(String url  ,JSONObject jsonParam){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        method.addHeader("Content-Type","application/json;charset=UTF-8");
        try {
            if (null != jsonParam) {

                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), Charset.forName("UTF-8"));
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            System.out.println(jsonParam.toString());
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity(),"UTF-8");
                    /**把json字符串转换成json对象**/
                    jsonResult = JSONObject.fromObject(str);
                } catch (Exception e) {
                    Log.e(e);
                }
            }
        } catch (IOException e) {
            Log.e(e);
        }
        return jsonResult.toString();
    }
    
    public static String getJson(String url){
    	
    	 String responseBody = "";
         //CloseableHttpClient httpClient=HttpClients.createDefault();
         //支持https
         CloseableHttpClient httpClient = getHttpClient();

         StringBuilder sb = new StringBuilder(url);

         HttpGet httpGet = new HttpGet(sb.toString());
         
         httpGet.addHeader("Content-Type","application/json;charset=UTF-8");
         
         if (SetTimeOut) {
             RequestConfig requestConfig = RequestConfig.custom()
                     .setSocketTimeout(SocketTimeout)
                     .setConnectTimeout(ConnectTimeout).build();//设置请求和传输超时时间
             httpGet.setConfig(requestConfig);
         }
         try {
             System.out.println("Executing request " + httpGet.getRequestLine());
             //请求数据
             CloseableHttpResponse response = httpClient.execute(httpGet);
             System.out.println(response.getStatusLine());
             int status = response.getStatusLine().getStatusCode();
             if (status == HttpStatus.SC_OK) {
                 HttpEntity entity = response.getEntity();
                 // do something useful with the response body
                 // and ensure it is fully consumed
                 responseBody = EntityUtils.toString(entity);
                 //EntityUtils.consume(entity);
             } else {
                 System.out.println("http return status error:" + status);
                 throw new ClientProtocolException("Unexpected response status: " + status);
             }
         } catch (Exception ex) {
             Log.e(ex);
         }finally {
             try {
                 httpClient.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }


         return responseBody;
    }

    public static String get(String url){

        String responseBody = "";
        //CloseableHttpClient httpClient=HttpClients.createDefault();
        //支持https
        CloseableHttpClient httpClient = getHttpClient();

        StringBuilder sb = new StringBuilder(url);

        HttpGet httpGet = new HttpGet(sb.toString());
        
        if (SetTimeOut) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
        }
        try {
            System.out.println("Executing request " + httpGet.getRequestLine());
            //请求数据
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                responseBody = EntityUtils.toString(entity);
                //EntityUtils.consume(entity);
            } else {
                System.out.println("http return status error:" + status);
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } catch (Exception ex) {
            Log.e(ex);
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return responseBody;

    }



    //TODO 暂未使用
    private static CloseableHttpClient getHttpClient() {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);

        //指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

            //信任任何链接
            TrustStrategy anyTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            };

            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();
            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        //设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
//      connManager.setDefaultConnectionConfig(connConfig);
//      connManager.setDefaultSocketConfig(socketConfig);
        //构建客户端
        return HttpClientBuilder.create().setConnectionManager(connManager).build();
    }

    public static void main(String[] args) {

    }
}