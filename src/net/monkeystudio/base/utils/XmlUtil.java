package net.monkeystudio.base.utils;

import net.monkeystudio.wx.mp.beam.Encryp;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.monkeystudio.wx.vo.thirtparty.UnauthorizedResp;

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

    public static void main(String[] args) {

        String xmlStr = "" +
                "<xml>" +
                "  <AppId><![CDATA[wx7dabde16518e57ad]]></AppId> " +
                "  <CreateTime>1510627962</CreateTime> " +
                "  <InfoType><![CDATA[unauthorized]]></InfoType>" +
                "  <AuthorizerAppid><![CDATA[wx880fe0c4ce7c8d35]]></AuthorizerAppid>" +
                "</xml>";


        UnauthorizedResp unauthorizedResp = converyToJavaBean(xmlStr, UnauthorizedResp.class);

        System.out.println(unauthorizedResp.toString());
        System.out.println(convertToXml(unauthorizedResp));


        String xmlStr2 = "<xml>    <AppId><![CDATA[wx7dabde16518e57ad]]></AppId>    <Encrypt><![CDATA[F0gmMTMJReZlDNZGICCTxjXBx7Sfj/e/iTz8cXOow0FfFl0DI7qZOUxoy8meZSAwkn+PYX4zrm4btXrSSLV10vQ9mfxAAYcNKiyZqK+bRigYHHs4U0xBhEfIXONrTB8vOdP/eVWvW5MRJS0uIOgf1XFdnh59Los5f/3ubs/u/BFy45YC8d+e7iWjMlmB81QJ/lPORXcHDCn8Rxe4WFL4JVFvPH7ZOPT+ZowjxBsoyq5zB0ZpKjgHp4jZ8T/WrVdLBwk5EiUWYGvhomfi0N9UlRDn7xCaQjugxHF0yoTWr7+Uj1E5g4QLFZYu7HdLBnCSv9+fllZZflX8bmpM03pWhg==]]></Encrypt></xml>";
        Encryp encryp = converyToJavaBean(xmlStr2, Encryp.class);

        System.out.println(encryp.toString());
        System.out.println(convertToXml(encryp));

    }

}
