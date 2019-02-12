package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.payline.payment.slimpay.utils.TestUtils.createRedirectionPaymentRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentWithRedirectionServiceTest {

    @InjectMocks
    public PaymentWithRedirectionServiceImpl service;

    @Spy
    SlimpayHttpClient httpClient;

    @BeforeAll
    public void setup() {
        service = new PaymentWithRedirectionServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    //todo mocker les appels http

    @Test
    public void finalizeRedirectionPaymentOK() throws Exception {
        PaymentWithRedirectionServiceImpl service = new PaymentWithRedirectionServiceImpl();

        RedirectionPaymentRequest request =  createRedirectionPaymentRequest("ORDER-DEV-1549638902921");
       PaymentResponse response = service.finalizeRedirectionPayment(request);
        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        System.out.println(successResponse.getPartnerTransactionId());
        Assertions.assertNotNull(successResponse);
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
        Assertions.assertNotNull(successResponse.getTransactionAdditionalData());
    }

    @Test
    public void finalizeRedirectionPaymentKO() throws Exception {
        //todo mocker response

        PaymentWithRedirectionServiceImpl    service = new PaymentWithRedirectionServiceImpl();
        RedirectionPaymentRequest request =  createRedirectionPaymentRequest("ZORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);
        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
        Assertions.assertNotNull(failureResponse.getFailureCause());


    }

    @Test
    public void handleSessionExpiredKo() throws Exception {
        // TODO
    }

    @Test
    public void handleSessionExpiredOk() throws Exception {
        // TODO
    }
}
