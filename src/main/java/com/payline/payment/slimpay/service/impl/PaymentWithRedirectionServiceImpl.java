package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.common.response.PaymentResponseSuccessAdditionalData;
import com.payline.payment.slimpay.bean.common.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayResponse;
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
    private BeanAssemblerServiceImpl assemblerService;
    private static String SUCCESS_MESSAGE = "COMMANDE_OK";

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        String transactionId = redirectionPaymentRequest.getTransactionId();

        try {
            SlimpayResponse orderResponse = SlimpayHttpClient.getOrder(redirectionPaymentRequest);

            if (orderResponse.getClass() == SlimpayFailureResponse.class) {
                //Fail to get order
                SlimpayFailureResponse slimpayOrderFailureResponse = (SlimpayFailureResponse) orderResponse;
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(errorToString(slimpayOrderFailureResponse.getError()))
                        .withFailureCause(handleSlimpayFailureResponse(slimpayOrderFailureResponse.getError()))
                        .withPartnerTransactionId(redirectionPaymentRequest.getTransactionId())
                        .build();


            } else {

                SlimpayOrderResponse slimpayOrderResponse = (SlimpayOrderResponse) orderResponse;
                String state = slimpayOrderResponse.getState();
                switch (state) {
                    case OPEN:
                    case OPEN_RUNNING:
                    case OPEN_NOT_RUNNING:
                        return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                                .withPartnerTransactionId(redirectionPaymentRequest.getTransactionId())
                                .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                                .build();

                    case CLOSED_ABORTED:
                    case CLOSED_ABORTED_BY_CLIENT:
                        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                                .withPartnerTransactionId(redirectionPaymentRequest.getTransactionId())
                                .withFailureCause(FailureCause.CANCEL)
                                .build();


                    case CLOSED_COMPLETED:
                        //check statut du payment ??
                        //todo get transaction additional data (id + ref) : order mandate payment
                        //check payment state or not ??

                        PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.Builder
                                .aPaymentResponseSuccessAdditionalData()
                                .withOrderId(slimpayOrderResponse.getId())
                                .withOrderReference(slimpayOrderResponse.getReference())
//                                .withMandateId()
//                                .withMandateReference()
//                                .withPaymentId()
//                                .withPaymentReference()
                                .build();
                        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                                //AJOUTER mandateRef, orderRef, payment ref ?
                                //slimpayOrderResponse.getReference()
                                .withTransactionAdditionalData(additionalData.toJson())
                                .withMessage(new Message(Message.MessageType.SUCCESS, SUCCESS_MESSAGE))
                                .withPartnerTransactionId(redirectionPaymentRequest.getTransactionId())
                                .withTransactionDetails(new EmptyTransactionDetails())
                                .build();

                    default:
                        //todo find another response or return a paymentFailure
                        return PaymentResponseOnHold.PaymentResponseOnHoldBuilder.aPaymentResponseOnHold()
                                .withPartnerTransactionId(redirectionPaymentRequest.getTransactionId())
                                .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                                .build();

                }


            }


        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // FIXME : catch the reals Exceptions
            LOGGER.error("unable to get the payment status");
            String errorString = e.getResponseBody();
            SlimpayError error = SlimpayError.fromJson(errorString);

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(errorToString(error))
                    .withFailureCause(handleSlimpayFailureResponse(error))
                    .withPartnerTransactionId(transactionId)
                    .build();


        }
        catch (PluginTechnicalException | HttpException e) {
            LOGGER.error("unable to call slimpay server", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(e.getCause().getMessage())
                    .withFailureCause(FailureCause.COMMUNICATION_ERROR)
                    .withPartnerTransactionId(transactionId)
                    .build();
        }

    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {

        try {
//Faire une payment method request
            return null;

        } catch (Exception e) {
            // FIXME : catch the reals Exceptions
            LOGGER.error("unable to handle the session expiration", e);
            //Renvoyer une erreur
            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }

    }
}
