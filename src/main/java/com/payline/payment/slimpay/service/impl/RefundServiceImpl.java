package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.request.SlimpayCancelRequest;
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
import com.slimpay.hapiclient.http.JsonBody;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.PluginUtils.truncateError;

public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();
    private String NOT_REFUNDABLE_PAYMENT = "unable to refund or cancel a not processed payment";


    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {

        String partnerTransactionId = refundRequest.getPartnerTransactionId();
        try {
            /*Get payment status first, according to his execution status do or not do refund
            if payment is notprocessed we cant refund or cancel it
              if paymentExecutionStatus is toprocess or toreplay we must do a cancel,
               else we do a refund
            */
            SlimpayResponse paymentResp = httpClient.getPayment(refundRequest);
            if (SlimpayPaymentResponse.class.equals(paymentResp.getClass())) {
                SlimpayPaymentResponse paymentToRefund = (SlimpayPaymentResponse) paymentResp;
                String executionStatus = paymentToRefund.getExecutionStatus();
                if (executionStatus.equals(PaymentExecutionStatus.NOT_PROCESSED)) {
                    //can't refund or cancel a notprocessed payment
                    LOGGER.error("unable to refund or cancel a not processed payment");
                    return RefundResponseFailure.RefundResponseFailureBuilder
                            .aRefundResponseFailure()
                            .withErrorCode(truncateError(NOT_REFUNDABLE_PAYMENT))
                            .withFailureCause(FailureCause.REFUSED)
                            .withPartnerTransactionId(partnerTransactionId)
                            .build();
                } else if (executionStatus.equals(PaymentExecutionStatus.TOP_PROCESS) || executionStatus.equals(PaymentExecutionStatus.TO_REPLAY)) {
                    //do cancellation
                    return this.cancelPayment(refundRequest);

                } else {
                    //we can't cancel this payment so we try to refund it
                    //Create a payment with direction from creditor to subscriber (payout)
                    Payment slimpayPayoutRequest = beanAssembleService.assemblePayout(refundRequest);
                    //refund a payment
                    SlimpayResponse refundResponse = httpClient.createPayout(refundRequest, slimpayPayoutRequest.toJsonBody());
                    if (refundResponse.getClass() == SlimpayFailureResponse.class) {
                        SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) refundResponse;
                        return RefundResponseFailure.RefundResponseFailureBuilder
                                .aRefundResponseFailure()
                                .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                                .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                                .withPartnerTransactionId(partnerTransactionId)
                                .build();
                    } else {
                        SlimpayPaymentResponse slimpayRefundResponse = (SlimpayPaymentResponse) refundResponse;
                        return RefundResponseSuccess.RefundResponseSuccessBuilder
                                .aRefundResponseSuccess()
                                .withPartnerTransactionId(partnerTransactionId)
                                .withStatusCode(slimpayRefundResponse.getExecutionStatus())
                                .build();
                    }

                }
            } else {
                LOGGER.error("unable to retrieve payment to refund");
                SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) paymentResp;
                return RefundResponseFailure.RefundResponseFailureBuilder
                        .aRefundResponseFailure()
                        .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }
        } catch (PluginTechnicalException e) {
            LOGGER.error("unable to communicate with Slimpay server");
            return e.toRefundResponseFailure(partnerTransactionId);
        }

    }


    //note Rel not accessible passer private
    public RefundResponse cancelPayment(RefundRequest refundRequest) {

        String partnerTransactionId = refundRequest.getPartnerTransactionId();
        JsonBody cancelRequest = new SlimpayCancelRequest(SlimpayCancelRequest.reasonCode.CUST).toJsonBody();
        try {
            SlimpayResponse slimpayResponse = httpClient.cancelPayment(refundRequest, cancelRequest);
            if (SlimpayPaymentResponse.class == slimpayResponse.getClass()) {
                SlimpayPaymentResponse paymentResponse = (SlimpayPaymentResponse) slimpayResponse;
                //Cancellation is OK
                if ((paymentResponse.getExecutionStatus().equals(PaymentExecutionStatus.NOT_PROCESSED))) {
                    return RefundResponseSuccess.RefundResponseSuccessBuilder
                            .aRefundResponseSuccess()
                            .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                            .withStatusCode(paymentResponse.getState())
                            //or return paymentExecutionStatus
                            .build();

                } else {
                    //Cancellation fails but the payment object  current was returned by slimpay
                    LOGGER.error("payment cancellation fails");
                    return RefundResponseFailure.RefundResponseFailureBuilder
                            .aRefundResponseFailure()
                            .withErrorCode(truncateError("Unable to cancel the payment,executionStatus: " + paymentResponse.getExecutionStatus()))
                            .withFailureCause(FailureCause.REFUSED)
                            .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                            .build();
                }

            } else {
                //An slimpay error was returned
                LOGGER.error("unable to cancel the payment");
                SlimpayFailureResponse orderErrror = (SlimpayFailureResponse) slimpayResponse;
                return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(orderErrror.getError()))
                        .withErrorCode(truncateError(orderErrror.getError().toPaylineError()))
                        .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                        .build();
            }
        } catch (PluginTechnicalException e) {
            LOGGER.error("unable to communicate with Slimpay server");
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