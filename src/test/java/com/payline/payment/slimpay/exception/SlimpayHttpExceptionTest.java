package com.payline.payment.slimpay.exception;

import com.payline.pmapi.bean.common.FailureCause;
import com.slimpay.hapiclient.exception.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SlimpayHttpExceptionTest {

    @Test
    void getFailureCause() {
        String json = "{code:100, message: \"message_1\",error_description:\"description_1\"}";
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));

        HttpException hhtHttpException = new HttpException(null, response, json);
        SlimpayHttpException slimpayHttpException = new SlimpayHttpException(hhtHttpException);

        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, slimpayHttpException.getFailureCause());
    }
}