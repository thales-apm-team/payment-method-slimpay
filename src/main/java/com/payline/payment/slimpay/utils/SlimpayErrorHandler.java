package com.payline.payment.slimpay.utils;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

public class SlimpayErrorHandler {

    private static final Logger LOGGER = LogManager.getLogger(SlimpayErrorHandler.class);

    private SlimpayErrorHandler() {
        super();
    }

    public static PaymentResponseFailure getPaymentResponseFailure(final FailureCause failureCause) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .build();
    }

    public static PaymentResponseFailure getPaymentResponseFailure(final FailureCause failureCause, String transactionId) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .withPartnerTransactionId(transactionId)
                .build();
    }

    public static RefundResponseFailure geRefundResponseFailure(final FailureCause failureCause) {
        return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                .withFailureCause(failureCause)
                .build();
    }

    public static RefundResponseFailure geRefundResponseFailure(final FailureCause failureCause, String transactionId) {
        return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                .withFailureCause(failureCause)
                .withPartnerTransactionId(transactionId)
                .build();
    }


}


