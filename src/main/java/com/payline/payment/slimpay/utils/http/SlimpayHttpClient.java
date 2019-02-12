package com.payline.payment.slimpay.utils.http;

import com.payline.payment.slimpay.bean.common.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.common.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.service.impl.RequestConfigServiceImpl;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.logger.LogManager;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.hal.CustomRel;
import com.slimpay.hapiclient.hal.Resource;
import com.slimpay.hapiclient.http.Follow;
import com.slimpay.hapiclient.http.HapiClient;
import com.slimpay.hapiclient.http.JsonBody;
import com.slimpay.hapiclient.http.Method;
import com.slimpay.hapiclient.http.auth.Oauth2BasicAuthentication;
import org.apache.logging.log4j.Logger;

public class SlimpayHttpClient {
    private static final Logger LOGGER = LogManager.getLogger(SlimpayHttpClient.class);
    private static final String EMPTY_RESPONSE_MESSAGE = "response is empty";
    private static final String USER_APPROVAL = "#user-approval";
    private static final String CREDITOR_REFERENCE = "creditorReference";
    private static final String REFERENCE = "reference";

    private SlimpayHttpClient() {
        // ras.
    }

    /**
     * Call the SlimPay API using the Slimpay hapiclient
     *
     * @param url            the API url
     * @param profile        the profile URL
     * @param authentication
     * @param follow
     * @return
     * @throws HttpException
     */
    private static Resource request(String url, String profile, Oauth2BasicAuthentication authentication, Follow follow) throws HttpException {
        final long start = System.currentTimeMillis();
        int count = 0;
        Resource response = null;
        HttpException exception = null;

        while (count < 3 && response == null) {
            try {
                LOGGER.info("Start partner call... [URL: {}]", url);

                HapiClient client = new HapiClient.Builder()
                        .setApiUrl(url)
                        .setProfile(profile)
                        .setAuthenticationMethod(authentication)
                        .build();

                response = client.send(follow);

                final long end = System.currentTimeMillis();
                LOGGER.info("End partner call [T: {}ms] [CODE: {}]", end - start); // ajouter le code de reponse (200)
            } catch (HttpException e) {
                exception = e;
                response = null;
            } finally {
                count++;
            }
        }

        if (exception != null) {
            throw exception;
        }

        return response;
    }

    /**
     * create the request to call a #create-orders http request
     *
     * @param request the payline request
     * @param body    the body of the http request
     * @throws InvalidDataException
     * @throws HttpException
     */
    public static SlimpayOrderResponse testConnection(ContractParametersCheckRequest request, JsonBody body) throws PluginTechnicalException, HttpException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.CREATE_ORDER_URL);

        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = request(url, profile, authentication, follow);
        if (response != null) {
            return SlimpayOrderResponse.fromJson(response.getState().toString());
        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.testConnection");
        }

    }


    /**
     * create the request to call a #create-orders http request
     *
     * @param request the payline request
     * @param body    the body of the http request
     * @throws InvalidDataException
     * @throws HttpException
     */
    public static SlimpayResponse createOrder(PaymentRequest request, JsonBody body) throws PluginTechnicalException, HttpException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.CREATE_ORDER_URL);

        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = request(url, profile, authentication, follow);


        if (response != null) {

            if(response.getState()!=null) {
                LOGGER.info("Order created");
                SlimpayOrderResponse orderSuccessResponse = SlimpayOrderResponse.fromJson(response.getState().toString());
                //add confirm url on the response
                String confirmationUrl = response.getLink(new CustomRel(ns + USER_APPROVAL)).getHref();
                orderSuccessResponse.setUrlApproval(confirmationUrl);
                return orderSuccessResponse;

            }
            else {
                //return a Failure response
                LOGGER.info("Fail to create the order");
                return SlimpayFailureResponse.fromJson(response.toString());

            }

        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.createOrder");

        }

    }

    /**
     * create the request to call a #create-payout http request
     *
     * @param request the payline request
     * @param body    the body of the http request
     * @throws InvalidDataException
     * @throws HttpException
     */
    public static SlimpayPaymentResponse createPayout(RefundRequest request, JsonBody body) throws PluginTechnicalException, HttpException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.CREATE_PAYOUT_URL);

        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = request(url, profile, authentication, follow);
        if (response != null) {
            return SlimpayPaymentResponse.Builder.fromJson(response.getState().toString());
        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.createPayout");
        }
    }


    /**
     * create the request to call a #get-payment http request
     *
     * @param request the payline request
     * @throws InvalidDataException
     * @throws HttpException
     */
    public static SlimpayPaymentResponse getPayment(RedirectionPaymentRequest request) throws PluginTechnicalException, HttpException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        CustomRel rel = new CustomRel(SlimpayConstants.GET_PAYMENT_URL);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_KEY);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .build();

        Resource response = request(url, profile, authentication, follow);
        if (response != null) {
            return SlimpayPaymentResponse.Builder.fromJson(response.getState().toString());
        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getPayment");
        }
    }

    /**
     * create the request to call a #get-payment http request
     *
     * @param request the payline RefundRequest
     * @throws InvalidDataException
     * @throws HttpException
     */
    public static SlimpayPaymentResponse getPayment(RefundRequest request) throws PluginTechnicalException, HttpException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_PAYMENT_URL);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .build();

        Resource response = request(url, profile, authentication, follow);
        if (response != null) {
            return SlimpayPaymentResponse.Builder.fromJson(response.getState().toString());
        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getPayment");
        }
    }

    /**
     * create the request to call a #get-order http request
     *
     * @param request the payline request
     * @throws InvalidDataException
     * @throws HttpException
     */
    public static SlimpayOrderResponse getOrder(RedirectionPaymentRequest request) throws PluginTechnicalException, HttpException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_ORDER_URL);

        //creditor references
        String creditorReference = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.CREDITOR_REFERENCE_KEY);
        //Order references
        String reference = request.getTransactionId();

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE,creditorReference)
                .setUrlVariable(REFERENCE,reference)
                .build();

        Resource response = request(url, profile, authentication, follow);

        if (response != null) {
            return SlimpayOrderResponse.fromJson(response.getState().toString());
        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getOrder");
        }
    }

    /**
     * create the Oauth2BasicAuthentication object needed by the Slimpay hapiclient
     *
     * @param request the payline PaymentRequest containing all needed data
     * @throws InvalidDataException
     */
    private static Oauth2BasicAuthentication createAuthentication(ContractParametersCheckRequest request) throws InvalidDataException {
        return new Oauth2BasicAuthentication.Builder()
                .setTokenEndPointUrl(SlimpayConstants.TOKEN_ENDPOINT)
                .setUserid(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_KEY))
                .setPassword(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_SECRET))
                .build();
    }

    /**
     * create the Oauth2BasicAuthentication object needed by the Slimpay hapiclient
     *
     * @param request the payline PaymentRequest containing all needed data
     * @throws InvalidDataException
     */
    private static Oauth2BasicAuthentication createAuthentication(PaymentRequest request) throws InvalidDataException {
        return new Oauth2BasicAuthentication.Builder()
                .setTokenEndPointUrl(SlimpayConstants.TOKEN_ENDPOINT)
                .setUserid(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_KEY))
                .setPassword(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_SECRET))
                .build();
    }

    /**
     * create the Oauth2BasicAuthentication object needed by the Slimpay hapiclient
     *
     * @param request the payline RefundRequest containing all needed data
     * @throws InvalidDataException
     */
    private static Oauth2BasicAuthentication createAuthentication(RefundRequest request) throws InvalidDataException {
        return new Oauth2BasicAuthentication.Builder()
                .setTokenEndPointUrl(SlimpayConstants.TOKEN_ENDPOINT)
                .setUserid(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_KEY))
                .setPassword(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_SECRET))
                .build();
    }
}
