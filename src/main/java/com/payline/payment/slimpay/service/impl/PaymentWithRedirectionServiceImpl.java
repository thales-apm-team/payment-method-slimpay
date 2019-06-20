package com.payline.payment.slimpay.service.impl;


import com.payline.payment.slimpay.bean.response.*;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.*;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Order;
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
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        return this.checkOrder(
                redirectionPaymentRequest.getPartnerConfiguration(),
                redirectionPaymentRequest.getContractConfiguration(),
                redirectionPaymentRequest.getTransactionId(),
                redirectionPaymentRequest.getAmount(),
                redirectionPaymentRequest.getBuyer(),
                redirectionPaymentRequest.getOrder()
        );
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        return this.checkOrder(
                transactionStatusRequest.getPartnerConfiguration(),
                transactionStatusRequest.getContractConfiguration(),
                transactionStatusRequest.getTransactionId(),
                transactionStatusRequest.getAmount(),
                transactionStatusRequest.getBuyer(),
                transactionStatusRequest.getOrder()
        );
    }

    /**
     * Checks the state of the order and returns the corresponding {@link PaymentResponse}.
     *
     * @param partnerConfiguration the {@link PartnerConfiguration} data
     * @param contractConfiguration the {@link ContractConfiguration} data
     * @param transactionId the transaction ID
     * @param amount The Amount object associated to the request
     * @param buyer The Buyer object associated to the request
     *
     * @return The {@link PaymentResponse} representing the state of the transaction.
     */
    PaymentResponse checkOrder( PartnerConfiguration partnerConfiguration, ContractConfiguration contractConfiguration, String transactionId, Amount amount, Buyer buyer, Order order ){
        try {
            // Get the order's data
            SlimpayResponse orderResponse = httpClient.getOrder(partnerConfiguration, contractConfiguration, order.getReference() );

            // Fail to get order
            if (orderResponse instanceof SlimpayFailureResponse) {
                SlimpayFailureResponse slimpayOrderFailureResponse = (SlimpayFailureResponse) orderResponse;
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(truncateError(slimpayOrderFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayOrderFailureResponse))
                        .withPartnerTransactionId(transactionId)
                        .build();
            }

            // An order has been found
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
                    return this.checkPayment( partnerConfiguration, contractConfiguration, transactionId, amount, buyer, slimpayOrderResponse );

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
     * Checks the state of the payment and returns the corresponding {@link PaymentResponse}.
     *
     * @param partnerConfiguration the {@link PartnerConfiguration} data
     * @param contractConfiguration the {@link ContractConfiguration} data
     * @param transactionId the transaction ID
     * @param amount The Amount object associated to the request
     * @param buyer The Buyer object associated to the request
     * @param slimpayOrderResponse The response from Slimpay API containing the order's data
     *
     * @return The {@link PaymentResponse} representing the state of the transaction.
     */
    PaymentResponse checkPayment( PartnerConfiguration partnerConfiguration, ContractConfiguration contractConfiguration, String transactionId, Amount amount, Buyer buyer, SlimpayOrderResponse slimpayOrderResponse ){
        try {
            // Search payment
            SlimpayResponse searchPaymentResponse = httpClient.searchPayment(
                    partnerConfiguration,
                    contractConfiguration,
                    transactionId,
                    beanAssembleService.assembleMandateReference( transactionId ),
                    buyer.getCustomerIdentifier() );

            // SlimpayResponse is a failure: we were unable to get the payment's data
            if( searchPaymentResponse instanceof SlimpayFailureResponse ) {
                LOGGER.error("Unable to retrieve the payment");
                SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) searchPaymentResponse;
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(truncateError(slimpayPayoutFailureResponse.getError().toPaylineError()))
                        .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayPayoutFailureResponse))
                        .withPartnerTransactionId(transactionId)
                        .build();
            }

            // A payment was found
            SlimpayPaymentResponse slimpayPayment = (SlimpayPaymentResponse) searchPaymentResponse;
            String executionStatus = slimpayPayment.getExecutionStatus();
            String paymentId = slimpayPayment.getId();

            switch( executionStatus ){
                // Payment is rejected: we need to get the reasons
                case PaymentExecutionStatus.REJECTED:
                    LOGGER.error("Payment rejected");
                    String slimpayRejectReason  = httpClient.getPaymentRejectReason(partnerConfiguration, paymentId);
                    FailureCause failureCause  = SlimpayErrorMapper.handleSlimpayPaymentError(slimpayRejectReason);
                    return PaymentResponseFailure.PaymentResponseFailureBuilder
                            .aPaymentResponseFailure()
                            .withErrorCode(truncateError("Payment rejected, reason code: " +slimpayRejectReason))
                            .withFailureCause(failureCause)
                            .withPartnerTransactionId(transactionId)
                            .build();
                // Payment not processed
                case PaymentExecutionStatus.NOT_PROCESSED:
                    LOGGER.error(NOT_PROCESSED_PAYMENT);
                    return PaymentResponseFailure.PaymentResponseFailureBuilder
                            .aPaymentResponseFailure()
                            .withErrorCode(truncateError(NOT_PROCESSED_PAYMENT))
                            .withFailureCause(FailureCause.REFUSED)
                            .withPartnerTransactionId(transactionId)
                            .build();

                // TODO: à confirmer, décommenter si un payment ayant le Statut TO_PROCESS TO_REPLAY doit renvoyer un PaymentResponseOnHold
                /*
                case PaymentExecutionStatus.TO_PROCESS:
                    return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                            .aPaymentResponseOnHold()
                            .withPartnerTransactionId(transactionId)
                            .withOnHoldCause(OnHoldCause.SCORING_ASYNC)
                            .build();
                */

                // Else, the payment is treated as OK
                default:
                    PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.Builder
                            .aPaymentResponseSuccessAdditionalData()
                            .withOrderId(slimpayOrderResponse.getId())
                            .withOrderReference(slimpayOrderResponse.getReference())
                            .withMandateReference(beanAssembleService.assembleMandateReference(transactionId))
                            .withPaymentReference(transactionId)
                            .withPaymentId(paymentId)
                            .build();
                    return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withTransactionAdditionalData(additionalData.toString())
                            .withMessage(new Message(Message.MessageType.SUCCESS, SUCCESS_MESSAGE))
                            .withPartnerTransactionId(transactionId)
                            .withTransactionDetails(new EmptyTransactionDetails())
                            .build();
            }

        } catch (PluginTechnicalException e) {
            LOGGER.error("An error occurred while contacting the API server", e);
            return e.toPaymentResponseFailure( transactionId );
        }
    }

}


