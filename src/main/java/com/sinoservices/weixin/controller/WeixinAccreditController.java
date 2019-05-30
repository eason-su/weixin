package com.sinoservices.weixin.controller;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.sinoservices.weixin.service.WeixinServer;
import com.sinoservices.weixin.util.Constants;
import com.sinoservices.weixin.util.XMLConverUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 微信 第三方开放平台  controller层
 *
 * @author Eason.Su
 * @Title: WeixinAccreditController
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2814:20
 */

@Controller
@RequestMapping(value = "/weixinAccreditController")
public class WeixinAccreditController {


    private static final Logger LOG = LoggerFactory.getLogger(SignatureController.class);


    // 返回成功状态
    private final static String SUCCESS = "success";

    String componentVerifyTicket;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private WeixinServer weixinServer;

    // 1、 授权事件接受url 每隔10分钟 获取微信服务器推送ticket 接收后需要解密 接收到后 必须返回字符串success
    @RequestMapping("/openwx/getticket")
    @ResponseBody
    public String getTicket(HttpServletRequest request, HttpServletResponse response)
            throws IOException, DocumentException, AesException, ParserConfigurationException, SAXException {
        processAuthorizeEvent(request);
        return SUCCESS;
    }



    @RequestMapping("/openwx/getComponentAccessToken")
    @ResponseBody
    public String getComponentAccessToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException, DocumentException, AesException, ParserConfigurationException, SAXException {

        return  weixinServer.getComponentAccessToken();
    }

    @RequestMapping("/openwx/getPreAuthCode")
    @ResponseBody
    public String getPreAuthCode(HttpServletRequest request, HttpServletResponse response)
            throws IOException, DocumentException, AesException, ParserConfigurationException, SAXException {

        return weixinServer.getPreAuthCode();
    }



    /**
     * 授权事件处理
     *
     * @param request
     * @throws IOException
     * @throws DocumentException
     */
    private void processAuthorizeEvent(HttpServletRequest request) throws IOException, DocumentException, AesException, SAXException, ParserConfigurationException {
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String signature = request.getParameter("signature");
        String msgSignature = request.getParameter("msg_signature");
        String echostr = request.getParameter("echostr");

        LOG.info("授权事件参数：" + signature + "，" + timestamp + "," + nonce + "," + echostr);
        HttpSession session = request.getSession();
        if (!StringUtils.isNotBlank(msgSignature)) { //判断消息是否空
            return;// 微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息
        }
        boolean isValid = checkSignature(Constants.COMPONENT_TOKEN, signature, timestamp, nonce);
        if (isValid) {
            StringBuilder sb = new StringBuilder();
            BufferedReader in = request.getReader();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            String oldXml = sb.toString();
            LOG.info("授权事件-原始-Xml:" + oldXml);

            String newXml = decryptMsg(msgSignature, timestamp, nonce, oldXml);

            LOG.info("授权事件-解密-Xml:" + newXml);

            Map<String, String> componentVerifyTicketMap = XMLConverUtil.convertToMap(newXml);
            componentVerifyTicket = componentVerifyTicketMap.get("ComponentVerifyTicket");
            session.setAttribute("componentVerifyTicketMap", componentVerifyTicketMap);
            session.setAttribute("componentVerifyTicket", componentVerifyTicket);

            LOG.info("授权事件-ComponentVerifyTicket:" + componentVerifyTicket);

            //redisTemplate.opsForValue().set("componentVerifyTicket",componentVerifyTicket);
            weixinServer.setComponentVerifyTicket(componentVerifyTicket);
            weixinServer.getComponentAccessToken();
            //processAuthorizationEvent(xml);
        }
    }

    /**
     * 保存Ticket
     *
     * @param xml
     */
    void processAuthorizationEvent(String xml) {
        Document doc;

        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            String ticket = rootElt.elementText("ComponentVerifyTicket");
            if (ticket != null) {
//                Wxticket wxticket = new Wxticket();
//                wxticket.setAppid(APPID);
//                wxticket.setAddtime(new Date());;
//                wxticket.setId(1l);
//                wxticket.setTicket(ticket);
//                wxticketService.updateNotNull(wxticket);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断消息是否加密
     *
     * @param token
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        System.out.println(
                "###token:" + token + ";signature:" + signature + ";timestamp:" + timestamp + "nonce:" + nonce);
        boolean flag = false;
        if (signature != null && !signature.equals("") && timestamp != null && !timestamp.equals("") && nonce != null
                && !nonce.equals("")) {
            String sha1 = "";
            String[] ss = new String[]{token, timestamp, nonce};
            Arrays.sort(ss);
            for (String s : ss) {
                sha1 += s;
            }
            sha1 = AddSHA1.SHA1(sha1);
            if (sha1.equals(signature)) {
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 解密消息
     *
     * @param msgSignature
     * @param timestamp
     * @param nonce
     * @param encryptXml
     * @return 解密消息内容
     * @throws AesException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private String decryptMsg(String msgSignature, String timestamp, String nonce, String encryptXml) throws AesException, IOException, SAXException, ParserConfigurationException {

        WXBizMsgCrypt pc = new WXBizMsgCrypt(Constants.COMPONENT_TOKEN, Constants.COMPONENT_ENCODINGAESKEY, Constants.COMPONENT_APPID);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(encryptXml);
        InputSource is = new InputSource(sr);
        org.w3c.dom.Document document = db.parse(is);

        org.w3c.dom.Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("Encrypt");
        NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

        String encrypt = nodelist1.item(0).getTextContent();
        //String msgSignature = nodelist2.item(0).getTextContent();

        String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
        String fromXML = String.format(format, encrypt);

        // 第三方收到公众号平台发送的消息
        String decryptXml = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);

        return decryptXml;
    }


}

class AddSHA1 {
    public static String SHA1(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1"); // 选择SHA-1，也可以选择MD5
            byte[] digest = md.digest(inStr.getBytes()); // 返回的是byet[]，要转化为String存储比较方便
            outStr = bytetoString(digest);
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }

    public static String bytetoString(byte[] digest) {
        String str = "";
        String tempStr = "";
        for (int i = 0; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1) {
                str = str + "0" + tempStr;
            } else {
                str = str + tempStr;
            }
        }
        return str.toLowerCase();
    }
}
