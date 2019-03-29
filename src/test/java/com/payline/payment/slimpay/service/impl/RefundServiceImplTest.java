package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.MalformedResponseException;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.slimpay.hapiclient.http.JsonBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.BeansUtils.*;
import static com.payline.payment.slimpay.utils.TestUtils.createRefundRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefundServiceImplTest {

    @InjectMocks
    public RefundServiceImpl service;

    @Mock
    SlimpayHttpClient httpClient;

    @BeforeEach
    public void setup() {
        service = new RefundServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void refundRequestTestOK() throws Exception {

        String transactionId = "HDEV-1550072222649";
        SlimpayPaymentResponse payout = createMockedSlimpayPaymentOutTopProcess();
        when(httpClient.createPayout(any(RefundRequest.class), any(JsonBody.class))).thenReturn(payout);
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED);

        Mockito.doReturn(payout).when(httpClient).createPayout(Mockito.any(RefundRequest.class), Mockito.any(JsonBody.class));
        Mockito.doReturn(paymentMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));

        RefundRequest request = createRefundRequest(transactionId, "100");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseSuccess.class);
        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        Assertions.assertNotNull(refundSuccess.getStatusCode());
        Assertions.assertNotNull(refundSuccess.getPartnerTransactionId());

    }

    @Test
    public void refundRequestTestKOGetPaymentFail() throws Exception {

        String transactionId = "HDEV-1550072222649";
        SlimpayFailureResponse spErrorMocked = createMockedSlimpayFailureResponse();

        Mockito.doReturn(spErrorMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));

        RefundRequest request = createRefundRequest(transactionId, "100");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseFailure.class);
        RefundResponseFailure refundFailure = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull(refundFailure.getErrorCode());
        Assertions.assertNotNull(refundFailure.getPartnerTransactionId());

    }

    @Test
    public void refundRequestTestKO() throws Exception {
        SlimpayFailureResponse payoutError = createMockedSlimpayPaymentOutError();
        when(httpClient.createPayout(any(RefundRequest.class), any(JsonBody.class))).thenReturn(payoutError);
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED);

        Mockito.doReturn(payoutError).when(httpClient).createPayout(Mockito.any(RefundRequest.class), Mockito.any(JsonBody.class));
        Mockito.doReturn(paymentMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));
        //too much money
        RefundRequest request = createRefundRequest("Y-ORDER-REF-1550495902513", "10000000");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull(refundFail.getErrorCode());
        Assertions.assertNotNull(refundFail.getFailureCause());
        System.out.println(refundFail.getFailureCause());
        System.out.println(refundFail.getErrorCode());
    }

    @Test
    public void refundRequestTestKOException() throws Exception {
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED);
        Mockito.doReturn(paymentMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));

        when(httpClient.createPayout(any(RefundRequest.class), any(JsonBody.class))).thenThrow(new HttpCallException("this is an error", "foo"));
        //too much money
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "10000000");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertEquals("foo", refundFail.getErrorCode());
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, refundFail.getFailureCause());
    }

    @Test
    public void cancelPaymentTestKO() throws Exception {
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.TOP_PROCESS);
        Mockito.doReturn(paymentMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));

        when(httpClient.cancelPayment(any(RefundRequest.class),any(JsonBody.class))).thenReturn(paymentMocked);
        //too much money
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "10000000");
        RefundResponse refundResponse = service.cancelPayment(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertEquals(FailureCause.REFUSED, refundFail.getFailureCause());
    }

    @Test
    public void cancelPaymentTestOK() throws Exception {

        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.NOT_PROCESSED);
        when(httpClient.cancelPayment(any(RefundRequest.class),any(JsonBody.class))).thenReturn(paymentMocked);
        //too much money
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "1000");
        RefundResponse refundResponse = service.cancelPayment(request);

        Assertions.assertSame(RefundResponseSuccess.class,refundResponse.getClass());
        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        Assertions.assertNotNull( refundSuccess.getPartnerTransactionId());

    }

    @Test
    public void cancelPaymentInvalidResponse() throws Exception {
        SlimpayFailureResponse paymentMocked = createMockedCancelPaymentError();
        Mockito.doReturn(paymentMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));

        when(httpClient.cancelPayment(any(RefundRequest.class),any(JsonBody.class))).thenReturn(paymentMocked);
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "1000");
        RefundResponse refundResponse = service.cancelPayment(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull( refundFail.getPartnerTransactionId());
        Assertions.assertNotNull( refundFail.getErrorCode());
        Assertions.assertNotNull( refundFail.getFailureCause());
    }


    @Test
    public void cancelPaymentKOMalformedResponse() throws Exception {
        SlimpayFailureResponse paymentMocked = createMockedCancelPaymentError();
        Mockito.doReturn(paymentMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));

        when(httpClient.cancelPayment(Mockito.any(RefundRequest.class),any(JsonBody.class))).thenThrow(new MalformedResponseException(new HttpCallException("this is an error", "foo")));
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "1000");
        RefundResponse refundResponse = service.cancelPayment(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull( refundFail.getPartnerTransactionId());
        Assertions.assertNotNull( refundFail.getErrorCode());
        Assertions.assertNotNull( refundFail.getFailureCause());
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, refundFail.getFailureCause());
    }

    @Test
    public void refundPaymentKOMalformedResponse() throws Exception {
        SlimpayPaymentResponse paymentMocked = createMockedSlimpayPaymentIn(PaymentExecutionStatus.PROCESSED);
        Mockito.doReturn(paymentMocked).when(httpClient).getPayment(Mockito.any(RefundRequest.class));

        when(httpClient.createPayout(Mockito.any(RefundRequest.class),any(JsonBody.class))).thenThrow(new MalformedResponseException(new HttpCallException("this is an error", "foo")));
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "1000");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertSame(refundResponse.getClass(), RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull( refundFail.getPartnerTransactionId());
        Assertions.assertNotNull( refundFail.getErrorCode());
        Assertions.assertNotNull( refundFail.getFailureCause());
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, refundFail.getFailureCause());
    }

    @Test
    public void canMultiple() {
        Assertions.assertTrue(service.canMultiple());
    }

    @Test
    public void canPartial() {
        Assertions.assertTrue(service.canPartial());
    }


}
