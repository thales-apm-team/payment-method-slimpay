package com.payline.payment.slimpay.bean.common;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class SlimpayError extends SlimpayBean {

    private int code;
    private String message;
    @SerializedName("error_description")
    private String errorDescription;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public SlimpayError(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.errorDescription = description;
    }

    public static SlimpayError fromJson(String json) {
        Gson parser = new Gson();
        return parser.fromJson(json, SlimpayError.class);
    }

    public String toPaylineError() {
        return this.code + " - " + this.message;
    }
}
