package com.payline.payment.slimpay.bean.common;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SlimpayErrorTest {

    private SlimpayError error;

    @Test
    public void slimpayErrorTest() {
        error = new SlimpayError(100, "message", "description");
        Assertions.assertEquals(100, error.getCode());
        Assertions.assertEquals("message", error.getMessage());
        Assertions.assertEquals("description", error.getErrorDescription());

    }

    @Test
    public void slimpayErrorFromJson() {
        String json = "{code:100, message: \"message_1\",error_description:\"description_1\"}";
        error = SlimpayError.fromJson(json);
        Assertions.assertEquals(100, error.getCode());
        Assertions.assertEquals("message_1", error.getMessage());
        Assertions.assertEquals("description_1", error.getErrorDescription());

    }

    @Test
    public void slimpayErrorFromJsonKo() {
        Assertions.assertThrows(JsonSyntaxException.class,()->{
        String json = "{code:100, message: \"message_1\",error_description:\"description_1\"";
        error = SlimpayError.fromJson(json);
        });
    }

}
