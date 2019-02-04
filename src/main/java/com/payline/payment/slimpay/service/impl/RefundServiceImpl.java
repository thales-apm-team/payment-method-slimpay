package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.utils.SlimpayErrorHandler;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.Logger;

public class RefundServiceImpl implements RefundService {

    private SlimpayHttpClient httpClient;
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    public RefundServiceImpl() {
        this.httpClient = SlimpayHttpClient.getInstance();
    }

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {


        try {

            // TODO
            return null;

        } catch (Exception e) {
            // FIXME : catch the reals Exceptions
            LOGGER.error("unable init the payment", e);


            // TODO add transaction Id
            String transactionId = "";
            return SlimpayErrorHandler.geRefundResponseFailure(FailureCause.INTERNAL_ERROR, transactionId);

        }

    }

    @Override
    public boolean canMultiple() {

        // FIXME
        return false;
    }

    @Override
    public boolean canPartial() {

        // FIXME
        return false;
    }

}
