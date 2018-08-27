package net.monkeystudio.wx.service;

import net.monkeystudio.base.service.CfgService;
import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.base.utils.MD5Util;
import net.monkeystudio.wx.vo.transfers.Transfer;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * 微信企业付款相关工具类
 */
@Service
public class WxTransferKitService {

    //appid
    private static String MCH_APP_ID;

    //商户id
    private static String MCH_ID;

    //证书路径
    private static String CERT_PATH;

    //签名算法秘钥
    private static String SIGN_KEY;

    //编码格式
    private static String CHARSET = "UTF-8";

    //指定读取证书格式为PKCS12
    private final static String CEART_MODE = "PKCS12";


    //http请求连接超时时间
    private static RequestConfig config;

    @Autowired
    private CfgService cfgService;

    @PostConstruct
    public void init(){
        MCH_APP_ID = cfgService.get(GlobalConfigConstants.MCH_APPID_KEY);
        MCH_ID = cfgService.get(GlobalConfigConstants.MCHID_KEY);
        CERT_PATH = cfgService.get(GlobalConfigConstants.CERT_PATH_KEY);
        SIGN_KEY = cfgService.get(GlobalConfigConstants.SIGN_KEY_KEY);
        config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(60000).build();//timeunit : seconds
    }



    /**
     * @param characterEncoding
     *            编码格式
     * @param transfer
     *            请求参数
     * @return
     */
    public String createSign(String characterEncoding, Transfer transfer) {
        SortedMap<Object, Object> signParams = new TreeMap<Object, Object>();

        String mchAppid = transfer.getMchAppid();
        signParams.put("mch_appid", mchAppid); // 微信分配的公众账号ID（企业号corpid即为此appId）

        String mchid = transfer.getMchid();
        signParams.put("mchid", mchid);// 微信支付分配的商户号

        String nonceStr = transfer.getNonceStr();
        signParams.put("nonce_str", nonceStr); // 随机字符串，不长于32位

        String partnerTradeNo = transfer.getPartnerTradeNo();
        signParams.put("partner_trade_no", partnerTradeNo); // 商户订单号，需保持唯一性

        String openid = transfer.getOpenid();
        signParams.put("openid", openid); // 商户appid下，某用户的openid

        String checkName = transfer.getCheckName();
        signParams.put("check_name", checkName); // NO_CHECK：不校验真实姓名

        Integer amount = transfer.getAmount();
        signParams.put("amount", amount); // 企业付款金额，单位为分

        String desc = transfer.getDesc();
        signParams.put("desc", desc); // 企业付款操作说明信息。必填。

        String spbillCreateIp = transfer.getSpbillCreateIp();
        signParams.put("spbill_create_ip", spbillCreateIp); // 调用接口的机器Ip地址


        StringBuffer sb = new StringBuffer();
        Set es = signParams.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + SIGN_KEY);//注：key为商户平台设置的密钥key,放到globalConfig中即可.
        //String sign = MD5.encode(sb.toString()).toUpperCase();
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }







    /**
     * post请求
     * @param url
     *        请求地址
     * @param paramsXml
     *         请求的参数xml格式
     * @return
     */
    public String doPost(String url, String paramsXml) {
        return doPost(url, paramsXml, CHARSET);
    }

    /**
     * post请求
     * @param url
     *        请求地址
     * @param paramsXml
     *        请求的参数xml格式
     * @param charset
     *        设置编码格式
     * @return
     */
    private String doPost(String url, String paramsXml, String charset) {
        Args.notNull(url, "请求目标url");
        CloseableHttpClient httpClient = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("Accept", "*/*");
            httpPost.addHeader("Content-Type", "text/xml; charset=UTF-8");
            httpPost.addHeader("Host", "api.mch.weixin.qq.com");
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.addHeader("Cache-Control", "max-age=0");
            httpPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            if (paramsXml != null) {
                httpPost.setEntity((new StringEntity(paramsXml,
                        ContentType.TEXT_XML.getMimeType(), CHARSET)));
            }
            httpClient = createClient();//创建 client
            CloseableHttpResponse response = httpClient.execute(httpPost);//异常？
            int statusCode = response.getStatusLine().getStatusCode();
            try {
                if (statusCode != 200) {
                    httpPost.abort();
                    throw new RuntimeException("HttpClient,error status code :"
                            + statusCode);
                }
                HttpEntity entity = response.getEntity();
                String result = null;
                if (entity != null) {
                    result = EntityUtils.toString(entity, Charsets.UTF_8);
                }
                EntityUtils.consume(entity);
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }finally {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally{
            // 关闭连接,释放资源
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    /**
     * 获取带证书的安全client
     * @return
     */
    private CloseableHttpClient createClient() {
        CloseableHttpClient httpclient = null;
        try {
            // 指定读取证书格式为PKCS12
            KeyStore keyStore = KeyStore.getInstance(CEART_MODE);
            // 读取本机存放的PKCS12证书文件
            FileInputStream instream = new FileInputStream(new File(CERT_PATH));
            //InputStream instream = WxTransferKitService.class.getResourceAsStream(CERT_PATH);
            try {
                // 指定PKCS12的密码(商户ID)
                keyStore.load(instream, MCH_ID.toCharArray());
            } catch (CertificateException e) {
                e.printStackTrace();
            }finally {
                instream.close();
            }
            // 相信自己的CA和所有自签名的证书
            //SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();
            // Trust own CA and all self-signed certs 加上密钥
            SSLContext sslcontext = SSLContexts
                    .custom()
                    .loadKeyMaterial(keyStore,MCH_ID.toCharArray())
                    .build();
            // Allow TLSv1 protocol only 指定TLS版本 (IETF Internet Enginnering TaskForce )
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[] { "TLSv1" }, // TLSv1 等于 SSLv3
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            // 设置httpclient的SSLSocketFactory
            httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setDefaultRequestConfig(config)
                    //.setConnectionManager(connManager)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return httpclient;
    }







    public static void main(String []args){
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        System.out.println(path);
    }


    //    public void getCert(String mchid,String url,String data) throws Exception{
//        // 获取证书，发送POST请求；
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        FileInputStream instream = new FileInputStream(new File("C:/cha/e.txt")); // 从配置文件里读取证书的路径信息
//        keyStore.load(instream, mchid.toCharArray());// 证书密码是商户ID
//        instream.close();
//        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchid.toCharArray()).build();
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
//        HttpPost httpost = new HttpPost(url); //
//        httpost.addHeader("Connection", "keep-alive");
//        httpost.addHeader("Accept", "*/*");
//        httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//        httpost.addHeader("Host", "api.mch.weixin.qq.com");
//        httpost.addHeader("X-Requested-With", "XMLHttpRequest");
//        httpost.addHeader("Cache-Control", "max-age=0");
//        httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
//        httpost.setEntity(new StringEntity(data, "UTF-8"));
//        CloseableHttpResponse response = httpclient.execute(httpost);
//        HttpEntity entity = response.getEntity();
//
//        String resultStr = entity.toString();
//
//
//
//        //String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
//        //EntityUtils.consume(entity);
//
//    }

}
