package com.payline.payment.slimpay.service.impl;

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


}
