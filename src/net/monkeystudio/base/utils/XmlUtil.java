package net.monkeystudio.base.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bint on 2017/11/3.
 */
public class XmlUtil {

    private static  XStream xstream = null;
    static {
        xstream = new XStream(new DomDriver());
    }

    /**
     * JavaBean转换成xml
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        xstream.processAnnotations(obj.getClass()); // 识别obj类中的注解

        xstream.alias("xml",obj.getClass());

        return xstream.toXML(obj);
    }

    /**
     * TODO XStream应该是只有一个实例
     * xml转换成JavaBean
     * @param xml
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String xml, Class<T> clazz) {
        XStream xstream2 = new XStream(new DomDriver());

        xstream2.autodetectAnnotations(true);

        xstream2.alias("xml", clazz);

        T object = (T) xstream2.fromXML(xml);

        /*xstream.autodetectAnnotations(true);

        xstream.alias("xml", clazz);

        T object = (T) xstream.fromXML(xml);*/
        return object;
    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }

    /**
     * @description 将xml字符串转换成map
     * @param xml
     * @return Map
     */
    public static Map<String,String> xmlToMap(String xml) {
        Map<String,String> map = new HashMap<String,String>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> list = rootElt.elements();//获取根节点下所有节点
            for (Element element : list) {  //遍历节点
                map.put(element.getName(), element.getText()); //节点的name为map的key，text为map的value
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) throws Exception{

//        Transfers tf = new Transfers();
//        tf.setMchAppid("wxe062425f740c30d8");
//        tf.setMchid("10000098");
//        tf.setNonceStr("3PG2J4ILTKCH16CQ2502SI8ZNMTM67VS");
//        tf.setAmount(100);
//        tf.setPartnerTradeNo("100000982014120919616");
//        tf.setCheckName("FORCE_CHECK");
//        tf.setSign("C97BDBACF37622775366F38B629F45E3");
//        tf.setSpbillCreateIp("10.2.3.10");
//        tf.setDesc("节日快乐");
//        String s = JsonUtil.toJSon(tf);
//        Map<String,String> ret = JsonUtil.jsonToMap(s);
//
//        String xml = XmlUtil.mapToXml(ret);
//        System.out.println(xml);
//        System.out.println(ret);
//        //String s = XmlUtil.convertToXml(tf);
//
//        System.out.println(s);
//
//        System.out.println(2);

//        <xml>
//
//<mch_appid>wxe062425f740c30d8</mch_appid>
//
//<mchid>10000098</mchid>
//
//<nonce_str>3PG2J4ILTKCH16CQ2502SI8ZNMTM67VS</nonce_str>
//
//<partner_trade_no>100000982014120919616</partner_trade_no>
////
////<openid>ohO4Gt7wVPxIT1A9GjFaMYMiZY1s</openid>
////
////<check_name>FORCE_CHECK</check_name>
////
////<re_user_name>张三</re_user_name>
////
////<amount>100</amount>
//
//<desc>节日快乐!</desc>
//
//<spbill_create_ip>10.2.3.10</spbill_create_ip>
//
//<sign>C97BDBACF37622775366F38B629F45E3</sign>
//
//</xml>
//        String xmlStr3 = "<xml>\n" +
//                "    <ToUserName>< ![CDATA[toUser] ]>\n" +
//                "    </ToUserName>\n" +
//                "    <FromUserName>< ![CDATA[FromUser] ]>\n" +
//                "    </FromUserName>\n" +
//                "    <CreateTime>123456789</CreateTime>\n" +
//                "    <MsgType>< ![CDATA[event] ]>\n" +
//                "    </MsgType>\n" +
//                "    <Event>< ![CDATA[subscribe] ]>\n" +
//                "    </Event>\n" +
//                "    <EventKey>< ![CDATA[qrscene_123123] ]>\n" +
//                "    </EventKey>\n" +
//                "    <Ticket>< ![CDATA[TICKET] ]>\n" +
//                "    </Ticket>\n" +
//                "</xml>";
//
//        xmlStr3 = xmlStr3.replace(" ", "");
//        SubscribeEvent subscribeEvent = converyToJavaBean(xmlStr3, SubscribeEvent.class);
//        Log.d(subscribeEvent.toString());toString

    }

}
