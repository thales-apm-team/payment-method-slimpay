package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
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
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();


    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
//        String additionalData = refundRequest.getTransactionAdditionalData();
        //recuperer le paymentId
//        String paymentId = transactionManagerService.readAdditionalData(additionalData, "PaymentResponseSuccessAdditionalData").get("paymentId");

        try {
            //todo get order status  to cancel order if state is open ?
//            SlimpayResponse orderResponse = SlimpayHttpClient.getOrder(refundRequest);

            //Obtenir le statut du paiement a remboursser

            //Create a payment with direction from creditor to subscriber (payout)
            Payment slimpayPayoutRequest = beanAssembleService.assemblePayout(refundRequest);
            //refund a payment
            SlimpayResponse refundResponse = httpClient.createPayout(refundRequest, slimpayPayoutRequest.toJsonBody());
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
                return RefundResponseSuccess.RefundResponseSuccessBuilder
                        .aRefundResponseSuccess()
                        //    .withPartnerTransactionId(slimpayRefundResponse.getReference())
                        .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                        .withStatusCode(slimpayRefundResponse.getExecutionStatus())
                        .build();
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

}
