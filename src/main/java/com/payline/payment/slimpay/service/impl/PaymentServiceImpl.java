package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.bean.common.SlimpayError;
import com.payline.payment.slimpay.bean.common.request.SlimpayOrderRequest;
import com.payline.payment.slimpay.bean.common.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.payment.slimpay.utils.SlimpayErrorHandler;
import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import com.slimpay.hapiclient.exception.HttpClientErrorException;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.exception.HttpServerErrorException;
import com.slimpay.hapiclient.http.JsonBody;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.payline.payment.slimpay.utils.PluginUtils.errorToString;
import static com.payline.payment.slimpay.utils.SlimpayErrorHandler.handleSlimpayFailureResponse;


public class PaymentServiceImpl implements PaymentService {


    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private BeanAssemblerServiceImpl beanAssembleService = BeanAssemblerServiceImpl.getInstance();


    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {

        SlimpayOrderRequest slimpayOrderRequest = beanAssembleService.assembleSlimPayOrderRequest(paymentRequest);
        //make order request body
        JsonBody jsonOrderRequest = slimpayOrderRequest.toJsonBody();
        try {
            //Initialise order
            SlimpayResponse slimpayOrderResponse = SlimpayHttpClient.createOrder(paymentRequest, jsonOrderRequest);
            if (slimpayOrderResponse == null) {
                LOGGER.debug("createOrder response is null !");
                LOGGER.error("Payment is null");
                return SlimpayErrorHandler.getPaymentResponseFailure(
                        FailureCause.PARTNER_UNKNOWN_ERROR,
                        slimpayOrderRequest.getReference(),
                        "Empty partner response");
            }

            //return  a paymentResponseRedirect
            else {
                if(slimpayOrderResponse.getClass() == SlimpayFailureResponse.class)
                {
                    SlimpayFailureResponse  slimpayOrderFailureResponse = (SlimpayFailureResponse) slimpayOrderResponse;
                    return PaymentResponseFailure.PaymentResponseFailureBuilder
                            .aPaymentResponseFailure()
                            .withErrorCode(errorToString(slimpayOrderFailureResponse.getError()))
                            .withFailureCause(handleSlimpayFailureResponse(slimpayOrderFailureResponse.getError()))
                            .withPartnerTransactionId(slimpayOrderRequest.getReference())
                            .build();
                }
                else{
                    SlimpayOrderResponse slimpayOrderSuccessResponse = (SlimpayOrderResponse)slimpayOrderResponse;
                    URL redirectURL = new URL(slimpayOrderSuccessResponse.getUrlApproval());
                    PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder responseRedirectURL = PaymentResponseRedirect.RedirectionRequest
                            .RedirectionRequestBuilder.aRedirectionRequest()
                            .withUrl(redirectURL);
                    Map<String, String> oneyContext = new HashMap<>();
                    //todo ajouter request context
                    oneyContext.put(SlimpayConstants.CREDITOR_REFERENCE_KEY, slimpayOrderRequest.getCreditor().getReference());
                    //order reference
                    //mandate reference
                    //payment reference
                    //state
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
                }

            }


        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("unable to  init the payment", e);
            //createSlimpayError
            String errorString = e.getResponseBody();
            SlimpayError error = SlimpayError.fromJson(errorString);

            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(errorToString(error))
                    .withFailureCause(handleSlimpayFailureResponse(error))
                    .withPartnerTransactionId(slimpayOrderRequest.getReference())
                    .build();

        }  catch (HttpException | PluginTechnicalException | MalformedURLException e) {
            LOGGER.error("unable call partner", e);
            return SlimpayErrorHandler.getPaymentResponseFailure(
                    FailureCause.COMMUNICATION_ERROR,
                    slimpayOrderRequest.getReference(),
                    e.getMessage());
        }

    }


}
