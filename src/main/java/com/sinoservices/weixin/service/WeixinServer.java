package com.sinoservices.weixin.service;

import com.alibaba.fastjson.TypeReference;
import com.sinoservices.weixin.bean.ComponentAccessTokenRequest;
import com.sinoservices.weixin.bean.ComponentAccessTokenResponse;
import com.sinoservices.weixin.bean.PreAuthCodeRequest;
import com.sinoservices.weixin.bean.PreAuthCodeResponse;
import com.sinoservices.weixin.controller.SignatureController;
import com.sinoservices.weixin.util.Constants;
import com.sinoservices.weixin.util.JsonUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eason.Su
 * @Title: WeixinServer
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2915:01
 */
@Component
public class WeixinServer {

    private static final Logger LOG = LoggerFactory.getLogger(SignatureController.class);

    @Value("${wechat.component_access_token.url:https://api.weixin.qq.com/cgi-bin/component/api_component_token}")
    private String  componentAccessTokenUrl;

    @Value("${wechat.pre_auth_code.url:https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=}")
    private String  preAuthCodeUrl;

    @Value("${wechat.authorizer_access_token.url:https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=}")
    private String  authorizerAccessTokenUrl;

    @Value("${wechat.authorizer_refresh_token.url:https:// api.weixin.qq.com /cgi-bin/component/api_authorizer_token?component_access_token=}")
    private String  AuthorizerRefreshTokenUrl;



    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 2、获取第三方平台component_access_token
     * 第三方平台通过自己的component_appid（即在微信开放平台管理中心的第三方平台详情页中的AppID和AppSecret）
     * 和component_appsecret，以及component_verify_ticket（每10分钟推送一次的安全ticket）来获取自己的接口调用凭据（component_access_token）
     */
    public String getComponentAccessToken() {

        ComponentAccessTokenRequest catr = new  ComponentAccessTokenRequest();
        catr.setComponentAppid(Constants.COMPONENT_APPID);
        catr.setComponentAppsecret(Constants.COMPONENT_APPSECRET);
        catr.setComponentVerifyTicket(getComponentVerifyTicket());

        // 访问微信获取 componentAccessToken
        HttpEntity<String> request = new HttpEntity<>(JsonUtil.getJson(catr));
        ResponseEntity<String> entity = restTemplate.postForEntity(componentAccessTokenUrl, request, String.class);
        String body = entity.getBody();

        ComponentAccessTokenResponse catp = JsonUtil.getObject(body, ComponentAccessTokenResponse.class);
        Assert.assertNotNull(catp.getComponentAccessToken(),"ComponentAccessToken 为空，错误信息："+body);

        LOG.info("ComponentAccessToken:"+body);
        String componentAccessToken = catp.getComponentAccessToken();
        setComponentAccessTokenResponse(componentAccessToken);
        return componentAccessToken;
    }

    /**
     *  3、获取预授权码pre_auth_code
     *  第三方平台通过自己的接口调用凭据（component_access_token）来获取用于授权流程准备的预授权码（pre_auth_code）
     * @return
     */
    public String getPreAuthCode() {

        PreAuthCodeRequest pacr = new PreAuthCodeRequest();
        pacr.setComponentAppid(Constants.COMPONENT_APPID);


        // 访问微信获取 componentAccessToken
        HttpEntity<String> request = new HttpEntity<>(JsonUtil.getJson(pacr));
        ResponseEntity<String> entity = restTemplate.postForEntity(preAuthCodeUrl + getComponentAccessToken(), request, String.class);
        String body = entity.getBody();

        PreAuthCodeResponse pacp = JsonUtil.getObject(body, PreAuthCodeResponse.class);
        Assert.assertNotNull(pacp.getPreAuthCode(),"preAuthCode 为空，错误信息："+body);

        LOG.info("ComponentAccessToken:"+body);
        String preAuthCode = pacp.getPreAuthCode();
        setPreAuthCode(preAuthCode);
        return preAuthCode;

    }

    /**
     * 4、使用授权码换取公众号或小程序的接口调用凭据和授权信息
     * 通过授权码和自己的接口调用凭据（component_access_token），
     * 换取公众号或小程序的接口调用凭据（authorizer_access_token
     * 和用于前者快过期时用来刷新它的authorizer_refresh_token）和授权信息（授权了哪些权限等信息）
     * @return
     */
    public String getAuthorizerAccessToken(){

        String authorizerAccessToken = "";
        return authorizerAccessToken;
    }

    /**
     * 5、获取（刷新）授权公众号或小程序的接口调用凭据
     * 通过authorizer_refresh_token来刷新公众号或小程序的接口调用凭据
     *
     * 10 分钟过期
     * @return
     */
    public String getAuthorizerRefreshToken(){

        String authorizerAccessToken = "";
        return authorizerAccessToken;
    }


    /**
     * 获取
     * @return
     */
    private String getComponentVerifyTicket(){
        String componentVerifyTicket = redisTemplate.opsForValue().get("componentVerifyTicket");
        Assert.assertNotNull(componentVerifyTicket,"componentVerifyTicket 为空");
        return componentVerifyTicket;
    }

    /**
     * 设置 1、componentVerifyTicket
     * 微信回每 10 分钟推送一次
     * @return
     */
    public void setComponentVerifyTicket(String componentVerifyTicket){
        Assert.assertNotNull(componentVerifyTicket);
        redisTemplate.opsForValue().set("componentVerifyTicket",componentVerifyTicket);
    }

    /**
     * 设置 2、 componentAccessTokenResponse
     * @return
     */
    public void setComponentAccessTokenResponse(String componentAccessTokenResponse){
        Assert.assertNotNull(componentAccessTokenResponse);
        redisTemplate.opsForValue().set("componentVerifyTicket",componentAccessTokenResponse);
    }


    /**
     * 设置 3、 componentAccessTokenResponse
     * @return
     */
    public void setPreAuthCode(String preAuthCode){
        Assert.assertNotNull(preAuthCode);
        redisTemplate.opsForValue().set("preAuthCode",preAuthCode);
    }


}
