package com.payline.payment.slimpay.bean.response;

import com.payline.payment.slimpay.exception.MalformedResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SlimpayFailureResponseTest {

    private SlimpayFailureResponse failureResponse;


    @Test
    public void slimpayFailureResponseOK() throws MalformedResponseException {
        String json = "{code:100, message: \"message_1\",error_description:\"description_1\"}";
        failureResponse = SlimpayFailureResponse.fromJson(json);
        Assertions.assertNotNull(failureResponse.getError());
        Assertions.assertNotNull(failureResponse.getError().getErrorDescription());
        Assertions.assertNotNull(failureResponse.getError().getMessage());
        Assertions.assertEquals(100, failureResponse.getError().getCode());

    }

    @Test
    public void slimpayFailureResponseMalformedJson() {
        Assertions.assertThrows(MalformedResponseException.class, () -> {
            String json = ("{malformed !!}");
            failureResponse = SlimpayFailureResponse.fromJson(json);
        });
    }

}
