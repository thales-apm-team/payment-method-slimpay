package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.common.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayErrorHandler;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.payment.slimpay.utils.properties.constants.PaymentExecutionStatus;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import com.slimpay.hapiclient.exception.HttpClientErrorException;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.exception.HttpServerErrorException;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.PluginUtils.errorToString;
import static com.payline.payment.slimpay.utils.SlimpayErrorHandler.handleSlimpayFailureResponse;

public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();
    private TransactionManagerServiceImpl transactionManagerService = new TransactionManagerServiceImpl();

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
        String additionalData = refundRequest.getTransactionAdditionalData();
        //recuperer le paymentId
//        String paymentId = transactionManagerService.readAdditionalData(additionalData, "PaymentResponseSuccessAdditionalData").get("paymentId");

        try {
            //get order status  to cancel order if state is open ?
//            SlimpayResponse orderResponse = SlimpayHttpClient.getOrder(refundRequest);

            //Obtenir le statut du paiement a remboursser

            Payment slimpayPayoutRequest = beanAssembleService.assemblePayout(refundRequest);
            //refund a payment
            SlimpayResponse refundResponse = SlimpayHttpClient.createPayout(refundRequest, slimpayPayoutRequest.toJsonBody());
            if (refundResponse == null) {
                LOGGER.debug("refundRequest response is null !");
                LOGGER.error("Refund is null");
                return SlimpayErrorHandler.geRefundResponseFailure(FailureCause.PARTNER_UNKNOWN_ERROR, slimpayPayoutRequest.getReference(), "Empty partner response");

            } else {
                if (refundResponse.getClass() == SlimpayFailureResponse.class) {
                    SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) refundResponse;
                    return RefundResponseFailure.RefundResponseFailureBuilder
                            .aRefundResponseFailure()
                            .withErrorCode(errorToString(slimpayPayoutFailureResponse.getError()))
                            .withFailureCause(handleSlimpayFailureResponse(slimpayPayoutFailureResponse.getError()))
                            .withPartnerTransactionId(transactionId)
                            .build();
                } else {
                    SlimpayPaymentResponse slimpayRefundResponse = (SlimpayPaymentResponse) refundResponse;
                    //fixme passer la reference du payout  ou du paiement a rembourser
                    return RefundResponseSuccess.RefundResponseSuccessBuilder
                            .aRefundResponseSuccess()
                            //    .withPartnerTransactionId(slimpayRefundResponse.getReference())
                            .withPartnerTransactionId(slimpayRefundResponse.getId())
                            .withStatusCode(slimpayRefundResponse.getExecutionStatus())
                            .build();
                }

            }


        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("unable to refund  the payment");
            String errorString = e.getResponseBody();
            SlimpayError error = SlimpayError.fromJson(errorString);
            return SlimpayErrorHandler.geRefundResponseFailure(handleSlimpayFailureResponse(error), transactionId, errorToString(error));

        } catch (PluginTechnicalException | HttpException e) {
            LOGGER.error("unable to communicate with Slimpay server");
            return SlimpayErrorHandler.geRefundResponseFailure(FailureCause.COMMUNICATION_ERROR, transactionId, e.getMessage());
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


    //fixme is i ti usefull ?
    /**
     * handle payment status
     * if payment must be cancelled or refunded
     *
     * @return
     */
    public String handlePaymentStatus(RefundRequest refundRequest) {
        //if payment status = toprocess or toreplay. : cancel
        String additionalData = refundRequest.getTransactionAdditionalData();
        String paymentId = transactionManagerService.readAdditionalData(additionalData, "PaymentResponseSuccessAdditionalData").get("paymentId");
        //GetPaymentStatus
        try {
            SlimpayResponse payment = SlimpayHttpClient.getPayment(refundRequest);
            if (payment.getClass() == SlimpayPaymentResponse.class) {
                //voir statut du paiement
                SlimpayPaymentResponse paymentResponse = (SlimpayPaymentResponse) payment;
                String paiementExecutionStatus = paymentResponse.getExecutionStatus();
                if (paiementExecutionStatus.equals(PaymentExecutionStatus.TOP_PROCESS) || paiementExecutionStatus.equals(PaymentExecutionStatus.TO_REPLAY)) {
                    //todo cancel a payment but it's not work
                    //if payment status = toprocess or toreplay. : cancel  payment

                    SlimpayPaymentResponse paymentCancelled = SlimpayHttpClient.cancelPayment(refundRequest, paymentId);
                    //must be cancel
                    return  paymentCancelled.getState();
                } else {
                    //call refund request
                    //todo
                    return "to_refund";

                }

            }
            //return a paymentFailure :  npayment not found
            return null;

        } catch (PluginTechnicalException | HttpException e) {
            e.printStackTrace();
            return null;

        }

    }


}
