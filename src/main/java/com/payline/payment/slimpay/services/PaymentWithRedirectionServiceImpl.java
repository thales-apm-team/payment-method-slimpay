package com.payline.payment.slimpay.services;

import com.payline.payment.slimpay.bean.SlimpayCaptureRequest;
import com.payline.payment.slimpay.bean.SlimpayPaymentResponse;
import com.payline.payment.slimpay.utils.InvalidRequestException;
import com.payline.payment.slimpay.utils.SlimpayCardConstants;
import com.payline.payment.slimpay.utils.SlimpayErrorHandler;
import com.payline.payment.slimpay.utils.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.Card;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.CardPayment;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.YearMonth;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    private static final Logger logger = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);

    private SlimpayHttpClient httpClient;

    public PaymentWithRedirectionServiceImpl() {
        httpClient = new SlimpayHttpClient();
    }

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        try {
            SlimpayCaptureRequest request = createRequest(redirectionPaymentRequest);
            boolean isSandbox = redirectionPaymentRequest.getEnvironment().isSandbox();

            // first try
            PaymentResponse response = validatePayment(request, isSandbox);
            if (PaymentResponseSuccess.class.equals(response.getClass())) {
                return response;
            } else {
                // second try
                return validatePayment(request, isSandbox);
            }

        } catch (InvalidRequestException e) {
            logger.error("unable to finalize the payment: {}", e.getMessage(), e);
            return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.INTERNAL_ERROR);
        }
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        try {
            SlimpayCaptureRequest request = createRequest(transactionStatusRequest);
            boolean isSandbox = transactionStatusRequest.getEnvironment().isSandbox();

            return validatePayment(request, isSandbox);
        } catch (InvalidRequestException e) {
            logger.error("unable to handle the session expiration: {}", e.getMessage(), e);
            return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.INVALID_DATA);
        }
    }

    /**
     * Used for test (mocking)
     *
     * @param transactionStatusRequest
     * @return
     * @throws InvalidRequestException
     */
    public SlimpayCaptureRequest createRequest(TransactionStatusRequest transactionStatusRequest) throws InvalidRequestException {
        return new SlimpayCaptureRequest(transactionStatusRequest);
    }

    /**
     * Used for test (mocking)
     *
     * @param redirectionPaymentRequest
     * @return
     * @throws InvalidRequestException
     */
    public SlimpayCaptureRequest createRequest(RedirectionPaymentRequest redirectionPaymentRequest) throws InvalidRequestException {
        return new SlimpayCaptureRequest(redirectionPaymentRequest);
    }

    private PaymentResponse getErrorFromStatus(String status) {
        switch (status) {
            case SlimpayCardConstants.STATUS_CANCELED_CUSTOMER:
                return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.CANCEL);
            case SlimpayCardConstants.STATUS_CANCELED_MERCHANT:
                return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.CANCEL);
            case SlimpayCardConstants.STATUS_EXPIRED:
                return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.SESSION_EXPIRED);
            default:
                return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.PARTNER_UNKNOWN_ERROR);
        }
    }

    private PaymentResponseSuccess createResponseSuccess(SlimpayPaymentResponse response) {
        Card card = Card.CardBuilder.aCard()
                .withPan(response.getFirstCardDetails().getSerial())
                .withExpirationDate(YearMonth.now())
                .build();

        CardPayment cardPayment = CardPayment.CardPaymentBuilder.aCardPayment()
                .withCard(card)
                .build();

        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                .withStatusCode("0")
                .withPartnerTransactionId(response.getId())
                .withTransactionDetails(cardPayment)
                .build();
    }

    private PaymentResponse validatePayment(SlimpayCaptureRequest request, boolean isSandbox) {
        try {
            // retrieve payment data
            SlimpayPaymentResponse response = httpClient.retrievePaymentData(request, isSandbox);
            if (response.getCode() != null) {
                return SlimpayErrorHandler.findError(response);
            } else {
                // check if the payment has to be captured
                if (SlimpayCardConstants.STATUS_AUTHORIZED.equals(response.getStatus())) {
                    response = httpClient.capture(request, isSandbox);
                }

                if (response.getCode() != null) {
                    return SlimpayErrorHandler.findError(response);
                }
                // check if the payment is well captured
                if (SlimpayCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {
                    return createResponseSuccess(response);
                } else {
                    return getErrorFromStatus(response.getStatus());
                }
            }
        } catch (IOException | URISyntaxException e) {
            logger.error("unable to validate the payment: {}", e.getMessage(), e);
            return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.COMMUNICATION_ERROR);
        }
    }
}
