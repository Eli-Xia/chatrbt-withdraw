package net.monkeystudio.wx.utils;

import net.monkeystudio.base.service.GlobalConfigConstants;
import net.monkeystudio.wx.service.WxTransferKitService;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
@Component
public class FsClientWithCertSSL {

//    @Autowired
//    private WxTransferKitService wxTransferKitService;
//
//    //证书位置
//    private static String CERT;
//
//    //商户id
//    private static String MCH_ID;
//
//    //携带证书的client
//    private static CloseableHttpClient httpClient;
//
//    //编码格式
//    private static String CHARSET = "UTF-8";
//
//    //指定读取证书格式为PKCS12
//    private final static String CEART_MODE = "PKCS12";
//
//    private static RequestConfig config;
//
//
//    @PostConstruct
//    public void init(){
//        CERT = wxTransferKitService.getCertPath();
//        MCH_ID = wxTransferKitService.getMchId();
//        // 设置http请求连接超时时间
//        config = RequestConfig.custom().setConnectTimeout(30000)
//                .setSocketTimeout(60000).build();//timeunit : seconds
//    }
//
//
//
//    /**
//     * 获取带证书的安全client
//     * @return
//     */
//    private CloseableHttpClient createClient() {
//        CloseableHttpClient httpclient = null;
//        try {
//            // 指定读取证书格式为PKCS12
//            KeyStore keyStore = KeyStore.getInstance(CEART_MODE);
//            // 读取本机存放的PKCS12证书文件
//            //FileInputStream instream = new FileInputStream(new File(CERT));
//            InputStream instream = FsClientWithCertSSL.class.getResourceAsStream(CERT);
//            try {
//                // 指定PKCS12的密码(商户ID)
//                keyStore.load(instream, MCH_ID.toCharArray());
//            } catch (CertificateException e) {
//                e.printStackTrace();
//            }finally {
//                instream.close();
//            }
//            // 相信自己的CA和所有自签名的证书
//            //SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();
//            // Trust own CA and all self-signed certs 加上密钥
//            SSLContext sslcontext = SSLContexts
//                    .custom()
//                    .loadKeyMaterial(keyStore,MCH_ID.toCharArray())
//                    .build();
//            // Allow TLSv1 protocol only 指定TLS版本 (IETF Internet Enginnering TaskForce )
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                    sslcontext,
//                    new String[] { "TLSv1" }, // TLSv1 等于 SSLv3
//                    null,
//                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//            // 设置httpclient的SSLSocketFactory
//            httpclient = HttpClients.custom()
//                    .setSSLSocketFactory(sslsf)
//                    .setDefaultRequestConfig(config)
//                    //.setConnectionManager(connManager)
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//        return httpclient;
//    }
//
//    /**
//     * post请求
//     * @param url
//     *        请求地址
//     * @param paramsXml
//     *         请求的参数xml格式
//     * @return
//     */
//    public String doPost(String url, String paramsXml) {
//        return doPost(url, paramsXml, CHARSET);
//    }
//
//    /**
//     * post请求
//     * @param url
//     *        请求地址
//     * @param paramsXml
//     *        请求的参数xml格式
//     * @param charset
//     *        设置编码格式
//     * @return
//     */
//    private String doPost(String url, String paramsXml, String charset) {
//        Args.notNull(url, "请求目标url");
//        try {
//            HttpPost httpPost = new HttpPost(url);
//            httpPost.addHeader("Connection", "keep-alive");
//            httpPost.addHeader("Accept", "*/*");
//            httpPost.addHeader("Content-Type", "text/xml; charset=UTF-8");
//            httpPost.addHeader("Host", "api.mch.weixin.qq.com");
//            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
//            httpPost.addHeader("Cache-Control", "max-age=0");
//            httpPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
//            if (paramsXml != null) {
//                httpPost.setEntity((new StringEntity(paramsXml,
//                        ContentType.TEXT_XML.getMimeType(), CHARSET)));
//            }
//            httpClient = createClient();//创建 client
//            CloseableHttpResponse response = httpClient.execute(httpPost);//异常？
//            int statusCode = response.getStatusLine().getStatusCode();
//            try {
//                if (statusCode != 200) {
//                    httpPost.abort();
//                    throw new RuntimeException("HttpClient,error status code :"
//                            + statusCode);
//                }
//                HttpEntity entity = response.getEntity();
//                String result = null;
//                if (entity != null) {
//                    result = EntityUtils.toString(entity, Charsets.UTF_8);
//                }
//                EntityUtils.consume(entity);
//                return result;
//            } catch (Exception e) {
//                throw new RuntimeException(e.getMessage());
//            }finally {
//                response.close();
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        } finally{
//            // 关闭连接,释放资源
//            if(httpClient != null){
//                try {
//                    httpClient.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e.getMessage());
//                }
//            }
//        }
//    }
}
