package com.payline.payment.slimpay.bean.common;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class SlimpayError extends SlimpayBean{

    // a voir
    private int code;
    private String message;
    // a voir
    @SerializedName("error_description")
    private String errorDscription;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorDescription() {
        return errorDscription;
    }

    public SlimpayError(int code, String message,String description) {
        this.code = code;
        this.message = message;
        this.errorDscription = description;
    }

    public SlimpayError fromJson(String json){
        Gson parser = new Gson();
        return parser.fromJson(json, SlimpayError.class);
    }
}
