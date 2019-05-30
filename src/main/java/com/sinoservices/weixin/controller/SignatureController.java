package com.sinoservices.weixin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import sun.util.logging.PlatformLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Eason.Su
 * @Title: Signature
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2815:04
 */
@Controller
@RequestMapping(value = "/signatureController")
public class SignatureController {

    private RestTemplate restTemplate;

    private final static String TOKEN = "49ba59abbe56e057";

    private static final Logger LOG = LoggerFactory.getLogger(SignatureController.class);

    @RequestMapping(value = "/signature")
    @ResponseBody
    public String signature(@RequestParam("") String signature, @RequestParam String timestamp,
                            @RequestParam String nonce, @RequestParam String echostr){


        LOG.info("wx-token校验接口："+signature+"，"+timestamp+","+nonce+","+echostr);
        String[] strings = new String[]{TOKEN,timestamp,nonce};
        StringBuilder builder = new StringBuilder();
        Arrays.sort(strings);
        for (int i=0;i<strings.length;i++){
            builder.append(strings[i]);
        }
        String res = sha1(builder.toString());
        LOG.info("加密后："+res);
        if(signature.equalsIgnoreCase(res)){
            LOG.info("成功！");
            return echostr;
        }
        LOG.info("失败！");
        return "";
    }


    @RequestMapping(value = "/authorization")
    @ResponseBody
    public String authorization(Map<String,String> map){
        return "1";
    }


    private String sha1(String str){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(str.getBytes());
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    private String toHex(byte[] bytes){
        String str = "";
        for(byte b : bytes){
            char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            char[] temp = new char[2];
            temp[0] = chars[(b>>>4)&0x0F];
            temp[1] = chars[b&0x0F];

            str += new String(temp);
        }
        return str;
    }
}
