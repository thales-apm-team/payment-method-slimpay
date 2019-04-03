package com.payline.payment.slimpay.utils.http;

import com.payline.payment.slimpay.bean.response.SlimpayFailureResponse;
import com.payline.payment.slimpay.bean.response.SlimpayOrderResponse;
import com.payline.payment.slimpay.bean.response.SlimpayPaymentResponse;
import com.payline.payment.slimpay.bean.response.SlimpayResponse;
import com.payline.payment.slimpay.exception.HttpCallException;
import com.payline.payment.slimpay.exception.InvalidDataException;
import com.payline.payment.slimpay.exception.PluginTechnicalException;
import com.payline.payment.slimpay.exception.SlimpayHttpException;
import com.payline.payment.slimpay.utils.SlimpayConstants;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.logger.LogManager;
import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.exception.RelNotFoundException;
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
import java.util.Currency;
import java.util.List;

public class SlimpayHttpClient {
    private static final Logger LOGGER = LogManager.getLogger(SlimpayHttpClient.class);
    private static HttpClientBuilder httpClientBuilder;

    private static final String EMPTY_RESPONSE_MESSAGE = "response is empty";
    private static final String USER_APPROVAL = "#user-approval";
    static final String CREDITOR_REFERENCE = "creditorReference";
    static final String REFERENCE = "reference";
    private static final String ORDER_FOUND_MESSAGE = "Order found";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Fail to find the order";
    private static final String PAYMENT_FOUND_MESSAGE = "Payment found";
    private static final String PAYMENT_NOT_FOUND_MESSAGE = "Fail to find the payment";
    private static final String REL_NOT_FOUND_MESSAGE = "Rel not found";
    private static final String ID = "id";
    static final String SUBSCRIBER_REFERENCE = "subcriberReference";
    static final String MANDATE_REFERENCE = "mandateReference";
    static final String SCHEME = "scheme";
    static final String DIRECTION = "direction";
    static final String CURRENCY = "currency";
    private static final String PAYMENT_ENDPOINT = "/payments/";

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
     * Create the request to call a #create-orders http request
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param body The body of the http request
     * @return
     * @throws PluginTechnicalException
     */
    public SlimpayOrderResponse testConnection( PartnerConfiguration partnerConfiguration, JsonBody body) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + SlimpayConstants.CREATE_ORDER_URL);
        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = this.sendRequest( partnerConfiguration, follow );
        if (response != null) {
            return SlimpayOrderResponse.fromJson(response.getState().toString());
        } else {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.testConnection");
        }
    }


    /**
     * Create the request to call a #create-orders http request
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param body    the body of the http request
     * @throws PluginTechnicalException
     */
    public SlimpayResponse createOrder( PartnerConfiguration partnerConfiguration, JsonBody body) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + SlimpayConstants.CREATE_ORDER_URL);
        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

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
        }
        else {
            //return a Failure response
            LOGGER.info("Fail to create the order");
            return SlimpayFailureResponse.fromJson(response.toString());
        }
    }

    /**
     * Create the request to call a #create-payout http request
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param body    the body of the http request
     * @throws PluginTechnicalException
     */
    public SlimpayResponse createPayout(PartnerConfiguration partnerConfiguration, JsonBody body) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + SlimpayConstants.CREATE_PAYOUT_URL);
        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);
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
     * Create the request to call a #get-orders http request
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param contractConfiguration The {@link ContractConfiguration} data
     * @throws PluginTechnicalException
     */
    public SlimpayResponse getOrder(PartnerConfiguration partnerConfiguration, ContractConfiguration contractConfiguration, String orderReference)
            throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        if( contractConfiguration == null ){
            throw new InvalidDataException("Contract configuration must not be null", "request.contractConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_ORDER_URL);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, contractConfiguration.getProperty( SlimpayConstants.CREDITOR_REFERENCE_KEY ))
                .setUrlVariable(REFERENCE, orderReference)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

        return getSlimpayResponse(response);
    }

    /**
     * Find a Slimpay Payment from its id
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @return A Slimpay payment
     * @throws PluginTechnicalException
     */
    public SlimpayResponse getPayment(PartnerConfiguration partnerConfiguration, String paymentId) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_PAYMENT_URL);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(ID, paymentId)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

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
     * Create the request to call #search-payments endpoint
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param contractConfiguration The {@link ContractConfiguration} data
     * @param subscriberReference The subscriber reference, also known as client reference
     * @param transactionId the unique transaction identifier, which acts as both mandate reference and payment reference
     * @param currency The transaction currency
     * @return
     * @throws PluginTechnicalException
     */
    public SlimpayResponse searchPayment(PartnerConfiguration partnerConfiguration, ContractConfiguration contractConfiguration,String subscriberReference, String transactionId, Currency currency ) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        if( contractConfiguration == null ){
            throw new InvalidDataException("Contract configuration must not be null", "request.contractConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + SlimpayConstants.SEARCH_PAYMENT_URL);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, contractConfiguration.getProperty( SlimpayConstants.CREDITOR_REFERENCE_KEY ))
                .setUrlVariable(SUBSCRIBER_REFERENCE, subscriberReference)
                .setUrlVariable(MANDATE_REFERENCE, transactionId)
                .setUrlVariable(REFERENCE, transactionId)
                .setUrlVariable(CURRENCY, currency.toString())
                .setUrlVariable(SCHEME, contractConfiguration.getProperty( SlimpayConstants.MANDATE_PAYIN_SCHEME ))
                .setUrlVariable(DIRECTION, "IN")
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

        return getSlimpayResponse(new CustomRel("payments"), response);
    }

    /**
     * Find reject reason of a payment.
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param paymentId the payment id
     * @return String the reject return reasonCode
     * @throws PluginTechnicalException
     */
    public String getPaymentRejectReason(PartnerConfiguration partnerConfiguration, String paymentId) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + SlimpayConstants.GET_PAYMENT_ISSUES_URL);
        CustomRel relPaymentIssue = new CustomRel("paymentIssues");

        String url = partnerConfiguration.getProperty( SlimpayConstants.API_URL_KEY );
        String entryPointUrl = url + PAYMENT_ENDPOINT + paymentId;
        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(ID, paymentId)
                .build();

        try {
            Resource response = this.sendRequest(partnerConfiguration, follow, entryPointUrl);

            //Get PaymentReject cause
            return getPaymentReturnReasonCode(relPaymentIssue, response);
        }

        catch (RelNotFoundException e){
            LOGGER.error(REL_NOT_FOUND_MESSAGE);
            throw new HttpCallException(REL_NOT_FOUND_MESSAGE, "SlimpayHttpClient.getPaymentRejectReason");
        }
    }

    /**
     * Cancel a payment
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param paymentId The payment Id.
     * @param body
     * @return
     * @throws PluginTechnicalException
     */
    public SlimpayResponse cancelPayment(PartnerConfiguration partnerConfiguration, String paymentId, JsonBody body) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel relCancelPayment = new CustomRel(ns + SlimpayConstants.CANCEL_PAYMENT_URL);

        String url = partnerConfiguration.getProperty( SlimpayConstants.API_URL_KEY );
        String entryPointUrl = url + PAYMENT_ENDPOINT + paymentId;

        Follow follow = new Follow.Builder(relCancelPayment)
                .setMethod(Method.POST)
                .setMessageBody(body)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow, entryPointUrl);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.cancelPayment");
        }
        if (response.getState() != null) {
            return SlimpayPaymentResponse.fromJson(response.getState().toString());
        } else {
            //return a Failure response
            LOGGER.info("Fail to cancel the payment");
            return SlimpayFailureResponse.fromJson(response.toString());
        }
    }

    /**
     * Build the HttpClient and Call the SlimPay API using the Slimpay hapiclient
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} containing information required to access the partner API
     * @param follow
     * @return The resulting resource.
     * @throws SlimpayHttpException
     */
    protected Resource sendRequest(PartnerConfiguration partnerConfiguration, Follow follow)
            throws SlimpayHttpException {
        return sendRequest( partnerConfiguration, follow, null );
    }

    /**
     * Build the HttpClient and Call the SlimPay API using the Slimpay hapiclient
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} containing information required to access the partner API
     * @param follow
     * @param entryPoint
     * @return The resulting resource.
     * @throws SlimpayHttpException
     */
    protected Resource sendRequest(PartnerConfiguration partnerConfiguration, Follow follow, String entryPoint)
            throws SlimpayHttpException {
        Oauth2BasicAuthentication authentication = new Oauth2BasicAuthentication.Builder()
                .setTokenEndPointUrl(SlimpayConstants.TOKEN_ENDPOINT)
                .setUserid( partnerConfiguration.getProperty( SlimpayConstants.APP_KEY ))
                .setPassword( partnerConfiguration.getProperty( SlimpayConstants.APP_SECRET ))
                .build();

        HapiClient.Builder hapiClientBuilder = new HapiClient.Builder()
                .setApiUrl( partnerConfiguration.getProperty( SlimpayConstants.API_URL_KEY ) )
                .setProfile( partnerConfiguration.getProperty( SlimpayConstants.API_PROFILE_KEY ) )
                .setAuthenticationMethod( authentication )
                .setClientBuilder( httpClientBuilder );
        if( entryPoint != null ){
            hapiClientBuilder.setEntryPointUrl(entryPoint);
        }

        return doSendRequest( hapiClientBuilder.build(), follow );
    }

    /**
     * Execute the Http call
     *
     * @param client
     * @param follow
     * @return
     * @throws SlimpayHttpException
     */
    private Resource doSendRequest(HapiClient client, Follow follow) throws SlimpayHttpException {
        final long start = System.currentTimeMillis();
        int count = 0;
        Resource response = null;
        HttpException exception = null;

        while (count < 3 && response == null) {
            try {
                LOGGER.info("Start partner call... [URL: {}]", client.getApiUrl());

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

    private String getPaymentReturnReasonCode(CustomRel relPaymentIssue, Resource response) throws PluginTechnicalException {
        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.searchPaymentIssue");
        }
        //the service called return a list of payment even when we search by mandate reference which is a unique identifier
        List<Resource> paymentsResult = response.getEmbeddedResources(relPaymentIssue);
        if (paymentsResult != null) {
            LOGGER.info(PAYMENT_FOUND_MESSAGE);
            return paymentsResult.get(0).getState().get("returnReasonCode").toString();
        } else {
            //return a empty String
            LOGGER.error("Rel not found");
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getPaymentReturnReasonCode");        }
    }


    SlimpayResponse getSlimpayResponse(CustomRel relPayment, Resource response) throws PluginTechnicalException {
        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getSlimpayResponse");
        }
        //the service called return a list of payment even when we search by mandate reference which is a unique identifier
        List<Resource> paymentsResult = null;
        try {
            paymentsResult = response.getEmbeddedResources(relPayment);
        }
        catch( RelNotFoundException e ){
            // TODO
        }

        if (paymentsResult != null) {
            LOGGER.info(PAYMENT_FOUND_MESSAGE);
            return SlimpayPaymentResponse.fromJson(paymentsResult.get(0).getState().toString());
        } else {
            //return a Failure response
            LOGGER.info(PAYMENT_NOT_FOUND_MESSAGE);
            return SlimpayFailureResponse.fromJson(response.toString());
        }
    }
}
