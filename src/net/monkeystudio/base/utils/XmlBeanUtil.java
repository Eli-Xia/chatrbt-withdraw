package net.monkeystudio.base.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import net.monkeystudio.wx.vo.transfers.TransfersResult;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class XmlBeanUtil {
    /**
     * 转换不带CDDATA的XML
     *
     * @return
     * @
     */
    private static XStream getXStream() {
        // 实例化XStream基本对象
        XStream xstream = new XStream(new DomDriver(StandardCharsets.UTF_8.name(), new NoNameCoder() {
            // 不对特殊字符进行转换，避免出现重命名字段时的“双下划线”
            public String encodeNode(String name) {
                return name;
            }
        }));
        // 忽视XML与JAVABEAN转换时，XML中的字段在JAVABEAN中不存在的部分
        xstream.ignoreUnknownElements();
        return xstream;
    }

    /**
     * 转换带CDDATA的XML
     *
     * @return
     * @
     */
    private static XStream getXStreamWithCData() {
        // 实例化XStream扩展对象
        XStream xstream = new XStream(new XppDriver() {
            // 扩展xstream，使其支持CDATA块
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    // 不对特殊字符进行转换，避免出现重命名字段时的“双下划线”
                    public String encodeNode(String name) {
                        return name;
                    }

                    // 对所有xml节点的转换都增加CDATA标记
                    protected void writeText(QuickWriter writer, String text) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    }
                };
            }
        });
        // 忽视XML与JAVABEAN转换时，XML中的字段在JAVABEAN中不存在的部分
        xstream.ignoreUnknownElements();
        return xstream;
    }

    /**
     * 以压缩的方式输出XML
     *
     * @param obj
     * @return
     */
    public static String toCompressXml(Object obj) {
        XStream xstream = getXStream();
        StringWriter sw = new StringWriter();
        // 识别obj类中的注解
        xstream.processAnnotations(obj.getClass());
        // 设置JavaBean的类别名
        xstream.aliasType("xml", obj.getClass());
        xstream.marshal(obj, new CompactWriter(sw));
        return sw.toString();
    }

    /**
     * 以格式化的方式输出XML
     *
     * @param obj
     * @return
     */
    public static String toXml(Object obj) {
        XStream xstream = getXStream();
        // 识别obj类中的注解
        xstream.processAnnotations(obj.getClass());
        // 设置JavaBean的类别名
        xstream.aliasType("xml", obj.getClass());
        return xstream.toXML(obj);
    }

    /**
     * 转换成JavaBean
     *
     * @param xmlStr
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(String xmlStr, Class<T> cls) {
        XStream xstream = getXStream();
        // 识别cls类中的注解
        xstream.processAnnotations(cls);
        // 设置JavaBean的类别名
        xstream.aliasType("xml", cls);
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }

    /**
     * 以格式化的方式输出XML
     *
     * @param obj
     * @return
     */
    public static String toXmlWithCData(Object obj) {
        XStream xstream = getXStreamWithCData();
        // 识别obj类中的注解
        xstream.processAnnotations(obj.getClass());
        // 设置JavaBean的类别名
        xstream.aliasType("xml", obj.getClass());
        return xstream.toXML(obj);
    }

    /**
     * 转换成JavaBean
     *
     * @param xmlStr
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBeanWithCData(String xmlStr, Class<T> cls) {
        XStream xstream = getXStreamWithCData();
        // 识别cls类中的注解
        xstream.processAnnotations(cls);
        // 设置JavaBean的类别名
        xstream.alias("xml", cls);
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }

    public static void main(String[]args){
//        String xml = "<xml>\n" +
//                "\n" +
//                "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
//                "\n" +
//                "<return_msg><![CDATA[]]></return_msg>\n" +
//                "\n" +
//                "<mch_appid><![CDATA[wxec38b8ff840bd989]]></mch_appid>\n" +
//                "\n" +
//                "<mchid><![CDATA[10013274]]></mchid>\n" +
//                "\n" +
//                "<device_info><![CDATA[]]></device_info>\n" +
//                "\n" +
//                "<nonce_str><![CDATA[lxuDzMnRjpcXzxLx0q]]></nonce_str>\n" +
//                "\n" +
//                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
//                "\n" +
//                "<partner_trade_no><![CDATA[10013574201505191526582441]]></partner_trade_no>\n" +
//                "\n" +
//                "<payment_no><![CDATA[1000018301201505190181489473]]></payment_no>\n" +
//                "\n" +
//                "<payment_time><![CDATA[2015-05-19 15：26：59]]></payment_time>\n" +
//                "\n" +
//                "</xml>";
        String xml = "<xml>\n" +
                "\n" +
                "<return_code><![CDATA[FAIL]]></return_code>\n" +
                "\n" +
                "<return_msg><![CDATA[系统繁忙,请稍后再试.]]></return_msg>\n" +
                "\n" +
                "<result_code><![CDATA[FAIL]]></result_code>\n" +
                "\n" +
                "<err_code><![CDATA[SYSTEMERROR]]></err_code>\n" +
                "\n" +
                "<err_code_des><![CDATA[系统繁忙,请稍后再试.]]></err_code_des>\n" +
                "\n" +
                "</xml>";
        TransfersResult transfersRet = XmlBeanUtil.toBeanWithCData(xml,TransfersResult.class);
        System.out.println(transfersRet);

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
//
//        String s = XmlBeanUtil.toXml(tf);
//        System.out.println(s);

    }
}
