package com.sinoservices.weixin.bean;

/**
 * 获取第三方平台component_access_token
 * @author Eason.Su
 * @Title: ComponentAccessTokenRequest
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2916:19
 */
public class ComponentAccessTokenRequest {

    private String componentAppid ;

    private String componentAppsecret ;

    private String componentVerifyTicket ;



    public String getComponentAppid() {
        return componentAppid;
    }

    public void setComponentAppid(String componentAppid) {
        this.componentAppid = componentAppid;
    }

    public String getComponentAppsecret() {
        return componentAppsecret;
    }

    public void setComponentAppsecret(String componentAppsecret) {
        this.componentAppsecret = componentAppsecret;
    }

    public String getComponentVerifyTicket() {
        return componentVerifyTicket;
    }

    public void setComponentVerifyTicket(String componentVerifyTicket) {
        this.componentVerifyTicket = componentVerifyTicket;
    }
}
