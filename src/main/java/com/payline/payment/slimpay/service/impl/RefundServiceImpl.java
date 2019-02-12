package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.common.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayErrorHandler;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
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


    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
        try {

            //Obtenir le statu du paiement a remboursser

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
                    //todo create a refundResponseSuccess
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

    /**
     * handle payment status
     * if payment must be cancelled or refunded
     * @return
     */
    public  String handlePaymentStatus(RefundRequest refundRequest){
        //todo
        //if payment status = toprocess or toreplay. : cancel
        //else do refund
        return null;
    }

}
