package com.payline.payment.slimpay.service.impl;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

import static com.payline.payment.slimpay.utils.SlimpayErrorHandler.getPaymentResponseFailure;


public class PaymentServiceImpl implements PaymentService {


    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);


    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        try {
            // TODO
            return null;

        } catch (Exception e) {
            // FIXME : catch the reals Exceptions
            LOGGER.error("unable init the payment", e);
            return getPaymentResponseFailure(FailureCause.INTERNAL_ERROR);
        }

    }

}
