package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayResponse;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static com.payline.payment.slimpay.utils.TestUtils.createRefundRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefundServiceImplTest {

    @InjectMocks
    public RefundServiceImpl service;

//    @Spy
//    SlimpayHttpClient httpClient;

    @BeforeAll
    public void setup() {
        service = new RefundServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void refundRequestTestOK() throws Exception {
        // TODO
        RefundRequest request = createRefundRequest("1245");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertTrue(refundResponse.getClass() == RefundResponseSuccess.class);
        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        Assertions.assertNotNull(refundSuccess.getStatusCode());
        Assertions.assertNotNull(refundSuccess.getPartnerTransactionId());


    }


    @Test
    public void refundRequestTestKO() throws Exception {
        //todo mock http call
        RefundRequest request = createRefundRequest("1245");
        RefundResponse refundResponse = service.refundRequest(request);

        Assertions.assertTrue(refundResponse.getClass() == RefundResponseFailure.class);
        RefundResponseFailure refundSuccess = (RefundResponseFailure) refundResponse;
        Assertions.assertNotNull(refundSuccess.getErrorCode());
        Assertions.assertNotNull(refundSuccess.getFailureCause());
    }

    @Test
    public void cancelPaymentTestOK() throws Exception {
        // TODO
        RefundRequest request = createRefundRequest("87785e67-2fa6-11e9-980d-000000000000");
        SlimpayResponse payment = SlimpayHttpClient.cancelPayment(request,"87785e67-2fa6-11e9-980d-000000000000");

//        RefundResponse refundResponse = service.handlePaymentStatus(request);

        Assertions.assertTrue(payment.getClass() == SlimpayPaymentResponse.class);
        SlimpayPaymentResponse cancelSuccess = (SlimpayPaymentResponse) payment;
        System.out.println(cancelSuccess);
//        Assertions.assertTrue(refundResponse.getClass() == RefundResponseSuccess.class);
//        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        Assertions.assertNotNull(cancelSuccess.getExecutionStatus());
        Assertions.assertNotNull(cancelSuccess.getState());


    }

}
