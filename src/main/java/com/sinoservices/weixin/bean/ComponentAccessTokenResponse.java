package com.sinoservices.weixin.bean;

/**
 * 获取第三方平台 component_access_token
 * @author Eason.Su
 * @Title: ComponentAccessTokenRequest
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2916:19
 */
public class ComponentAccessTokenResponse {

    private String componentAccessToken ;

    private long expiresIn ;

    private String errcode ;

    private String errmsg ;

    public String getComponentAccessToken() {
        return componentAccessToken;
    }

    public void setComponentAccessToken(String componentAccessToken) {
        this.componentAccessToken = componentAccessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
