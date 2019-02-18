package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.slimpay.hapiclient.http.JsonBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayPaymentOutError;
import static com.payline.payment.slimpay.utils.BeansUtils.createMockedSlimpayPaymentOutTopProcess;
import static com.payline.payment.slimpay.utils.TestUtils.createRefundRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefundServiceImplTest {

    @InjectMocks
    public RefundServiceImpl service;

    @Mock
    SlimpayHttpClient httpClient;

    @BeforeAll
    public void setup() {
        service = new RefundServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void refundRequestTestOK() throws Exception {

        String transactionId = "HDEV-1550072222649";
        SlimpayPaymentResponse payout = createMockedSlimpayPaymentOutTopProcess();
        when(httpClient.createPayout(any(RefundRequest.class), any(JsonBody.class))).thenReturn(payout);

        RefundRequest request = createRefundRequest(transactionId, "100");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertTrue(refundResponse.getClass() == RefundResponseSuccess.class);
        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        Assertions.assertNotNull(refundSuccess.getStatusCode());
        Assertions.assertNotNull(refundSuccess.getPartnerTransactionId());

    }


    @Test
    public void refundRequestTestKO() throws Exception {
        SlimpayFailureResponse payoutError = createMockedSlimpayPaymentOutError();
        when(httpClient.createPayout(any(RefundRequest.class), any(JsonBody.class))).thenReturn(payoutError);
        //too much money
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "10000000");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertTrue(refundResponse.getClass() == RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull(refundFail.getErrorCode());
        Assertions.assertNotNull(refundFail.getErrorCode());
        Assertions.assertEquals(FailureCause.INVALID_DATA, refundFail.getFailureCause());
    }

    @Test
    public void refundRequestTestKOException() throws Exception {
        when(httpClient.createPayout(any(RefundRequest.class), any(JsonBody.class))).thenThrow(new HttpCallException("this is an error", "foo"));
        //too much money
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "10000000");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertTrue(refundResponse.getClass() == RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertEquals("foo", refundFail.getErrorCode());
        Assertions.assertEquals(FailureCause.COMMUNICATION_ERROR, refundFail.getFailureCause());
    }

    @Test
    public void canMultiple() {
        Assertions.assertEquals(true, service.canMultiple());
    }

    @Test
    public void canPartial() {
        Assertions.assertEquals(true, service.canPartial());
    }


}
