package com.payline.payment.slimpay.services;

import com.payline.payment.slimpay.bean.SlimpayPaymentRequest;
import com.payline.payment.slimpay.bean.SlimpayPaymentResponse;
import com.payline.payment.slimpay.utils.InvalidRequestException;
import com.payline.payment.slimpay.utils.SlimpayCardConstants;
import com.payline.payment.slimpay.utils.SlimpayErrorHandler;
import com.payline.payment.slimpay.utils.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    private SlimpayHttpClient httpClient = new SlimpayHttpClient();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        try {
            // create the payment request
            SlimpayPaymentRequest request = new SlimpayPaymentRequest(paymentRequest);

            Boolean isSandbox = paymentRequest.getEnvironment().isSandbox();
            SlimpayPaymentResponse response = httpClient.initiate(request, isSandbox);

            // check response object
            if (response.getCode() != null) {
                return SlimpayErrorHandler.findError(response);
            } else {
                // get the url to get
                URL redirectURL = new URL(response.getRedirectURL());
                //get a  object which contains the url to get redirection Builder
                PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder responseRedirectURL = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder.aRedirectionRequest()
                        .withUrl(redirectURL);

                PaymentResponseRedirect.RedirectionRequest redirectionRequest = new PaymentResponseRedirect.RedirectionRequest(responseRedirectURL);
                Map<String, String> slimpayContext = new HashMap<>();
                slimpayContext.put(SlimpayCardConstants.PSC_ID, response.getId());
                RequestContext requestContext = RequestContext.RequestContextBuilder.aRequestContext()
                        .withRequestData(slimpayContext)
                        .build();

                return PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                        .withRedirectionRequest(redirectionRequest)
                        .withPartnerTransactionId(response.getId())
                        .withStatusCode(response.getStatus())
                        .withRequestContext(requestContext)
                        .build();
            }

        } catch (IOException | URISyntaxException | InvalidRequestException e) {
            logger.error("unable init the payment: {}", e.getMessage(), e);
            return SlimpayErrorHandler.getPaymentResponseFailure(FailureCause.INTERNAL_ERROR);
        }
    }
}
