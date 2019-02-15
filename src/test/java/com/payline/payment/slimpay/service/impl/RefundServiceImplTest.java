package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.payline.payment.slimpay.utils.BeansUtils.*;
import static com.payline.payment.slimpay.utils.TestUtils.createRefundRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefundServiceImplTest {

    @Spy
    SlimpayHttpClient httpClient;

    @InjectMocks
    public RefundServiceImpl service;

    @BeforeAll
    public void setup() {
        service = new RefundServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void refundRequestTestOK() throws Exception {

        String transactionId = "HDEV-1550072222649";
        SlimpayPaymentResponse payout = createMockedSlimpayPaymentOutTopProcess();
        Mockito.doReturn(payout).when(httpClient).createPayout(Mockito.any(RefundRequest.class), Mockito.any(JsonBody.class));

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
        Mockito.doReturn(payoutError).when(httpClient).createPayout(Mockito.any(RefundRequest.class), Mockito.any(JsonBody.class));
        //too much money
        RefundRequest request = createRefundRequest("HDEV-1550072222649", "10000000");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertTrue(refundResponse.getClass() == RefundResponseFailure.class);
        RefundResponseFailure refundFail = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull(refundFail.getErrorCode());
        Assertions.assertNotNull(refundFail.getFailureCause());
        System.out.println(refundFail.getFailureCause());
        System.out.println(refundFail.getErrorCode());
    }

}
