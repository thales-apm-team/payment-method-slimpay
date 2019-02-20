package com.payline.payment.slimpay.utils;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SlimpayErrorHandlerTest {

    private String transactionId = "transactionId";
    private String errorCode = "error Code";


    @Test
    public void geRefundResponseFailure() {
        final FailureCause failureCause = FailureCause.SESSION_EXPIRED;
        RefundResponseFailure result = SlimpayErrorHandler.geRefundResponseFailure(failureCause, transactionId,errorCode );

        Assertions.assertEquals(transactionId, result.getPartnerTransactionId());
        Assertions.assertEquals(failureCause, result.getFailureCause());
        Assertions.assertEquals(errorCode, result.getErrorCode());
    }


}
