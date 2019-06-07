package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.request.SlimpayCancelRequest;
import com.payline.payment.slimpay.bean.response.PaymentResponseSuccessAdditionalData;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ResetService;
import com.slimpay.hapiclient.http.JsonBody;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.PluginUtils.truncateError;

public class ResetServiceImpl implements ResetService {

    private static final Logger LOGGER = LogManager.getLogger(ResetServiceImpl.class);

    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();

    @Override
    public ResetResponse resetRequest(ResetRequest resetRequest) {
        String partnerTransactionId = resetRequest.getPartnerTransactionId();

        try {
            // Get the payment data
            PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.fromJson(resetRequest.getTransactionAdditionalData());
            SlimpayResponse paymentResp = httpClient.getPayment(resetRequest.getPartnerConfiguration(), additionalData.getPaymentId());

            // Response is an error : can't get the payment data
            if( paymentResp instanceof SlimpayFailureResponse){
                LOGGER.error("Unable to retrieve payment data");
                SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) paymentResp;
                return ResetResponseFailure.ResetResponseFailureBuilder
                        .aResetResponseFailure()
                        .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }

            SlimpayPaymentResponse paymentToReset = (SlimpayPaymentResponse) paymentResp;

            // This payment can't be cancelled
            if( !paymentToReset.isCancellable() ){
                LOGGER.error("payment {} can't be cancelled", paymentToReset.getId());
                return ResetResponseFailure.ResetResponseFailureBuilder
                        .aResetResponseFailure()
                        .withErrorCode("payment can't be cancelled")
                        .withFailureCause(FailureCause.REFUSED)
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }

            // Cancel the payment
            JsonBody cancelRequest = new SlimpayCancelRequest(SlimpayCancelRequest.reasonCode.CUST).toJsonBody();
            SlimpayResponse slimpayResponse = httpClient.cancelPayment(resetRequest.getPartnerConfiguration(), paymentToReset.getId(), cancelRequest);

            // Cancellation failed: an error occurred
            if( slimpayResponse instanceof SlimpayFailureResponse ){
                LOGGER.error("unable to cancel the payment");
                SlimpayFailureResponse slimpayFailure = (SlimpayFailureResponse) slimpayResponse;
                return ResetResponseFailure.ResetResponseFailureBuilder
                        .aResetResponseFailure()
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayFailure.getError()))
                        .withErrorCode(truncateError(slimpayFailure.getError().toPaylineError()))
                        .withPartnerTransactionId(resetRequest.getPartnerTransactionId())
                        .build();
            }

            SlimpayPaymentResponse paymentResponse = (SlimpayPaymentResponse) slimpayResponse;

            // Payment successfully cancelled
            if( PaymentExecutionStatus.NOT_PROCESSED.equals( paymentResponse.getExecutionStatus() ) ){
                return ResetResponseSuccess.ResetResponseSuccessBuilder
                        .aResetResponseSuccess()
                        .withPartnerTransactionId(partnerTransactionId)
                        .withStatusCode(paymentResponse.getState())
                        .build();
            }
            // Cancellation failed: the new payment executionStatus is different than 'notprocessed'
            else {
                LOGGER.error("payment cancellation failed");
                return ResetResponseFailure.ResetResponseFailureBuilder
                        .aResetResponseFailure()
                        .withErrorCode(truncateError("Unable to cancel, status: " + paymentResponse.getExecutionStatus()))
                        .withFailureCause(FailureCause.REFUSED)
                        .withPartnerTransactionId(resetRequest.getPartnerTransactionId())
                        .build();
            }

        } catch (PluginTechnicalException e) {
            LOGGER.error("An error occurred during the reset process", e);
            return e.toResetResponseFailure(partnerTransactionId);
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
