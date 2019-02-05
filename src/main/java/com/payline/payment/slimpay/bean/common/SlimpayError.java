package com.payline.payment.slimpay.bean.common;

public class SlimpayError extends SlimpayBean{

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public SlimpayError(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
