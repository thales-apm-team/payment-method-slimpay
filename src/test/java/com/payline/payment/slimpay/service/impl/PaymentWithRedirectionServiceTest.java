package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.MalformedResponseException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.BeansUtils;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayPaymentIn;
import static com.payline.payment.slimpay.utils.TestUtils.createDefaultTransactionStatusRequest;
import static com.payline.payment.slimpay.utils.TestUtils.createRedirectionPaymentRequest;
import static com.payline.payment.slimpay.utils.properties.constants.OrderStatus.CLOSED_ABORTED_BY_SERVER;
import static com.payline.payment.slimpay.utils.properties.constants.OrderStatus.OPEN_RUNNING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentWithRedirectionServiceTest {

    @InjectMocks
    private PaymentWithRedirectionServiceImpl service;

    @Mock
    private SlimpayHttpClient httpClient;
    private TransactionStatusRequest transactionStatusRequest;
    private RedirectionPaymentRequest redirectionStatusRequest;

    @BeforeAll
    public void setup() {
        service = new PaymentWithRedirectionServiceImpl();
        MockitoAnnotations.initMocks(this);

         transactionStatusRequest = createDefaultTransactionStatusRequest("125");
         redirectionStatusRequest = createRedirectionPaymentRequest("125");
    }

    @Test
    public void finalizeRedirectionPaymentOK() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());

        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS);
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
    public void finalizeRedirectionPaymentToProcess() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());

        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS);
        Mockito.doReturn(paymentMocked).when(httpClient).searchPayment(Mockito.any(RedirectionPaymentRequest.class));

        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        //uncomment this section if payment with status toprocess must return  PaymentResponseSuccess
        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertNotNull(successResponse);
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());

        //uncomment this section if payment with status toprocess must return  PaymentResponseOnHold
//        Assertions.assertTrue(response instanceof PaymentResponseOnHold);
//        PaymentResponseOnHold successResponse = (PaymentResponseOnHold) response;
//        Assertions.assertNotNull(successResponse.getOnHoldCause());
//        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, successResponse.getOnHoldCause());
//        Assertions.assertNotNull(successResponse);
//        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
    }
    @Test
    public void finalizeRedirectionPaymentToReplay() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());

        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_REPLAY);
        Mockito.doReturn(paymentMocked).when(httpClient).searchPayment(Mockito.any(RedirectionPaymentRequest.class));

        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        //uncomment this section if payment with status toreplay must return  PaymentResponseSuccess
        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertNotNull(successResponse);
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());

        //uncomment this section if payment with status toreplay must return  PaymentResponseOnHold
//        Assertions.assertTrue(response instanceof PaymentResponseOnHold);
//        PaymentResponseOnHold successResponse = (PaymentResponseOnHold) response;
//        Assertions.assertNotNull(successResponse.getOnHoldCause());
//        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, successResponse.getOnHoldCause());
//        Assertions.assertNotNull(successResponse.getPartnerTransactionId());




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
    public void finalizeRedirectionOrderKO() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayFailureResponse());
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("ORDER-DEV-1549638902921");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getFailureCause());
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
    }

    @Test
    public void finalizeRedirectionPaymentOnHold() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponse(OPEN_RUNNING));
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("HDEV-1550072222649");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseOnHold);
        PaymentResponseOnHold onHoldResponse = (PaymentResponseOnHold) response;
        Assertions.assertNotNull(onHoldResponse);
        Assertions.assertNotNull(onHoldResponse.getPartnerTransactionId());
    }
    @Test
    public void finalizeRedirectionPaymentOrderKO() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayFailureResponse());
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("HDEV-1550072222649");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getFailureCause());
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
    }


    @Test
    public void finalizeRedirectionPaymentDefault() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponse("UNKNOWN"));
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("HDEV-1550072222649");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseOnHold);
        PaymentResponseOnHold onHoldResponse = (PaymentResponseOnHold) response;
        Assertions.assertNotNull(onHoldResponse);
        Assertions.assertNotNull(onHoldResponse.getPartnerTransactionId());
    }

    @Test
    public void finalizeRedirectionPaymentKoAbortedClient() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosedAbortedByClient());
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("HDEV-1550072222649");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
        Assertions.assertEquals(FailureCause.CANCEL,failureResponse.getFailureCause());
    }

    @Test
    public void finalizeRedirectionPaymentKoAbortedServer() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponse(CLOSED_ABORTED_BY_SERVER));
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("HDEV-1550072222649");
        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
        Assertions.assertEquals(FailureCause.REFUSED,failureResponse.getFailureCause());

    }
    @Test
    public void finalizeRedirectionPaymentKoRejected() throws Exception {
        when(httpClient.getOrder(any(RedirectionPaymentRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());
        RedirectionPaymentRequest request = createRedirectionPaymentRequest("HDEV-1550072222649");
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        Mockito.doReturn(paymentMocked).when(httpClient).searchPayment(Mockito.any(RedirectionPaymentRequest.class));

        PaymentResponse response = service.finalizeRedirectionPayment(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
    }


    @Test
    public void handleSessionExpiredKoNotProcessed() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.NOT_PROCESSED);
        Mockito.doReturn(paymentMocked).when(httpClient).searchPayment(Mockito.any(TransactionStatusRequest.class));

        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
    }
    @Test
    public void handleSessionExpiredOrderKO() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayFailureResponse());
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getFailureCause());
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
    }

    @Test
    public void handleSessionExpiredOnHold() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponse(OPEN_RUNNING));
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseOnHold);
        PaymentResponseOnHold onHoldResponse = (PaymentResponseOnHold) response;
        Assertions.assertNotNull(onHoldResponse);
        Assertions.assertNotNull(onHoldResponse.getPartnerTransactionId());
    }

    @Test
    public void handleSessionExpireDefault() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponse("UNKNOWN"));
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseOnHold);
        PaymentResponseOnHold onHoldResponse = (PaymentResponseOnHold) response;
        Assertions.assertNotNull(onHoldResponse);
        Assertions.assertNotNull(onHoldResponse.getPartnerTransactionId());
    }

    @Test
    public void handleSessionExpiredKoAbortedClient() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosedAbortedByClient());
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
        Assertions.assertEquals(FailureCause.CANCEL,failureResponse.getFailureCause());
    }

    @Test
    public void handleSessionExpiredKoAbortedServer() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponse(CLOSED_ABORTED_BY_SERVER));
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
        Assertions.assertEquals(FailureCause.REFUSED,failureResponse.getFailureCause());

    }
    @Test
    public void handleSessionExpiredKoRejected() throws Exception {
        when(httpClient.getOrder(any(TransactionStatusRequest.class))).thenReturn(BeansUtils.createMockedSlimpayOrderResponseClosed());
        TransactionStatusRequest request = createDefaultTransactionStatusRequest("HDEV-1550072222649");
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        Mockito.doReturn(paymentMocked).when(httpClient).searchPayment(Mockito.any(TransactionStatusRequest.class));

        PaymentResponse response = service.handleSessionExpired(request);

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse);
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
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
    //Tests for returnPaymentResponsesFromPaymentRequest
    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestSuccess() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");

        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals("120", responseSuccess.getTransactionAdditionalData());
        Assertions.assertNotNull(responseSuccess.getPartnerTransactionId());

    }

    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestRejected_REFUSED() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "AM04";
//        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);
        when(httpClient.getPaymentRejectReason(any(RedirectionPaymentRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.REFUSED, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("AM04"));
    }
    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestRejected_INVALID_DATA() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "MD01";
        when(httpClient.getPaymentRejectReason(any(RedirectionPaymentRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("MD01"));
    }
    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestRejected_PAYMENT_PARTNER_ERROR() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "CNOR";
        when(httpClient.getPaymentRejectReason(any(RedirectionPaymentRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("CNOR"));
    }

    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestRejected_PARTNER_UNKNOWN_ERROR() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "MS03";
        when(httpClient.getPaymentRejectReason(any(RedirectionPaymentRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("MS03"));
    }
    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestRejected_INVALID_FIELD_FORMAT() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "FF01";
        when(httpClient.getPaymentRejectReason(any(RedirectionPaymentRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("FF01"));
    }
    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestRejected_CANCEL() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "FOCR";
        when(httpClient.getPaymentRejectReason(any(RedirectionPaymentRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.CANCEL, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("FOCR"));
    }
    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestRejected_OTHER() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "WWE";
        when(httpClient.getPaymentRejectReason(any(RedirectionPaymentRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("WWE"));
    }

    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestResponseNotProcessed() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.NOT_PROCESSED);
        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.REFUSED, responseFailure.getFailureCause());
        Assertions.assertEquals("Payment not processed", responseFailure.getErrorCode());
    }

    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestResponseToProcess() throws PluginTechnicalException{
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS);
        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");

        //uncomment this section if payment with status toprocess must return  PaymentResponseOnHold
//        Assertions.assertEquals(PaymentResponseOnHold.class, response.getClass());
//        PaymentResponseOnHold responseOnHold = (PaymentResponseOnHold) response;
//        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, responseOnHold.getOnHoldCause());

        //uncomment this section if payment with status toprocess must return  PaymentResponseSuccess
        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertEquals("120", successResponse.getTransactionAdditionalData());
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
    }

    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestResponseToReplay() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_REPLAY);
        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");

        //uncomment this section if payment with status toReplay must return  PaymentResponseOnHold
//        Assertions.assertEquals(PaymentResponseOnHold.class, response.getClass());
//        PaymentResponseOnHold responseOnHold = (PaymentResponseOnHold) response;
//        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, responseOnHold.getOnHoldCause());

        //uncomment this section if payment with status toReplay must return  PaymentResponseSuccess
        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertEquals("120", successResponse.getTransactionAdditionalData());
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
    }


    //Tests for returnPaymentResponsesFromTransactionStatusRequest
    @Test
    public void returnPaymentResponseFromTransactionStatusRequestSuccess() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED);

        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");

        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assertions.assertEquals("120", responseSuccess.getTransactionAdditionalData());
        Assertions.assertNotNull(responseSuccess.getPartnerTransactionId());

    }

    @Test
    public void returnPaymentResponseFromTransactionStatusRequestRejected_REFUSED() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "AM04";
        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.REFUSED, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("AM04"));
    }
    @Test
    public void returnPaymentResponseFromTransactionStatusRequestRejected_INVALID_DATA() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "MD01";
        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("MD01"));
    }
    @Test
    public void returnPaymentResponseFromTransactionStatusRequestRejected_PAYMENT_PARTNER_ERROR() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "CNOR";
        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("CNOR"));
    }

    @Test
    public void returnPaymentResponseFromTransactionStatusRequestRejected_PARTNER_UNKNOWN_ERROR() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "MS03";
        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("MS03"));
    }
    @Test
    public void returnPaymentResponseFromTransactionStatusRequestRejected_INVALID_FIELD_FORMAT() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "FF01";
        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.INVALID_FIELD_FORMAT, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("FF01"));
    }
    @Test
    public void returnPaymentResponseFromTransactionStatusRequestRejected_CANCEL() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "FOCR";
        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.CANCEL, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("FOCR"));
    }
    @Test
    public void returnPaymentResponseFromTransactionStatusRequestRejected_OTHER() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.REJECTED);
        String reasonCode = "WWE";
        when(httpClient.getPaymentRejectReason(any(TransactionStatusRequest.class),anyString())).thenReturn(reasonCode);

        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());
        Assertions.assertTrue (responseFailure.getErrorCode().contains("WWE"));
    }

    @Test
    public void returnPaymentResponseFromTransactionStatusRequestResponseNotProcessed() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.NOT_PROCESSED);
        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure= (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.REFUSED, responseFailure.getFailureCause());
        Assertions.assertEquals("Payment not processed", responseFailure.getErrorCode());
    }

    @Test
    public void returnPaymentResponseFromTransactionStatusRequestResponseToProcess() throws PluginTechnicalException{
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_PROCESS);
        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");

        //uncomment this section if payment with status toprocess must return  PaymentResponseOnHold
//        Assertions.assertEquals(PaymentResponseOnHold.class, response.getClass());
//        PaymentResponseOnHold responseOnHold = (PaymentResponseOnHold) response;
//        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, responseOnHold.getOnHoldCause());

        //uncomment this section if payment with status toprocess must return  PaymentResponseSuccess
        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertEquals("120", successResponse.getTransactionAdditionalData());
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
    }

    @Test
    public void returnPaymentResponseFromTransactionStatusRequestResponseToReplay() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayPaymentIn(PaymentExecutionStatus.TO_REPLAY);
        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");

        //uncomment this section if payment with status toReplay must return  PaymentResponseOnHold
//        Assertions.assertEquals(PaymentResponseOnHold.class, response.getClass());
//        PaymentResponseOnHold responseOnHold = (PaymentResponseOnHold) response;
//        Assertions.assertEquals(OnHoldCause.SCORING_ASYNC, responseOnHold.getOnHoldCause());

        //uncomment this section if payment with status toReplay must return  PaymentResponseSuccess
        Assertions.assertTrue(response instanceof PaymentResponseSuccess);
        PaymentResponseSuccess successResponse = (PaymentResponseSuccess) response;
        Assertions.assertEquals("120", successResponse.getTransactionAdditionalData());
        Assertions.assertNotNull(successResponse.getPartnerTransactionId());
    }

    @Test
    public void returnPaymentResponseFromTransactionStatusRequestResponseKO() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayFailureResponse();
        PaymentResponse response = service.returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse, "1","120");

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
        Assertions.assertNotNull(failureResponse.getFailureCause());

    }

    @Test
    public void returnPaymentResponseFromRedirectionPaymentRequestResponseKO() throws PluginTechnicalException {
        SlimpayResponse paymentResponse = BeansUtils.createMockedSlimpayFailureResponse();
        PaymentResponse response = service.returnPaymentResponseFromRedirectionPaymentRequest(redirectionStatusRequest,paymentResponse, "1","120");

        Assertions.assertTrue(response instanceof PaymentResponseFailure);
        PaymentResponseFailure failureResponse = (PaymentResponseFailure) response;
        Assertions.assertNotNull(failureResponse.getPartnerTransactionId());
        Assertions.assertNotNull(failureResponse.getErrorCode());
        Assertions.assertNotNull(failureResponse.getFailureCause());

    }
}
