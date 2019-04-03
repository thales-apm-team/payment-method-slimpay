package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.payment.slimpay.utils.SlimpayErrorMapper;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import com.slimpay.hapiclient.http.JsonBody;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.payline.payment.slimpay.utils.PluginUtils.truncateError;


public class PaymentServiceImpl implements PaymentService {


    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();
    private SlimpayHttpClient httpClient = SlimpayHttpClient.getInstance();

    /**
     * Execute a paymentRequest
     *
     * @param paymentRequest
     * @return
     */
    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {

        SlimpayOrderRequest slimpayOrderRequest = null;
        try {
            slimpayOrderRequest = beanAssembleService.assembleSlimPayOrderRequest(paymentRequest);
        } catch (InvalidDataException e) {
            LOGGER.error("Unable to build a orderRequest {}", e);
            return e.toPaymentResponseFailure();
        }
        //make order request body
        JsonBody jsonOrderRequest = slimpayOrderRequest.toJsonBody();
        try {
            //Initialise order
            SlimpayResponse slimpayOrderResponse = httpClient.createOrder(paymentRequest.getPartnerConfiguration(), jsonOrderRequest);
            if (slimpayOrderResponse == null) {
                LOGGER.debug("createOrder response is null !");
                LOGGER.error("Payment is null");
                return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                        .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                        .withPartnerTransactionId(slimpayOrderRequest.getReference())
                        .withErrorCode("Empty partner response")
                        .build();
            }
            //return  a paymentResponseRedirect
            else {
                if (slimpayOrderResponse instanceof SlimpayFailureResponse) {
                    // payment Failed return  a PaymentResponseFailure with Slimpay error
                    SlimpayFailureResponse slimpayOrderFailureResponse = (SlimpayFailureResponse) slimpayOrderResponse;
                    return PaymentResponseFailure.PaymentResponseFailureBuilder
                            .aPaymentResponseFailure()
                            .withErrorCode(truncateError(slimpayOrderFailureResponse.getError().toPaylineError()))
                            .withFailureCause(SlimpayErrorMapper.handleSlimpayError(slimpayOrderFailureResponse))
                            .withPartnerTransactionId(slimpayOrderRequest.getReference())
                            .build();
                } else {
                    try {
                        // payment Successed, return a paymentResponseRedirect With a confirmation url
                        SlimpayOrderResponse slimpayOrderSuccessResponse = (SlimpayOrderResponse) slimpayOrderResponse;
                        URL redirectURL = new URL(slimpayOrderSuccessResponse.getUrlApproval());
                        PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder responseRedirectURL = PaymentResponseRedirect.RedirectionRequest
                                .RedirectionRequestBuilder.aRedirectionRequest()
                                .withUrl(redirectURL);
                        Map<String, String> oneyContext = new HashMap<>();
                        oneyContext.put(SlimpayConstants.CREDITOR_REFERENCE_KEY, slimpayOrderRequest.getCreditor().getReference());
                        oneyContext.put(SlimpayConstants.ORDER_REFERENCE, slimpayOrderSuccessResponse.getReference());
                        oneyContext.put(SlimpayConstants.ORDER_ID, slimpayOrderSuccessResponse.getId());

                        PaymentResponseRedirect.RedirectionRequest redirectionRequest = new PaymentResponseRedirect.RedirectionRequest(responseRedirectURL);
                        RequestContext requestContext = RequestContext.RequestContextBuilder.aRequestContext()
                                .withRequestData(oneyContext)
                                .build();
                        return PaymentResponseRedirect.PaymentResponseRedirectBuilder.aPaymentResponseRedirect()
                                .withRedirectionRequest(redirectionRequest)
                                .withPartnerTransactionId(slimpayOrderRequest.getReference())
                                .withStatusCode(slimpayOrderSuccessResponse.getState())
                                .withRequestContext(requestContext)
                                .build();
                    } catch (MalformedURLException e) {
                        throw new HttpCallException(e, "SlimpayOrderResponse.getUrlApproval");
                    }
                }

            }

        } catch (PluginTechnicalException e) {
            LOGGER.error("unable call partner", e);
            return e.toPaymentResponseFailure();
        }

    }


}
