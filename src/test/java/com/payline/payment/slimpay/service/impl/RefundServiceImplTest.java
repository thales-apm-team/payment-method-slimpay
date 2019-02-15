package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.payline.payment.slimpay.utils.TestUtils.createRefundRequest;


public class RefundServiceImplTest {
//todo ASAP

//    private RefundServiceImpl service = new RefundServiceImpl();

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
        // TODO mocker http

////        Mockito.doReturn(null).when(httpClient).createPayout(Mockito.any(RefundRequest.class), Mockito.any(JsonBody.class));
//        PowerMockito.mockStatic(SlimpayHttpClient.class);
//        PowerMockito.doReturn(null).when(SlimpayHttpClient.createPayout(Mockito.any(RefundRequest.class), Mockito.any(JsonBody.class)));
//

        RefundRequest request = createRefundRequest("HDEV-1550072222649","100");
        RefundResponse refundResponse = service.refundRequest(request);


        Assertions.assertTrue(refundResponse.getClass() == RefundResponseSuccess.class);
        RefundResponseSuccess refundSuccess = (RefundResponseSuccess) refundResponse;
        Assertions.assertNotNull(refundSuccess.getStatusCode());
        Assertions.assertNotNull(refundSuccess.getPartnerTransactionId());


    }


    @Test
    public void refundRequestTestKO() throws Exception {
        //todo mock http call
        //too much money
        RefundRequest request = createRefundRequest("HDEV-1550072222649","10000000");
        RefundResponse refundResponse = service.refundRequest(request);

//        Assertions.assertTrue(refundResponse.getClass() == RefundResponseFailure.class);
//        RefundResponseFailure refundSuccess = (RefundResponseFailure) refundResponse;
//        Assertions.assertNotNull(refundSuccess.getErrorCode());
//        Assertions.assertNotNull(refundSuccess.getFailureCause());
    }

    @Test
    public void cancelPaymentTestOK() throws Exception {
        // TODO
        RefundRequest request = createRefundRequest("87785e67-2fa6-11e9-980d-000000000000","40800");
        SlimpayResponse payment = httpClient.cancelPayment(request,"87785e67-2fa6-11e9-980d-000000000000");

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
