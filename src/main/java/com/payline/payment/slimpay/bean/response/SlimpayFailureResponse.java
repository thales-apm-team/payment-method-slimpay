package com.payline.payment.slimpay.bean.response;

import com.google.gson.JsonSyntaxException;
import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.exception.MalformedResponseException;

public class SlimpayFailureResponse extends SlimpayResponse {

    private SlimpayError error;

    public SlimpayError getError() {
        return error;
    }

    public SlimpayFailureResponse(SlimpayError error) {
        this.error = error;
    }

    public static SlimpayFailureResponse fromJson(String json)  throws MalformedResponseException {
        try {
            SlimpayError error = SlimpayError.fromJson(json);
            return new SlimpayFailureResponse(error);
        }
        catch( JsonSyntaxException e ){
            throw new MalformedResponseException( e );
        }
    }
}
