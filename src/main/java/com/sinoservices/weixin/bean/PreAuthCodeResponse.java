package com.sinoservices.weixin.bean;

/**
 * @author Eason.Su
 * @Title: preAuthCodeRequest
 * @ProjectName weixin
 * @Description: TODO
 * @date 2019-5-2921:08
 */
public class PreAuthCodeResponse {

    private String preAuthCode;

    private String expiresIn;


    private String errcode ;

    private String errmsg ;

    public String getPreAuthCode() {
        return preAuthCode;
    }

    public void setPreAuthCode(String preAuthCode) {
        this.preAuthCode = preAuthCode;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
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
