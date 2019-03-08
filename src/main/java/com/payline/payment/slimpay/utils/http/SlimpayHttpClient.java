package com.payline.payment.slimpay.utils.http;

import com.payline.payment.slimpay.bean.response.*;
import com.payline.payment.slimpay.exception.*;
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
import java.util.List;

public class SlimpayHttpClient {
    private static final Logger LOGGER = LogManager.getLogger(SlimpayHttpClient.class);
    private static HttpClientBuilder httpClientBuilder;

    private static final String EMPTY_RESPONSE_MESSAGE = "response is empty";
    private static final String USER_APPROVAL = "#user-approval";
    private static final String CREDITOR_REFERENCE = "creditorReference";
    private static final String REFERENCE = "reference";
    private static final String ORDER_FOUND_MESSAGE = "Order found";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Fail to find the order";
    private static final String PAYMENT_FOUND_MESSAGE = "Payment found";
    private static final String PAYMENT_NOT_FOUND_MESSAGE = "Fail to find the payment";
    private static final String ID = "id";
    private static final String SUBSCRIBER_REFERENCE = "subcriberReference";
    private static final String MANDATE_REFERENCE = "mandateReference";
    private static final String SCHEME = "scheme";
    private static final String DIRECTION = "direction";
    private static final String CURRENCY = "direction";

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
     * Build the HttpClient and Call the SlimPay API using the Slimpay hapiclient
     *
     * @param url            the API url
     * @param profile        the profile URL
     * @param authentication
     * @param follow
     * @return
     * @throws SlimpayHttpException
     */
    private Resource request(String url, String profile, Oauth2BasicAuthentication authentication, Follow follow) throws SlimpayHttpException {
        HapiClient client = new HapiClient.Builder()
                .setApiUrl(url)
                .setProfile(profile)
                .setAuthenticationMethod(authentication)
                .setClientBuilder(httpClientBuilder)
                .build();
        return sendRequest(client, follow, url);
    }

    /**
     * Build the HttpClient and Call the SlimPay API using the Slimpay hapiclient
     *
     * @param url
     * @param profile
     * @param authentication
     * @param follow
     * @param entryPoint     entryPoint of the http request
     * @return
     * @throws SlimpayHttpException
     */
    private Resource request(String url, String profile, Oauth2BasicAuthentication authentication, Follow follow, String entryPoint) throws SlimpayHttpException {

        HapiClient client = new HapiClient.Builder()
                .setApiUrl(url)
                .setProfile(profile)
                .setAuthenticationMethod(authentication)
                .setClientBuilder(httpClientBuilder)
                .setEntryPointUrl(entryPoint)
                .build();

        return sendRequest(client, follow, url);
    }

    /**
     * Execute the Http call
     *
     * @param client
     * @param follow
     * @param url
     * @return
     * @throws SlimpayHttpException
     */
    private Resource sendRequest(HapiClient client, Follow follow, String url) throws SlimpayHttpException {
        final long start = System.currentTimeMillis();
        int count = 0;
        Resource response = null;
        HttpException exception = null;

        while (count < 3 && response == null) {
            try {
                LOGGER.info("Start partner call... [URL: {}]", url);

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


        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.createOrder");

        }

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
        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.createPayout");
        }

        if (response.getState() != null) {
            return SlimpayPaymentResponse.fromJson(response.getState().toString());
        } else {//return a Failure response
            LOGGER.info("Fail to create the payout");
            return SlimpayFailureResponse.fromJson(response.toString());

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

        return getSlimpayResponse(response);

    }

    private SlimpayResponse getSlimpayResponse(Resource response) throws PluginTechnicalException {
        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getOrder");
        }

        if (response.getState() != null) {
            LOGGER.info(ORDER_FOUND_MESSAGE);
            return SlimpayOrderResponse.fromJson(response.getState().toString());
        } else {
            //return a Failure response
            LOGGER.info(ORDER_NOT_FOUND_MESSAGE);
            return SlimpayFailureResponse.fromJson(response.toString());
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

        return getSlimpayResponse(response);
    }

    /**
     * Find a Slimpay Payment from its id
     *
     * @param request a Payline RefundRequest
     * @return A Slimpay payment
     * @throws PluginTechnicalException
     */
    public SlimpayResponse getPayment(RefundRequest request) throws PluginTechnicalException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_PAYMENT_URL);

        //get paymentId from transactionAdditionalData
        PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.fromJson(request.getTransactionAdditionalData());
        String paymentId = additionalData.getPaymentId();

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(ID, paymentId)
                .build();

        Resource response = request(url, profile, authentication, follow);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getPayment");
        }
        if (response.getState() != null) {
            LOGGER.info(ORDER_FOUND_MESSAGE);
            return SlimpayPaymentResponse.fromJson(response.getState().toString());
        } else {
            //return a Failure response
            LOGGER.info(ORDER_NOT_FOUND_MESSAGE);
            return SlimpayFailureResponse.fromJson(response.toString());
        }

    }

    /**
     * Search a payment a payment from a TransactionStatus request
     * This method is useful to get payment id
     *
     * @param request
     * @return
     * @throws PluginTechnicalException
     */
    public SlimpayResponse searchPayment(TransactionStatusRequest request) throws PluginTechnicalException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.SEARCH_PAYMENT_URL);

        //get paymentId from transactionAdditionalData
        String subscriber = request.getBuyer().getCustomerIdentifier();
        //creditor reference
        String creditorReference = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.CREDITOR_REFERENCE_KEY);
        String scheme = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.MANDATE_PAYIN_SCHEME);
        //Order reference
        String mandateReference = request.getTransactionId();
        String paymentReference = request.getOrder().getReference();
        String currency = request.getAmount().getCurrency().toString();
        CustomRel relPayment = new CustomRel("payments");

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, creditorReference)
                .setUrlVariable(SUBSCRIBER_REFERENCE, subscriber)
                .setUrlVariable(MANDATE_REFERENCE, mandateReference)
                .setUrlVariable(REFERENCE, paymentReference)
                .setUrlVariable(CURRENCY, currency)
                .setUrlVariable(SCHEME, scheme)
                .setUrlVariable(DIRECTION, "IN")
                .build();


        Resource response = request(url, profile, authentication, follow);

        return getSlimpayResponse(relPayment, response);
    }


    /**
     * Get a payment from a redirection request
     * This method is useful to get payment id
     *
     * @param request
     * @return
     * @throws PluginTechnicalException
     */
    public SlimpayResponse searchPayment(RedirectionPaymentRequest request) throws PluginTechnicalException {
        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel rel = new CustomRel(ns + SlimpayConstants.SEARCH_PAYMENT_URL);

        //get paymentId from transactionAdditionalData
        String subscriber = request.getBuyer().getCustomerIdentifier();
        //creditor reference
        String creditorReference = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.CREDITOR_REFERENCE_KEY);
        String scheme = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.MANDATE_PAYIN_SCHEME);
        //Order reference
        String mandateReference = request.getTransactionId();
        String paymentReference = request.getOrder().getReference();
        String currency = request.getAmount().getCurrency().toString();
        CustomRel relPayment = new CustomRel("payments");

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, creditorReference)
                .setUrlVariable(SUBSCRIBER_REFERENCE, subscriber)
                .setUrlVariable(MANDATE_REFERENCE, mandateReference)
                .setUrlVariable(REFERENCE, paymentReference)
                .setUrlVariable(CURRENCY, currency)
                .setUrlVariable(SCHEME, scheme)
                .setUrlVariable(DIRECTION, "IN")
                .build();

        Resource response = request(url, profile, authentication, follow);

        return getSlimpayResponse(relPayment, response);

    }

    private SlimpayResponse getSlimpayResponse(CustomRel relPayment, Resource response) throws PluginTechnicalException {
        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.searchPayment");
        }
        //the service called return a list of payment even when we search by mandate reference which is a unique identifier
        List<Resource> paymentsResult = response.getEmbeddedResources(relPayment);
        if (paymentsResult != null) {
            LOGGER.info(PAYMENT_FOUND_MESSAGE);
            return SlimpayPaymentResponse.fromJson(paymentsResult.get(0).getState().toString());
        } else {
            //return a Failure response
            LOGGER.info(PAYMENT_NOT_FOUND_MESSAGE);
            return SlimpayFailureResponse.fromJson(response.toString());
        }
    }

    /**
     * Cancel a payment
     *
     * @param request
     * @param body
     * @return
     * @throws PluginTechnicalException
     */

    public SlimpayResponse cancelPayment(RefundRequest request, JsonBody body) throws PluginTechnicalException {

        Oauth2BasicAuthentication authentication = createAuthentication(request);
        String url = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_URL_KEY);
        String profile = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_PROFILE_KEY);
        String ns = RequestConfigServiceImpl.INSTANCE.getParameterValue(request, SlimpayConstants.API_NS_KEY);
        CustomRel relCancelPayment = new CustomRel(ns + SlimpayConstants.CANCEL_PAYMENT_URL);

        //get paymentId from transactionAdditionalData
        PaymentResponseSuccessAdditionalData additionalData = PaymentResponseSuccessAdditionalData.fromJson(request.getTransactionAdditionalData());
        String paymentId = additionalData.getPaymentId();
        String entryPointUrl = url + "/payments/" + paymentId;


        Follow follow = new Follow.Builder(relCancelPayment)
                .setMethod(Method.POST)
                .setMessageBody(body)
                .build();
        Resource response = request(url, profile, authentication, follow, entryPointUrl);


        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.cancelPayment");
        }
        if (response.getState() != null) {
            return SlimpayPaymentResponse.fromJson(response.getState().toString());
        } else {//return a Failure response
            LOGGER.info("Fail to cancel the payment");
            return SlimpayFailureResponse.fromJson(response.toString());

        }
    }

    /**
     * Get payment ID fom transactionStatus Request
     *
     * @param request
     * @return String the payment id or null
     */
    public String getPaymentId(TransactionStatusRequest request) throws PluginTechnicalException {

        SlimpayResponse responsePayment = searchPayment(request);
        if (responsePayment.getClass() == SlimpayPaymentResponse.class) {
            SlimpayPaymentResponse paymentCreated = (SlimpayPaymentResponse) responsePayment;
            return paymentCreated.getId();
        }
        return "";

    }

    /**
     * Get payment ID fom RedirectionPaymentRequest Request
     *
     * @param request
     * @return String the payment id or null
     */
    public String getPaymentId(RedirectionPaymentRequest request) throws PluginTechnicalException {

        SlimpayResponse responsePayment = searchPayment(request);
        if (responsePayment.getClass() == SlimpayPaymentResponse.class) {
            SlimpayPaymentResponse paymentCreated = (SlimpayPaymentResponse) responsePayment;
            return paymentCreated.getId();
        }
        return "";

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
