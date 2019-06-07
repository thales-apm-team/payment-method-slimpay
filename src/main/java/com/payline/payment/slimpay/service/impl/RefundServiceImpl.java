package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.response.PaymentResponseSuccessAdditionalData;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.PluginUtils.truncateError;

public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();


    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String partnerTransactionId = refundRequest.getPartnerTransactionId();

        try {
            // Get the payment data
            PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.fromJson(refundRequest.getTransactionAdditionalData());
            SlimpayResponse paymentResp = httpClient.getPayment(refundRequest.getPartnerConfiguration(), additionalData.getPaymentId());

            // Response is an error : can't get the payment data
            if( paymentResp instanceof SlimpayFailureResponse ){
                LOGGER.error("Unable to retrieve payment data");
                SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) paymentResp;
                return RefundResponseFailure.RefundResponseFailureBuilder
                        .aRefundResponseFailure()
                        .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }

            // Can't refund a payment which has not been processed
            SlimpayPaymentResponse paymentToRefund = (SlimpayPaymentResponse) paymentResp;
            if( !PaymentExecutionStatus.PROCESSED.equals( paymentToRefund.getExecutionStatus() ) ){
                LOGGER.error("payment executionStatus is not PROCESSED");
                return RefundResponseFailure.RefundResponseFailureBuilder
                        .aRefundResponseFailure()
                        .withErrorCode("payment executionStatus is not PROCESSED")
                        .withFailureCause(FailureCause.REFUSED)
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }

            // Create a payment with direction from creditor to subscriber (payout)
            Payment slimpayPayoutRequest = beanAssembleService.assemblePayout(refundRequest);
            SlimpayResponse refundResponse = httpClient.createPayout(refundRequest.getPartnerConfiguration(), slimpayPayoutRequest.toJsonBody());
            // Payout creation failed
            if( refundResponse instanceof SlimpayFailureResponse ){
                SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) refundResponse;
                return RefundResponseFailure.RefundResponseFailureBuilder
                        .aRefundResponseFailure()
                        .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }
            // Payout successfully created
            else {
                SlimpayPaymentResponse slimpayRefundResponse = (SlimpayPaymentResponse) refundResponse;
                return RefundResponseSuccess.RefundResponseSuccessBuilder
                        .aRefundResponseSuccess()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(slimpayRefundResponse.getExecutionStatus())
                        .build();
            }

        } catch (PluginTechnicalException e) {
            LOGGER.error("An error occurred during the refund process", e);
            return e.toRefundResponseFailure(partnerTransactionId);
        }
    }

    @Override
    public boolean canMultiple() {
        return true;
    }

    @Override
    public boolean canPartial() {
        return true;
    }
}