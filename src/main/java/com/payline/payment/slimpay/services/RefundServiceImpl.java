package com.payline.payment.slimpay.services;

import com.payline.payment.slimpay.bean.SlimpayPaymentRequest;
import com.payline.payment.slimpay.bean.SlimpayPaymentResponse;
import com.payline.payment.slimpay.utils.InvalidRequestException;
import com.payline.payment.slimpay.utils.SlimpayCardConstants;
import com.payline.payment.slimpay.utils.SlimpayErrorHandler;
import com.payline.payment.slimpay.utils.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class RefundServiceImpl implements RefundService {
    private static final Logger LOGGER = LogManager.getLogger(RefundServiceImpl.class);

    private SlimpayHttpClient client;

    public RefundServiceImpl() {
        this.client = new SlimpayHttpClient();
    }

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
        try {
            boolean isSandbox = refundRequest.getEnvironment().isSandbox();
            SlimpayPaymentRequest request = createRequest(refundRequest);

            SlimpayPaymentResponse response = client.refund(request, isSandbox);

            if (response.getCode() != null) {
                return SlimpayErrorHandler.findRefundError(response, transactionId);
            } else if (!SlimpayCardConstants.STATUS_REFUND_SUCCESS.equals(response.getStatus())) {
                return SlimpayErrorHandler.getRefundResponseFailure(FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            updateRequest(request);
            response = client.refund(request, isSandbox);

            if (response.getCode() != null) {
                return SlimpayErrorHandler.findRefundError(response, transactionId);
            } else if (!SlimpayCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {
                return SlimpayErrorHandler.getRefundResponseFailure(FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            // refund Success
            return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                    .withStatusCode("0")
                    .withPartnerTransactionId(transactionId)
                    .build();


        } catch (InvalidRequestException | URISyntaxException | IOException e) {
            LOGGER.error("unable to refund the payment: {}" , e.getMessage(), e);
            return SlimpayErrorHandler.getRefundResponseFailure(FailureCause.CANCEL, transactionId);
        }
    }

    public SlimpayPaymentRequest createRequest(RefundRequest refundRequest) throws InvalidRequestException {
        return new SlimpayPaymentRequest(refundRequest);
    }

    public void updateRequest(SlimpayPaymentRequest request) {
        request.setCapture(true);
    }


    @Override
    public boolean canMultiple() {
        return false;
    }

    @Override
    public boolean canPartial() {
        return false;
    }
}
