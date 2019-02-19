package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.Payment;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.SlimpayErrorHandler.handleSlimpayFailureResponse;
import static com.payline.payment.slimpay.utils.properties.constants.OrderStatus.CLOSED_ABORTED;

public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();


    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {

        String partnerTransactionId = refundRequest.getPartnerTransactionId();

        try {
            //Create a payment with direction from creditor to subscriber (payout)
            Payment slimpayPayoutRequest = beanAssembleService.assemblePayout(refundRequest);
            //refund a payment
            SlimpayResponse refundResponse = httpClient.createPayout(refundRequest, slimpayPayoutRequest.toJsonBody());
            if (refundResponse.getClass() == SlimpayFailureResponse.class) {
                SlimpayFailureResponse slimpayPayoutFailureResponse = (SlimpayFailureResponse) refundResponse;
                return RefundResponseFailure.RefundResponseFailureBuilder
                        .aRefundResponseFailure()
                        .withErrorCode(slimpayPayoutFailureResponse.getError().toPaylineError())
                        .withFailureCause(handleSlimpayFailureResponse(slimpayPayoutFailureResponse.getError()))
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

//note Rel not accessible
    public RefundResponse cancelPayment(RefundRequest refundRequest) throws PluginTechnicalException {

        //recuperer orderId ou pas ?
        SlimpayResponse slimpayResponse = httpClient.cancelPayment(refundRequest);

        if (SlimpayOrderResponse.class.equals(slimpayResponse.getClass())) {

            SlimpayOrderResponse orderResponse = (SlimpayOrderResponse) slimpayResponse;
            if ((orderResponse.getState().contains(CLOSED_ABORTED))) {
                return RefundResponseSuccess.RefundResponseSuccessBuilder
                        .aRefundResponseSuccess()
                        //    .withPartnerTransactionId(slimpayRefundResponse.getReference())
                        .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                        .withStatusCode(orderResponse.getState())
                        .build();

            } else {
                // fix me CancelFail
                return RefundResponseFailure.RefundResponseFailureBuilder
                        .aRefundResponseFailure()
                        .withErrorCode("Unable to cancel the payment")
                        .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                        .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                        .build();
            }

        }
    else{
            LOGGER.error("unable to cancel the payment");
            SlimpayFailureResponse orderErrror = (SlimpayFailureResponse) slimpayResponse;
            return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                    .withFailureCause(handleSlimpayFailureResponse(orderErrror.getError()))
                    .withErrorCode(orderErrror.getError().toPaylineError())
                    .withPartnerTransactionId(refundRequest.getPartnerTransactionId())
                    .build();
        }
    }

}