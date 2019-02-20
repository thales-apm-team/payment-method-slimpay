package com.payline.payment.slimpay.utils.http;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.exception.SlimpayHttpException;
import com.payline.payment.slimpay.service.impl.RequestConfigServiceImpl;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;

public class SlimpayHttpClient {
    private static final Logger LOGGER = LogManager.getLogger(SlimpayHttpClient.class);
    private static HttpClientBuilder httpClientBuilder ;

    private static final String EMPTY_RESPONSE_MESSAGE = "response is empty";
    private static final String USER_APPROVAL = "#user-approval";
    private static final String CREDITOR_REFERENCE = "creditorReference";
    private static final String REFERENCE = "reference";
    private static final String ORDER_FOUND_MESSAGE = "Order found";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Fail to find the order";

    /**
     * Instantiate a HTTP client with default values.
     */


    private SlimpayHttpClient() {
        //Define Client builder used in every Request to Slimpay
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2 * 1000)
                .setConnectionRequestTimeout(3 * 1000)
                .setSocketTimeout(4 * 1000).build();

        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier()));

    }

    /**
     * Singleton Holder
     */
    private static class SingletonHolder {
        private static final SlimpayHttpClient INSTANCE = new SlimpayHttpClient();
    }

    /**
     * @return the singleton instance
     */
    public static SlimpayHttpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Call the SlimPay API using the Slimpay hapiclient
     *
     * @param url            the API url
     * @param profile        the profile URL
     * @param authentication
     * @param follow
     * @return
     * @throws SlimpayHttpException
     */
    private Resource request(String url, String profile, Oauth2BasicAuthentication authentication, Follow follow) throws SlimpayHttpException {
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
                        .setClientBuilder(httpClientBuilder)
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
            throw new SlimpayHttpException(exception);
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
    public SlimpayOrderResponse testConnection(ContractParametersCheckRequest request, JsonBody body) throws PluginTechnicalException {
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
     * @throws PluginTechnicalException
     */
    public SlimpayResponse createOrder(PaymentRequest request, JsonBody body) throws PluginTechnicalException {
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

            if (response.getState() != null) {
                LOGGER.info("Order created");
                SlimpayOrderResponse orderSuccessResponse = SlimpayOrderResponse.fromJson(response.getState().toString());
                //add confirm url on the response
                String confirmationUrl = response.getLink(new CustomRel(ns + USER_APPROVAL)).getHref();
                orderSuccessResponse.setUrlApproval(confirmationUrl);
                return orderSuccessResponse;

            } else {
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
     * @throws PluginTechnicalException
     */
    public SlimpayResponse createPayout(RefundRequest request, JsonBody body) throws PluginTechnicalException {
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

            if (response.getState() != null) {
                return SlimpayPaymentResponse.fromJson(response.getState().toString());
            } else {//return a Failure response
                LOGGER.info("Fail to create the payout");
                return SlimpayFailureResponse.fromJson(response.toString());

            }

        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.createPayout");
        }
    }

    /**
     * create the request to call a #get-orders http request
     *
     * @param request the payline request
     * @throws PluginTechnicalException
     */
    public SlimpayResponse getOrder(RedirectionPaymentRequest request) throws PluginTechnicalException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_ORDER_URL);

        //creditor reference
        String creditorReference = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.CREDITOR_REFERENCE_KEY);
        //Order reference
        String reference = request.getTransactionId();

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, creditorReference)
                .setUrlVariable(REFERENCE, reference)
                .build();

        Resource response = request(url, profile, authentication, follow);

        if (response != null) {
            if (response.getState() != null) {
                LOGGER.info(ORDER_FOUND_MESSAGE);
                return SlimpayOrderResponse.fromJson(response.getState().toString());
            } else {
                //return a Failure response
                LOGGER.info(ORDER_NOT_FOUND_MESSAGE);
                return SlimpayFailureResponse.fromJson(response.toString());
            }

        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getOrder");
        }
    }


    /**
     * create the request to call a #get-order http request
     *
     * @param request the payline request
     * @throws PluginTechnicalException
     */
    public SlimpayResponse getOrder(TransactionStatusRequest request) throws PluginTechnicalException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_ORDER_URL);

        //creditor reference
        String creditorReference = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.CREDITOR_REFERENCE_KEY);
        //Order reference
        String reference = request.getTransactionId();

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, creditorReference)
                .setUrlVariable(REFERENCE, reference)
                .build();

        Resource response = request(url, profile, authentication, follow);

        if (response != null) {
            if (response.getState() != null) {
                LOGGER.info(ORDER_FOUND_MESSAGE);
                return SlimpayOrderResponse.fromJson(response.getState().toString());
            } else {
                //return a Failure response
                LOGGER.info(ORDER_NOT_FOUND_MESSAGE);
                return SlimpayFailureResponse.fromJson(response.toString());
            }
        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getOrder");
        }
    }

    /**
     * create the request to call a #get-order http request
     *
     * @param request the payline request
     * @throws PluginTechnicalException
     */
    public SlimpayResponse getOrder(RefundRequest request) throws PluginTechnicalException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_ORDER_URL);

        //creditor reference
        String creditorReference = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.CREDITOR_REFERENCE_KEY);
        //Order reference
        String reference = request.getTransactionId();

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, creditorReference)
                .setUrlVariable(REFERENCE, reference)
                .build();

        Resource response = request(url, profile, authentication, follow);

        if (response != null) {
            if (response.getState() != null) {
                LOGGER.info(ORDER_FOUND_MESSAGE);
                return SlimpayOrderResponse.fromJson(response.getState().toString());
            } else {
                //return a Failure response
                LOGGER.info(ORDER_NOT_FOUND_MESSAGE);
                return SlimpayFailureResponse.fromJson(response.toString());
            }

        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getOrder");
        }
    }

    //fixme cancel jamais autorisé
    public SlimpayResponse cancelPayment(RefundRequest request) throws PluginTechnicalException {


        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + "#cancel-payment");

        //creditor reference
        String creditorReference = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.CREDITOR_REFERENCE_KEY);
        //Order reference
        String reference = request.getTransactionId();

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, creditorReference)
                .setUrlVariable(REFERENCE, reference)
                .build();

        Resource response = request(url, profile, authentication, follow);

        if (response != null) {
            if (response.getState() != null) {
                return SlimpayPaymentResponse.fromJson(response.getState().toString());
            } else {//return a Failure response
                LOGGER.info("Fail to cancel the payment");
                return SlimpayFailureResponse.fromJson(response.toString());

            }
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
    private Oauth2BasicAuthentication createAuthentication(ContractParametersCheckRequest request) throws InvalidDataException {
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
    private Oauth2BasicAuthentication createAuthentication(PaymentRequest request) throws InvalidDataException {
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
    private Oauth2BasicAuthentication createAuthentication(RefundRequest request) throws InvalidDataException {
        return new Oauth2BasicAuthentication.Builder()
                .setTokenEndPointUrl(SlimpayConstants.TOKEN_ENDPOINT)
                .setUserid(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_KEY))
                .setPassword(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_SECRET))
                .build();
    }

    /**
     * create the Oauth2BasicAuthentication object needed by the Slimpay hapiclient
     *
     * @param request the payline TransactionStatusRequest containing all needed data
     * @throws InvalidDataException
     */
    private Oauth2BasicAuthentication createAuthentication(TransactionStatusRequest request) throws InvalidDataException {
        return new Oauth2BasicAuthentication.Builder()
                .setTokenEndPointUrl(SlimpayConstants.TOKEN_ENDPOINT)
                .setUserid(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_KEY))
                .setPassword(RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.APP_SECRET))
                .build();
    }
}
