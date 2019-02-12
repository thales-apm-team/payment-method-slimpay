package com.payline.payment.slimpay.bean.common.response;

import com.payline.payment.slimpay.bean.common.SlimpayError;

public class SlimpayFailureResponse extends SlimpayResponse {

    private SlimpayError error;

    public SlimpayError getError() {
        return error;
    }

    public SlimpayFailureResponse(SlimpayError error) {
        this.error = error;
    }

    public static SlimpayFailureResponse fromJson(String json){

        SlimpayError error = SlimpayError.fromJson(json);
        return new SlimpayFailureResponse(error);
    }
}
