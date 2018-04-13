package net.monkeystudio.base.utils;

import net.monkeystudio.chatrbtw.sdk.wx.bean.SubscribeEvent;
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


        String xmlStr3 = "<xml>\n" +
                "    <ToUserName>< ![CDATA[toUser] ]>\n" +
                "    </ToUserName>\n" +
                "    <FromUserName>< ![CDATA[FromUser] ]>\n" +
                "    </FromUserName>\n" +
                "    <CreateTime>123456789</CreateTime>\n" +
                "    <MsgType>< ![CDATA[event] ]>\n" +
                "    </MsgType>\n" +
                "    <Event>< ![CDATA[subscribe] ]>\n" +
                "    </Event>\n" +
                "    <EventKey>< ![CDATA[qrscene_123123] ]>\n" +
                "    </EventKey>\n" +
                "    <Ticket>< ![CDATA[TICKET] ]>\n" +
                "    </Ticket>\n" +
                "</xml>";

        xmlStr3 = xmlStr3.replace(" ", "");
        SubscribeEvent subscribeEvent = converyToJavaBean(xmlStr3, SubscribeEvent.class);
        Log.d(subscribeEvent.toString());
    }

}
