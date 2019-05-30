package com.sinoservices.weixin.util;

import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class Program2 {

	private final static String COMPONENT_APPID = "wxcad1498aa776c5e3"; //第三方平台 APPID
	private final String COMPONENT_APPSECRET = "59ebf23afcafd25dcf5e6ed1ff6cedb0";    //第三方平台 秘钥
	private final static String COMPONENT_ENCODINGAESKEY = "8yVe61E3mUVWVO1izRlZdyv26zrVUSE3zUEBdcX21e1";  //开发者 设置的 key
	private final static String COMPONENT_TOKEN = "123456";   //开发者 设置的 token


	public static void main(String[] args) throws Exception {

		//



		String token = COMPONENT_TOKEN;
		String encodingAesKey = COMPONENT_ENCODINGAESKEY;
		String appId = COMPONENT_APPID;



		String mingwen = "<xml>    \n" +
				"<AppId><![CDATA[wxcad1498aa776c5e3]]></AppId>    <Encrypt><![CDATA[LqDhSrdjkKUbUzjjyG6kZz9Wz4ZVwn+iUXVXm4UwfwUCpWSFZSughD8rsbnioVI2QPHPCDSU5UEU5mQ/wCdHYExynwhbqoPc7berrJrLEuM+zi2JizuczCleku3D69VUfdj8kyVZkiC9qRJrXAsYghDEPV4VrDNrrhEzTVnt+yYWD34uCzbft8OHy5JPkzQuK2LaMh69ZXOa67iCbcgwg5ewavqhechrwy6ywt+GEhDpGWwbiLhLKtebNKMd8AVUBdlmDz0TpXUZV8kJqiTMGTnUIPIxdiQRB9GFw/r0fQG8Bjg5cb40FrhCYsvcvEqLf8DhS077A8heobw7ly8a1l3ANUOt7dAd8Mg2lMWjRRtEuSOxUKew7IjOsCMWDYHUXRWPFRZaZAZZ533X8e0h/1M1jYt/EOdHQqs/K0zj1Y+6D2GZBShRno4ZmxG3HGKAD1S+UwGMz4CHarz1BH98/A==]]></Encrypt></xml>";


		WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setXIncludeAware(false);
		dbf.setExpandEntityReferences(false);

		DocumentBuilder db = dbf.newDocumentBuilder();
		StringReader sr = new StringReader(mingwen);
		InputSource is = new InputSource(sr);
		Document document = db.parse(is);

		Element root = document.getDocumentElement();
		NodeList nodelist1 = root.getElementsByTagName("Encrypt");
		NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

		String encrypt = nodelist1.item(0).getTextContent();
		//String msgSignature = nodelist2.item(0).getTextContent();

		String msgSignature = "8cf5857666c5334d7aa65f5dc1affc88fae53b94";
		String nonce = "2075757647";
		String timestamp = "1559061085";



		String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
		String fromXML = String.format(format, encrypt);

		//
		// 公众平台发送消息给第三方，第三方处理
		//

		// 第三方收到公众号平台发送的消息
		String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
		System.out.println("解密后明文: " + result2);

		//pc.verifyUrl(null, null, null, null);
	}
}
