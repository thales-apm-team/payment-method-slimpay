package com.payline.payment.slimpay.utils;

import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;

import static com.payline.payment.slimpay.utils.SlimpayErrorMapper.handleSlimpayError;

public class SlimpayErrorHandler {


    private SlimpayErrorHandler() {
        super();
    }

    public static PaymentResponseFailure getPaymentResponseFailure(final FailureCause failureCause) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .build();
    }

    public static PaymentResponseFailure getPaymentResponseFailure(final FailureCause failureCause, String transactionId, String errorCode) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .withPartnerTransactionId(transactionId)
                .withErrorCode(errorCode)
                .build();
    }

    public static RefundResponseFailure geRefundResponseFailure(final FailureCause failureCause, String transactionId, String errorCode) {
        return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                .withFailureCause(failureCause)
                .withPartnerTransactionId(transactionId)
                .withErrorCode(errorCode)
                .build();
    }

    public static FailureCause handleSlimpayFailureResponse(SlimpayError error) {
        return handleSlimpayError(error);
    }

}


