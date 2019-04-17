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

    // API Endpoints
    private static final String API_ENDPOINT_TOKEN = "/oauth/token";
    private static final String API_ENDPOINT_PAYMENT = "/payments/";

    // API Relations Types
    private static final String API_REL_CANCEL_PAYMENT = "#cancel-payment";
    private static final String API_REL_CREATE_ORDER = "#create-orders";
    private static final String API_REL_CREATE_PAYOUT = "#create-payouts";
    private static final String API_REL_GET_ORDER = "#get-orders";
    private static final String API_REL_GET_PAYMENT = "#search-payment-by-id";
    private static final String API_REL_GET_PAYMENT_ISSUES = "#get-payment-issues";
    private static final String API_REL_SEARCH_PAYMENTS = "#search-payments";
    private static final String API_REL_USER_APPROVAL = "#user-approval";

    // Messages
    private static final String EMPTY_RESPONSE_MESSAGE = "response is empty";
    private static final String REL_NOT_FOUND_MESSAGE = "Rel not found";

    // URL parameters keys
    private static final String CREDITOR_REFERENCE = "creditorReference";
    private static final String CURRENCY = "currency";
    private static final String DIRECTION = "direction";
    private static final String ID = "id";
    private static final String MANDATE_REFERENCE = "mandateReference";
    private static final String REFERENCE = "reference";
    private static final String SCHEME = "scheme";
    private static final String SUBSCRIBER_REFERENCE = "subcriberReference";


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
        CustomRel rel = new CustomRel(ns + API_REL_CREATE_ORDER);
        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = this.sendRequest( partnerConfiguration, follow );
        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.testConnection");
        }

        return SlimpayOrderResponse.fromJson(response.getState().toString());
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
        CustomRel rel = new CustomRel(ns + API_REL_CREATE_ORDER);
        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.createOrder");
        }
        if (response.getState() == null) {
            //return a Failure response
            LOGGER.info("Fail to create the order");
            return SlimpayFailureResponse.fromJson(response.toString());
        }

        LOGGER.info("Order created");
        SlimpayOrderResponse orderSuccessResponse = SlimpayOrderResponse.fromJson(response.getState().toString());
        //add confirm url on the response
        try {
            String confirmationUrl = response.getLink(new CustomRel(ns + API_REL_USER_APPROVAL)).getHref();
            orderSuccessResponse.setUrlApproval(confirmationUrl);
        }
        catch( RelNotFoundException e ){
            // If we encounter this error, it means the returned resource does not have a link "user-approval"
            LOGGER.error( "Cannot find 'user-approval' link in the server response" );
            throw new PluginTechnicalException( e, "redirect URL not found in the server response" );
        }

        return orderSuccessResponse;
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
        CustomRel rel = new CustomRel(ns + API_REL_CREATE_PAYOUT);
        Follow follow = new Follow.Builder(rel)
                .setMessageBody(body)
                .setMethod(Method.POST)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.createPayout");
        }
        if (response.getState() == null) {
            LOGGER.info("Fail to create the payout");
            return SlimpayFailureResponse.fromJson(response.toString());
        }

        LOGGER.info("Payout created");
        return SlimpayPaymentResponse.fromJson(response.getState().toString());
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
        CustomRel rel = new CustomRel(ns + API_REL_GET_ORDER);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, contractConfiguration.getProperty( SlimpayConstants.CREDITOR_REFERENCE_KEY ))
                .setUrlVariable(REFERENCE, orderReference)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getOrder");
        }
        if (response.getState() == null) {
            LOGGER.info("Fail to find the order");
            return SlimpayFailureResponse.fromJson(response.toString());
        }

        LOGGER.info("Order found");
        return SlimpayOrderResponse.fromJson(response.getState().toString());
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
        CustomRel rel = new CustomRel(ns + API_REL_GET_PAYMENT);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(ID, paymentId)
                .build();

        Resource payment = this.sendRequest(partnerConfiguration, follow);

        if (payment == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getPayment");
        }
        if (payment.getState() == null) {
            LOGGER.info("Fail to find the payment");
            return SlimpayFailureResponse.fromJson(payment.toString());
        }

        LOGGER.info("Payment found");

        // Check if this payment can be cancelled
        boolean cancellable = true;
        CustomRel relCancelPayment = new CustomRel(ns + API_REL_CANCEL_PAYMENT);
        try {
            payment.getLink(relCancelPayment);
        } catch( RelNotFoundException e ){
            cancellable = false;
        }

        return SlimpayPaymentResponse.fromJson(payment.getState().toString(), cancellable);
    }

    /**
     * Create the request to call #search-payments endpoint
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} data
     * @param contractConfiguration The {@link ContractConfiguration} data
     * @param paymentReference The payment (payin) reference
     * @param mandateReference The mandate reference
     * @param subscriberReference The subscriber reference, also known as client reference
     * @return
     * @throws PluginTechnicalException
     */
    public SlimpayResponse searchPayment(PartnerConfiguration partnerConfiguration, ContractConfiguration contractConfiguration,
                                         String paymentReference, String mandateReference, String subscriberReference ) throws PluginTechnicalException {
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Partner configuration must not be null", "request.partnerConfiguration");
        }
        if( contractConfiguration == null ){
            throw new InvalidDataException("Contract configuration must not be null", "request.contractConfiguration");
        }
        String ns = partnerConfiguration.getProperty( SlimpayConstants.API_NS_KEY );
        CustomRel rel = new CustomRel(ns + API_REL_SEARCH_PAYMENTS);

        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(CREDITOR_REFERENCE, contractConfiguration.getProperty( SlimpayConstants.CREDITOR_REFERENCE_KEY ))
                .setUrlVariable(SUBSCRIBER_REFERENCE, subscriberReference)
                .setUrlVariable(MANDATE_REFERENCE, mandateReference)
                .setUrlVariable(REFERENCE, paymentReference)
                .setUrlVariable(SCHEME, contractConfiguration.getProperty( SlimpayConstants.MANDATE_PAYIN_SCHEME ))
                .setUrlVariable(DIRECTION, "IN")
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.searchPayment");
        }

        // The service called returns a list of payments even when we search by mandate reference which is a unique identifier
        List<Resource> paymentsResult = null;
        try {
            paymentsResult = response.getEmbeddedResources( new CustomRel("payments") );
        }
        catch( RelNotFoundException e ){
            // If we encounter this error, it means that the response contains no payment
            LOGGER.error( "Cannot find 'payments' rel in the server response" );
            paymentsResult = null;
        }

        if( paymentsResult == null ){
            LOGGER.info("Fail to find the payment");
            return SlimpayFailureResponse.fromJson(response.toString());
        }

        LOGGER.info("Payment found");
        return SlimpayPaymentResponse.fromJson(paymentsResult.get(0).getState().toString());
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
        CustomRel rel = new CustomRel(ns + API_REL_GET_PAYMENT_ISSUES);
        String url = partnerConfiguration.getProperty( SlimpayConstants.API_URL_KEY );
        String entryPointUrl = url + API_ENDPOINT_PAYMENT + paymentId;
        Follow follow = new Follow.Builder(rel)
                .setMethod(Method.GET)
                .setUrlVariable(ID, paymentId)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow, entryPointUrl);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getPaymentRejectReason");
        }

        // The service called returns a list of payment issues, we return the first one's code
        Resource paymentRejectReason = null;
        try {
            List<Resource> paymentsResult = response.getEmbeddedResources( new CustomRel("paymentIssues") );
            if( paymentsResult != null ){
                paymentRejectReason = paymentsResult.get(0);
            }
        }
        catch( RelNotFoundException e ){
            // If we encounter this error, it means that the response contains no paymentIssues
            LOGGER.error( "Cannot find 'paymentIssues' rel in the server response" );
        }

        if( paymentRejectReason == null ){
            LOGGER.error("Payment reason not found");
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.getPaymentReturnReasonCode");
        }

        LOGGER.info("Payment reason found");
        return paymentRejectReason.getState().get("returnReasonCode").toString();
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
        CustomRel relCancelPayment = new CustomRel(ns + API_REL_CANCEL_PAYMENT);

        String url = partnerConfiguration.getProperty( SlimpayConstants.API_URL_KEY );
        String entryPointUrl = url + API_ENDPOINT_PAYMENT + paymentId;

        Follow follow = new Follow.Builder(relCancelPayment)
                .setMethod(Method.POST)
                .setMessageBody(body)
                .build();

        Resource response = this.sendRequest(partnerConfiguration, follow, entryPointUrl);

        if (response == null) {
            throw new HttpCallException(EMPTY_RESPONSE_MESSAGE, "SlimpayHttpClient.cancelPayment");
        }
        if( response.getState() == null ){
            LOGGER.info("Fail to cancel the payment");
            return SlimpayFailureResponse.fromJson(response.toString());
        }

        return SlimpayPaymentResponse.fromJson(response.getState().toString());
    }

    /**
     * Build the HttpClient and Call the SlimPay API using the Slimpay hapiclient
     *
     * @param partnerConfiguration The {@link PartnerConfiguration} containing information required to access the partner API
     * @param follow
     * @return The resulting resource.
     * @throws SlimpayHttpException
     */
    Resource sendRequest(PartnerConfiguration partnerConfiguration, Follow follow)
            throws PluginTechnicalException {
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
    Resource sendRequest(PartnerConfiguration partnerConfiguration, Follow follow, String entryPoint)
            throws PluginTechnicalException {
        Oauth2BasicAuthentication authentication = new Oauth2BasicAuthentication.Builder()
                .setTokenEndPointUrl( API_ENDPOINT_TOKEN )
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
    Resource doSendRequest(HapiClient client, Follow follow) throws PluginTechnicalException {
        final long start = System.currentTimeMillis();
        int count = 0;
        Resource response = null;
        HttpException exception = null;

        while (count < 3 && response == null) {
            try {
                LOGGER.info("Start partner call... [URL: {}]", client.getApiUrl());

                response = client.send(follow);

                final long end = System.currentTimeMillis();
                LOGGER.info("End partner call [T: {}ms] [CODE: {}]", end - start);
            } catch (HttpException e) {
                exception = e;
            } catch( RelNotFoundException e ){
                throw new PluginTechnicalException( e, "Relation type not found" );
            } finally {
                count++;
            }
        }

        if( response == null && exception != null ){
            throw new SlimpayHttpException(exception);
        }

        return response;
    }
}
