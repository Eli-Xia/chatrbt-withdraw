package net.monkeystudio.wx.mp.aes;
/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------


import net.monkeystudio.base.utils.Log;
import net.monkeystudio.base.utils.XmlUtil;
import net.monkeystudio.wx.mp.aes.AesException;
import net.monkeystudio.wx.mp.beam.ComponentVerifyTicket;
import net.monkeystudio.wx.mp.beam.Encryp;
import net.monkeystudio.wx.vo.pub.EventEncrypt;
import net.monkeystudio.wx.vo.pub.TextMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * XMLParse class
 *
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 */
public class XMLParse {

	/**
	 * 提取出xml数据包中的加密消息
	 * @param xmlText 待提取的xml字符串
	 * @return 提取出的加密消息字符串
	 * @throws AesException
	 */
	public static EventEncrypt extracttMessageEncrypt(String xmlText) throws AesException {
		EventEncrypt eventEncrypt = new EventEncrypt();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmlText);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("ToUserName");


			String encrypt = nodelist1.item(0).getTextContent();
			String toUserName = nodelist2.item(0).getTextContent();

			eventEncrypt.setEncrypt(encrypt);
			eventEncrypt.setToUserName(toUserName);

			//eventEncrypt = XmlUtil.converyToJavaBean(xmlText, EventEncrypt.class);

			return eventEncrypt;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ParseXmlError);
		}
	}


	public static Encryp extractEncryp(String encrypXmlString){

		Encryp encryp = new Encryp();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(encrypXmlString);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("AppId");

			encryp.setEncrypt(nodelist1.item(0).getTextContent());
			encryp.setAppId(nodelist2.item(0).getTextContent());

		} catch (Exception e) {
			Log.e(e);

		}

		return encryp;
	}

	public static ComponentVerifyTicket extractComponentVerifyTicket(String ticketXmlString){
		ComponentVerifyTicket componentVerifyTicket = new ComponentVerifyTicket();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(ticketXmlString);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("ComponentVerifyTicket");
			NodeList nodelist2 = root.getElementsByTagName("AppId");

			componentVerifyTicket.setTicket(nodelist1.item(0).getTextContent());
			componentVerifyTicket.setAppId(nodelist2.item(0).getTextContent());

		} catch (Exception e) {
			Log.e(e);
			Log.e("error str : " + ticketXmlString);
		}

		return componentVerifyTicket;
	}

	public TextMessage extractTextMessage(String textMessageString){

//		return XmlUtil.converyToJavaBean(textMessageString, TextMessage.class);

		TextMessage textMessage = new TextMessage();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(textMessageString);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();

			String fromUserName = root.getElementsByTagName("FromUserName").item(0).getTextContent();
			String createTime = root.getElementsByTagName("CreateTime").item(0).getTextContent();
			String event = root.getElementsByTagName("Event").item(0).getTextContent();
			String latitude = root.getElementsByTagName("Latitude").item(0).getTextContent();
			String longitude = root.getElementsByTagName("Longitude").item(0).getTextContent();
			String precision = root.getElementsByTagName("Precision").item(0).getTextContent();





		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}


	public static String extractDataType(String dataStr){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(dataStr);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();

			String dataType = root.getElementsByTagName("MsgType").item(0).getTextContent();

			return dataType;
		} catch (Exception e) {
			Log.e("error in " + dataStr);
			Log.e(e);
		}

		return null;
	}



	public static String extractField(String xmlStr, String fieldName){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmlStr);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();

			NodeList nodeList = root.getElementsByTagName(fieldName);

			if(nodeList == null){
				Log.d("there is not the name of ? node in the xml" ,fieldName);
			}

			String value = nodeList.item(0).getTextContent();

			return value;
		} catch (Exception e) {
			Log.e("error in " + xmlStr);
			Log.e(e);
		}

		return null;
	}


	/**
	 * 生成xml消息
	 * @param encrypt 加密后的消息密文
	 * @param signature 安全签名
	 * @param timestamp 时间戳
	 * @param nonce 随机字符串
	 * @return 生成的xml字符串
	 */
	public static String generate(String encrypt, String signature, String timestamp, String nonce) {

		String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
				+ "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
				+ "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
		return String.format(format, encrypt, signature, timestamp, nonce);

	}
}
