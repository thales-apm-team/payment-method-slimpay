package com.payline.payment.slimpay.utils.http;

import com.payline.payment.slimpay.bean.common.SlimpayBean;

public abstract class BeanResponse extends SlimpayBean {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}