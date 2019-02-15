package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.response.PaymentResponseSuccessAdditionalData;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
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
import com.slimpay.hapiclient.exception.HttpClientErrorException;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.exception.HttpServerErrorException;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.PluginUtils.errorToString;
import static com.payline.payment.slimpay.utils.SlimpayErrorHandler.handleSlimpayFailureResponse;
import static com.payline.payment.slimpay.utils.properties.constants.OrderStatus.*;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);
    private static String SUCCESS_MESSAGE = "COMMANDE_OK";
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();


    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        String transactionId = redirectionPaymentRequest.getTransactionId();

        try {
            SlimpayResponse orderResponse = httpClient.getOrder(redirectionPaymentRequest);
            return returnResponse(orderResponse, transactionId);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("unable to get the payment status");
            String errorString = e.getResponseBody();
            SlimpayError error = SlimpayError.fromJson(errorString);

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(errorToString(error))
                    .withFailureCause(handleSlimpayFailureResponse(error))
                    .withPartnerTransactionId(transactionId)
                    .build();


        } catch (PluginTechnicalException | HttpException e) {
            LOGGER.error("unable to call slimpay server", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(e.getCause().getMessage())
                    .withFailureCause(FailureCause.COMMUNICATION_ERROR)
                    .withPartnerTransactionId(transactionId)
                    .build();
        }

    }

    /**
     * get order status from a transactionStatusRequest
     * @param transactionStatusRequest
     * @return a PaymentResponse
     */
    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        String transactionId = transactionStatusRequest.getTransactionId();

        try {
            SlimpayResponse orderResponse = httpClient.getOrder(transactionStatusRequest);
            return returnResponse(orderResponse, transactionId);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("unable to get the transactionStatusRequest status");
            String errorString = e.getResponseBody();
            SlimpayError error = SlimpayError.fromJson(errorString);

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(errorToString(error))
                    .withFailureCause(handleSlimpayFailureResponse(error))
                    .withPartnerTransactionId(transactionId)
                    .build();
        } catch (PluginTechnicalException | HttpException e) {
            LOGGER.error("unable to call slimpay server", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(e.getCause().getMessage())
                    .withFailureCause(FailureCause.COMMUNICATION_ERROR)
                    .withPartnerTransactionId(transactionId)
                    .build();
        }

    }

    public PaymentResponse returnResponse(SlimpayResponse orderResponse, String transactionId){
        if (orderResponse.getClass() == SlimpayFailureResponse.class) {
            //Fail to get order
            SlimpayFailureResponse slimpayOrderFailureResponse = (SlimpayFailureResponse) orderResponse;
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(errorToString(slimpayOrderFailureResponse.getError()))
                    .withFailureCause(handleSlimpayFailureResponse(slimpayOrderFailureResponse.getError()))
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
                            .withFailureCause(FailureCause.CANCEL)
                            .build();

                case CLOSED_ABORTED:
                case CLOSED_ABORTED_BY_SERVER:
                    return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                            .withPartnerTransactionId(transactionId)
                            .withFailureCause(FailureCause.REFUSED)
                            .build();

                case CLOSED_COMPLETED:
                    //check statut du payment ??
                    //todo get transaction additional data (id) :  mandate payment
                    //check payment state or not ??
                    PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.Builder
                            .aPaymentResponseSuccessAdditionalData()
                            .withOrderId(slimpayOrderResponse.getId())
                            .withOrderReference(slimpayOrderResponse.getReference())
//                                .withMandateId()
                            .withMandateReference(slimpayOrderResponse.getReference())
//                                .withPaymentId()
                            .withPaymentReference(slimpayOrderResponse.getReference())
                            .build();
                    return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withTransactionAdditionalData(additionalData.toJson())
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


