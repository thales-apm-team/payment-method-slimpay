package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.bean.response.*;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.Message;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.PluginUtils.truncateError;
import static com.payline.payment.slimpay.utils.properties.constants.OrderStatus.*;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);
    private static final String SUCCESS_MESSAGE = "COMMANDE_OK";
    private static final String CANCELLATION_CLIENT_MESSAGE = "Cancelled by client";
    private static final String CANCELLATION_SERVER_MESSAGE = "Cancelled by server";
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();


    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        String transactionId = redirectionPaymentRequest.getTransactionId();

        try {
            SlimpayResponse orderResponse = httpClient.getOrder(redirectionPaymentRequest);
            if (orderResponse.getClass() == SlimpayFailureResponse.class) {
                //Fail to get order
                SlimpayFailureResponse slimpayOrderFailureResponse = (SlimpayFailureResponse) orderResponse;
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(truncateError(slimpayOrderFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayOrderFailureResponse))
                        .withPartnerTransactionId(transactionId)
                        .build();

            }

            SlimpayOrderResponse slimpayOrderResponse = (SlimpayOrderResponse) orderResponse;
            String state = slimpayOrderResponse.getState();
            switch (state) {
                case OPEN:
                case OPEN_RUNNING:
                case OPEN_NOT_RUNNING:
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                            .withPartnerTransactionId(transactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();

                case CLOSED_ABORTED_BY_CLIENT:
                    return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withPartnerTransactionId(transactionId)
                            .withErrorCode(CANCELLATION_CLIENT_MESSAGE)
                            .withFailureCause(FailureCause.CANCEL)
                            .build();

                case CLOSED_ABORTED:
                case CLOSED_ABORTED_BY_SERVER:
                    return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withPartnerTransactionId(transactionId)
                            .withErrorCode(CANCELLATION_SERVER_MESSAGE)
                            .withFailureCause(FailureCause.REFUSED)
                            .build();

                case CLOSED_COMPLETED:
                    String reference = slimpayOrderResponse.getReference();
                    //get Payment Id genrated by Slimpay
                        String paymentId = httpClient.getPaymentId(redirectionPaymentRequest);

                    PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.Builder
                            .aPaymentResponseSuccessAdditionalData()
                            .withOrderId(slimpayOrderResponse.getId())
                            .withOrderReference(reference)
                            .withMandateReference(reference)
                            .withPaymentReference(reference)
                            .withPaymentId(paymentId)
                            .build();
                    return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withTransactionAdditionalData(additionalData.toString())
                            .withMessage(new Message(Message.MessageType.SUCCESS, SUCCESS_MESSAGE))
                            .withPartnerTransactionId(transactionId)
                            .withTransactionDetails(new EmptyTransactionDetails())
                            .build();

                default:
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                            .withPartnerTransactionId(transactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();
            }

        } catch (PluginTechnicalException e) {
            LOGGER.error("unable to call slimpay server", e);
            return e.toPaymentResponseFailure(transactionId);
        }

    }

    /**
     * get order status from a transactionStatusRequest
     *
     * @param transactionStatusRequest
     * @return a PaymentResponse
     */
    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        String transactionId = transactionStatusRequest.getTransactionId();

        try {
            SlimpayResponse orderResponse = httpClient.getOrder(transactionStatusRequest);
            if (orderResponse.getClass() == SlimpayFailureResponse.class) {
                //Fail to get order
                SlimpayFailureResponse slimpayOrderFailureResponse = (SlimpayFailureResponse) orderResponse;
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(truncateError(slimpayOrderFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayOrderFailureResponse))
                        .withPartnerTransactionId(transactionId)
                        .build();

            }

            SlimpayOrderResponse slimpayOrderResponse = (SlimpayOrderResponse) orderResponse;
            String state = slimpayOrderResponse.getState();
            switch (state) {
                case OPEN:
                case OPEN_RUNNING:
                case OPEN_NOT_RUNNING:
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                            .withPartnerTransactionId(transactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();

                case CLOSED_ABORTED_BY_CLIENT:
                    return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withPartnerTransactionId(transactionId)
                            .withErrorCode(CANCELLATION_CLIENT_MESSAGE)
                            .withFailureCause(FailureCause.CANCEL)
                            .build();

                case CLOSED_ABORTED:
                case CLOSED_ABORTED_BY_SERVER:
                    return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withPartnerTransactionId(transactionId)
                            .withErrorCode(CANCELLATION_SERVER_MESSAGE)
                            .withFailureCause(FailureCause.REFUSED)
                            .build();

                case CLOSED_COMPLETED:
                    String reference = slimpayOrderResponse.getReference();
                    //check statut du payment ??
                    //check payment state or not ??

                    //Get payment Id generated by Slimpay
                        String paymentId = httpClient.getPaymentId(transactionStatusRequest);
                    //Get PaymentId
                    PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.Builder
                            .aPaymentResponseSuccessAdditionalData()
                            .withOrderId(slimpayOrderResponse.getId())
                            .withOrderReference(reference)
                            .withMandateReference(reference)
                            .withPaymentReference(reference)
                            .withPaymentId(paymentId)
                            .build();
                    return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withTransactionAdditionalData(additionalData.toString())
                            .withMessage(new Message(Message.MessageType.SUCCESS, SUCCESS_MESSAGE))
                            .withPartnerTransactionId(transactionId)
                            .withTransactionDetails(new EmptyTransactionDetails())
                            .build();

                default:
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                            .withPartnerTransactionId(transactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();


            }

        } catch (PluginTechnicalException e) {
            LOGGER.error("unable to call slimpay server", e);
            return e.toPaymentResponseFailure(transactionId);
        }

    }

    public PaymentResponse returnResponse(SlimpayResponse orderResponse, String transactionId) {
        if (orderResponse.getClass() == SlimpayFailureResponse.class) {
            //Fail to get order
            SlimpayFailureResponse slimpayOrderFailureResponse = (SlimpayFailureResponse) orderResponse;
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(truncateError(slimpayOrderFailureResponse.getError().toPaylineError()))
                    .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayOrderFailureResponse))
                    .withPartnerTransactionId(transactionId)
                    .build();

        } else {

            SlimpayOrderResponse slimpayOrderResponse = (SlimpayOrderResponse) orderResponse;
            String state = slimpayOrderResponse.getState();
            switch (state) {
                case OPEN:
                case OPEN_RUNNING:
                case OPEN_NOT_RUNNING:
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                            .withPartnerTransactionId(transactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();

                case CLOSED_ABORTED_BY_CLIENT:
                    return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withPartnerTransactionId(transactionId)
                            .withErrorCode(CANCELLATION_CLIENT_MESSAGE)
                            .withFailureCause(FailureCause.CANCEL)
                            .build();

                case CLOSED_ABORTED:
                case CLOSED_ABORTED_BY_SERVER:
                    return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withPartnerTransactionId(transactionId)
                            .withErrorCode(CANCELLATION_SERVER_MESSAGE)
                            .withFailureCause(FailureCause.REFUSED)
                            .build();

                case CLOSED_COMPLETED:
                    String reference = slimpayOrderResponse.getReference();
                    //check statut du payment ??
                    //check payment state or not ??
                    //Get PaymentId
                    PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.Builder
                            .aPaymentResponseSuccessAdditionalData()
                            .withOrderId(slimpayOrderResponse.getId())
                            .withOrderReference(reference)
                            .withMandateReference(reference)
                            .withPaymentReference(reference)
                            .build();
                    return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withTransactionAdditionalData(additionalData.toString())
                            .withMessage(new Message(Message.MessageType.SUCCESS, SUCCESS_MESSAGE))
                            .withPartnerTransactionId(transactionId)
                            .withTransactionDetails(new EmptyTransactionDetails())
                            .build();

                default:
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                            .withPartnerTransactionId(transactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();
            }

        }
    }



}


