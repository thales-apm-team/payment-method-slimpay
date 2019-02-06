package com.payline.payment.slimpay.service.impl;


import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);


    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {

        try {
            // TODO
            return null;

        } catch (Exception e) {
            // FIXME : catch the reals Exceptions
            LOGGER.error("unable to confirm the payment", e);

            // TODO add transaction Id
            String transactionId = "";
            return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                    .withFailureCause(FailureCause.COMMUNICATION_ERROR)
                    .withErrorCode("503")
                    .withPartnerTransactionId(transactionId)
                    .build();
        }

    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {

        try {

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
