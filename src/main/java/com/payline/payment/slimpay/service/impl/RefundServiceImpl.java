package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.request.SlimpayCancelRequest;
import com.payline.payment.slimpay.bean.response.PaymentResponseSuccessAdditionalData;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.business.impl.BeanAssemblerBusinessImpl;
import com.payline.payment.slimpay.exception.CustomPluginException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.DateUtils;
import com.payline.payment.slimpay.utils.PluginUtils;
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
import com.slimpay.hapiclient.http.JsonBody;
import org.apache.logging.log4j.Logger;

import java.util.Date;

import static com.payline.payment.slimpay.utils.PluginUtils.truncateError;

public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    private BeanAssemblerBusinessImpl beanAssembleService = BeanAssemblerBusinessImpl.getInstance();
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

            SlimpayPaymentResponse paymentData = (SlimpayPaymentResponse) paymentResp;
            Date executionDate = DateUtils.parse( paymentData.getExecutionDate() );

            // If executionDate is in the future, we are in the "reset" case. Else, we are in the "refund" case.
            if( new Date().before( executionDate ) ){
                return this.reset( paymentData, refundRequest );
            } else {
                return this.refund( paymentData, refundRequest );
            }

        } catch (PluginTechnicalException e) {
            LOGGER.error("An error occurred during the refund process", e);
            return e.toRefundResponseFailure(partnerTransactionId);
        }
    }

    private RefundResponse refund( SlimpayPaymentResponse payment, RefundRequest refundRequest )
            throws PluginTechnicalException {
        // Add 5 working days to the execution date
        Date executionDate = DateUtils.parse( payment.getExecutionDate() );
        Date safeRefundDay = DateUtils.addWorkingDays( executionDate, 5 );

        // Before this date, it's not safe to make a refund
        if( new Date().before( safeRefundDay ) ){
            throw new CustomPluginException( FailureCause.REFUSED, "this payment cannot be refunded yet" );
        }

        // Create a payment with direction from creditor to subscriber (payout)
        Payment slimpayPayoutRequest = beanAssembleService.assemblePayout(refundRequest);
        SlimpayResponse refundResponse = httpClient.createPayout(refundRequest.getPartnerConfiguration(), slimpayPayoutRequest.toJsonBody());

        // Payout creation failed
        if( refundResponse instanceof SlimpayFailureResponse ){
            SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) refundResponse;
            throw new CustomPluginException(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse), slimpayPayoutFailureResponse.getError().toPaylineError());
        }

        // Payout successfully created
        SlimpayPaymentResponse slimpayRefundResponse = (SlimpayPaymentResponse) refundResponse;
        return RefundResponseSuccess.RefundResponseSuccessBuilder
                .aRefundResponseSuccess()
                .withPartnerTransactionId( refundRequest.getPartnerTransactionId() )
                .withStatusCode(slimpayRefundResponse.getExecutionStatus())
                .build();
    }

    private RefundResponse reset( SlimpayPaymentResponse payment, RefundRequest refundRequest )
            throws PluginTechnicalException {
        // If payment cannot be cancelled, return an error
        if( !payment.isCancellable() ){
            throw new CustomPluginException( FailureCause.REFUSED, "this payment is no longer cancellable" );
        }

        // Cancel the payment
        JsonBody cancelRequest = new SlimpayCancelRequest(SlimpayCancelRequest.reasonCode.CUST).toJsonBody();
        SlimpayResponse slimpayResponse = httpClient.cancelPayment(refundRequest.getPartnerConfiguration(), payment.getId(), cancelRequest);

        // Cancellation failed: an error occurred
        if( slimpayResponse instanceof SlimpayFailureResponse ){
            FailureCause failureCause = SlimpayErrorMapper.handleSlimpayError(((SlimpayFailureResponse) slimpayResponse).getError());
            throw new CustomPluginException( failureCause, "payment cancellation failed" );
        }

        SlimpayPaymentResponse cancelResponse = (SlimpayPaymentResponse) slimpayResponse;

        // Cancellation failed: the new payment executionStatus is different than 'notprocessed'
        if( !PaymentExecutionStatus.NOT_PROCESSED.equals( cancelResponse.getExecutionStatus() ) ){
            throw new CustomPluginException( FailureCause.REFUSED, PluginUtils.truncateError("payment cancellation failed, status: " + cancelResponse.getExecutionStatus() ) );
        }

        // Payment successfully cancelled
        return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                .withPartnerTransactionId( refundRequest.getPartnerTransactionId() )
                .withStatusCode( cancelResponse.getExecutionStatus() )
                .build();
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