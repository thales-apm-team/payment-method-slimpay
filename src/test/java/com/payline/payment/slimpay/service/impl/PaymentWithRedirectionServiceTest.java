package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.MalformedResponseException;
import com.payline.payment.slimpay.utils.BeansUtils;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayPaymentIn;
import static com.payline.payment.slimpay.utils.TestUtils.createDefaultTransactionStatusRequest;
import static com.payline.payment.slimpay.utils.TestUtils.createRedirectionPaymentRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentWithRedirectionServiceTest {

    @InjectMocks
    private PaymentWithRedirectionServiceImpl service;

    @Mock
    private SlimpayHttpClient httpClient;

    @BeforeAll
    public void setup() {
        service = new PaymentWithRedirectionServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void finalizeRedirectionPaymentOK() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());

        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.TOP_PROCESS);
        Mockito.doReturn(paymentMocked).when(httpClient).searchPayment(Mockito.any(RedirectionPaymentRequest.class));

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
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenThrow( new HttpCallException("this is an error", "foo"));
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, failureResponse.getFailureCause());
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
    }

    @Test
    public void handleSessionExpiredKoFailure() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.TOP_PROCESS);
        Mockito.doReturn(paymentMocked).when(httpClient).searchPayment(Mockito.any(TransactionStatusRequest.class));

        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertNotNull(successResponse);
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
        Assertions.assertNotNull(successResponse.getTransactionAdditionalData());
    }

    @Test
    public void handleSessionExpiredKO() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenThrow(new HttpCallException("this is an error", "foo"));
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, failureResponse.getFailureCause());
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
    }

    @Test
    public void handleSessionExpiredKOMalformedUrl() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenThrow(new MalformedResponseException(new HttpCallException("this is an error", "foo")));
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, failureResponse.getFailureCause());
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
    }

    @Test
    public void returnResponseFailure() throws MalformedResponseException{
        SlimpayFailureResponse failureResponse = BeansUtils.createMockedSlimpayFailureResponse();
        PaymentResponse response = service.returnResponse(failureResponse, "1");

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    public void returnResponseOPEN() throws MalformedResponseException {
        SlimpayOrderResponse orderResponse = BeansUtils.createMockedSlimpayOrderResponseOpen();
        PaymentResponse response = service.returnResponse(orderResponse, "1");

        Assertions.assertEquals(PaymentResponseOnHold.class, response.getClass());
        PaymentResponseOnHold responseOnHold = (PaymentResponseOnHold) response;
        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, responseOnHold.getOnHoldCause());
    }

    @Test
    public void returnResponseClosedAbortedByClient() throws MalformedResponseException {
        SlimpayOrderResponse orderResponse = BeansUtils.createMockedSlimpayOrderResponseClosedAbortedByClient();
        PaymentResponse response = service.returnResponse(orderResponse, "1");

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.CANCEL, responseFailure.getFailureCause());
    }

    @Test
    public void returnResponseClosedAborted() throws MalformedResponseException{
        SlimpayOrderResponse orderResponse = BeansUtils.createMockedSlimpayOrderResponseClosedAborted();
        PaymentResponse response = service.returnResponse(orderResponse, "1");

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.REFUSED, responseFailure.getFailureCause());
    }

    @Test
    public void returnResponseClosedCompleted() throws MalformedResponseException {
        SlimpayOrderResponse orderResponse = BeansUtils.createMockedSlimpayOrderResponseClosed();
        PaymentResponse response = service.returnResponse(orderResponse, "1");

        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
    }

    @Test
    public void returnResponseClosedOther() throws MalformedResponseException{
        SlimpayOrderResponse orderResponse = BeansUtils.createMockedSlimpayOrderResponse("foo");
        PaymentResponse response = service.returnResponse(orderResponse, "1");

        Assertions.assertEquals(PaymentResponseOnHold.class, response.getClass());
        PaymentResponseOnHold responseOnHold = (PaymentResponseOnHold) response;
        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, responseOnHold.getOnHoldCause());
    }

}
