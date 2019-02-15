package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.utils.BeansUtils;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.payline.payment.slimpay.utils.TestUtils.createDefaultTransactionStatusRequest;
import static com.payline.payment.slimpay.utils.TestUtils.createRedirectionPaymentRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentWithRedirectionServiceTest {

    @InjectMocks
    private static PaymentWithRedirectionServiceImpl service;

    @Mock
    private SlimpayHttpClient httpClient;

    @BeforeAll
    public static void setup() {
        service = new PaymentWithRedirectionServiceImpl();
        MockitoAnnotations.initMocks(PaymentWithRedirectionServiceTest.class);
    }

    @Test
    public void finalizeRedirectionPaymentOK() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertNotNull(successResponse);
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
        Assertions.assertNotNull(successResponse.getTransactionAdditionalData());
    }

    @Test
    public void finalizeRedirectionPaymentKO() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenThrow(new HttpCallException("401", "bar"));

        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ZORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getFailureCause());
    }

    @Test
    public void finalizeRedirectionPaymentKOAborted() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosedAborted());

        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ZORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getFailureCause());
    }

//    @Test
//    public void finalizeRedirectionPaymentFailure() throws Exception {
//        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayFailureResponse());
//
//        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ZORDER-DEV-1549638902921");
//        PaymentResponse response = service.finalizeRedirectionPayment(request);
//
//        Assertions.assertTrue(response instanceof PaymentResponseFailure);
//        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
//        Assertions.assertNotNull(failureResponse);
//        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
//        Assertions.assertNotNull(failureResponse.getFailureCause());
//    }

    @Test
    public void handleSessionExpiredKo() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenThrow(new HttpCallException("401", "bar"));

        //aborted order
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("Y-ORDER-REF-1550138270755");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getFailureCause());
    }

//    @Test
//    public void handleSessionExpiredOk() throws Exception {
//        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayFailureResponse());
//        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
//        PaymentResponse response = service.handleSessionExpired(request);
//
//        Assertions.assertTrue(response instanceof PaymentResponseFailure);
//        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
//        Assertions.assertNotNull(failureResponse);
//        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
//        Assertions.assertNotNull(failureResponse.getFailureCause());
//    }

    @Test
    public void handleSessionExpiredKoFailure() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertNotNull(successResponse);
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
        Assertions.assertNotNull(successResponse.getTransactionAdditionalData());
    }
}
