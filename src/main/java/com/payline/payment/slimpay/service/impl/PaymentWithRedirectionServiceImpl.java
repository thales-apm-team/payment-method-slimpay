package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.bean.response.*;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
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
    private static final String NOT_PROCESSED_PAYMENT = "Payment not processed";
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
                    //Search payment
                    SlimpayResponse paymentResponse = httpClient.searchPayment(redirectionPaymentRequest);
                    //Return a paymentResponse depending of payment executionStatus
                    return returnPaymentResponseFromRedirectionPaymentRequest(redirectionPaymentRequest,paymentResponse,transactionId,additionalData.toString());


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
                    //Get payment Id generated by Slimpay
                    //todo fusionner getPaymentId et getPayment
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
                    //Search payment
                    SlimpayResponse paymentResponse = httpClient.searchPayment(transactionStatusRequest);
                    //Return a paymentResponse depending of payment executionStatus
                    return returnPaymentResponseFromTransactionStatusRequest(transactionStatusRequest,paymentResponse,transactionId,additionalData.toString());

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
     *  return a payment response from a Payment RedirectionPaymentRequest & a SlimpayOrder
     * @param redirectionPaymentRequest
     * @param slimpayPaymentResponse
     * @param partnerTransactionId
     * @param additionalData
     * @return
     * @throws PluginTechnicalException
     */
    public PaymentResponse returnPaymentResponseFromRedirectionPaymentRequest(RedirectionPaymentRequest redirectionPaymentRequest, SlimpayResponse slimpayPaymentResponse, String partnerTransactionId, String additionalData) throws PluginTechnicalException {
            if (SlimpayPaymentResponse.class.equals(slimpayPaymentResponse.getClass())) {
                SlimpayPaymentResponse slimpayPayment = (SlimpayPaymentResponse) slimpayPaymentResponse;
                String executionStatus = slimpayPayment.getExecutionStatus();
                String paymentId = slimpayPayment.getId();
                if (executionStatus.equals(PaymentExecutionStatus.REJECTED)){
                    LOGGER.error("Payment rejected");
                    //get Slimpay reject reason code
                    String slimpayRejectReason  = httpClient.getPaymentRejectReason(redirectionPaymentRequest,paymentId);
                    FailureCause failureCause  = SlimpayErrorMapper.handleSlimpayPaymentError(slimpayRejectReason);
                    return PaymentResponseFailure.PaymentResponseFailureBuilder
                            .aPaymentResponseFailure()
                            .withErrorCode(truncateError("Payment rejected, reason code: " +slimpayRejectReason))
                            .withFailureCause(failureCause)
                            .withPartnerTransactionId(partnerTransactionId)
                            .build();
                }
                else return returnPaymentResponse(slimpayPayment,partnerTransactionId,additionalData);
            } else {
                LOGGER.error("unable to retrieve the payment");
                SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) slimpayPaymentResponse;
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }

    }
    /**
     * return a payment Response from a Transaction Status request
     *
     * @param slimpayPaymentResponse
     * @return
     */
    public PaymentResponse returnPaymentResponseFromTransactionStatusRequest(TransactionStatusRequest transactionStatusRequest, SlimpayResponse slimpayPaymentResponse, String partnerTransactionId, String additionalData) throws PluginTechnicalException {
        if (SlimpayPaymentResponse.class.equals(slimpayPaymentResponse.getClass())) {
            SlimpayPaymentResponse slimpayPayment = (SlimpayPaymentResponse) slimpayPaymentResponse;
            String executionStatus = slimpayPayment.getExecutionStatus();
            String paymentId = slimpayPayment.getId();
            if (executionStatus.equals(PaymentExecutionStatus.REJECTED)){
                LOGGER.error("Payment rejected");
                //get Slimpay reject reason code
                String slimpayRejectReason  = httpClient.getPaymentRejectReason(transactionStatusRequest,paymentId);
                FailureCause failureCause  = SlimpayErrorMapper.handleSlimpayPaymentError(slimpayRejectReason);
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(truncateError("Payment rejected, reason code: " +slimpayRejectReason))
                        .withFailureCause(failureCause)
                        .withPartnerTransactionId(partnerTransactionId)
                        .build();
            }
            else return returnPaymentResponse(slimpayPayment,partnerTransactionId,additionalData);
        } else {
            LOGGER.error("unable to retrieve the payment");
            SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) slimpayPaymentResponse;
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                    .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();
        }

    }
    /**
     * return a payment Response from a depending on Slimpay payment status
     * @param slimpayPayment
     * @param partnerTransactionId
     * @param additionalData
     * @return
     */
    public PaymentResponse returnPaymentResponse(SlimpayPaymentResponse slimpayPayment, String partnerTransactionId, String additionalData){
        String executionStatus = slimpayPayment.getExecutionStatus();
                if (executionStatus.equals(PaymentExecutionStatus.NOT_PROCESSED) ) {
            //can't refund or cancel a notprocessed payment
            LOGGER.error("Payment not processed or rejected");
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(truncateError(NOT_PROCESSED_PAYMENT))
                    .withFailureCause(FailureCause.REFUSED)
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();
        }
        //todo a confirmer  decommenter si un payment ayant le Statut TO_PROCESS TO_REPLAY doit renvoyer un PaymentResponseOnHold

/*
            //  toprocess and to retry return an PaymentResponseOnHold
               else if (executionStatus.equals(PaymentExecutionStatus.TO_PROCESS) || executionStatus.equals(PaymentExecutionStatus.TO_REPLAY)) {
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                            .aPaymentResponseOnHold()
                            .withPartnerTransactionId(partnerTransactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();
                }
 */
        else {
            return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                    .withTransactionAdditionalData(additionalData)
                    .withMessage(new Message(Message.MessageType.SUCCESS, SUCCESS_MESSAGE))
                    .withPartnerTransactionId(partnerTransactionId)
                    .withTransactionDetails(new EmptyTransactionDetails())
                    .build();

        }

    }


}


