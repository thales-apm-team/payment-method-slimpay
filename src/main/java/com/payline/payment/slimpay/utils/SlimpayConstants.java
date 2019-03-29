package com.payline.payment.slimpay.utils;


public class SlimpayConstants {


    //CONTRACT CONFIGURATION KEY
    // appId:appSecret
    public static final String AUTHORISATIONKEY_KEY = "authorizationKey";

    public static final String CREDITOR_REFERENCE_KEY = "creditorReference";
    public static final String CREDITOR_REFERENCE_KEY_LABEL = "creditor.reference.label";
    public static final String CREDITOR_REFERENCE_KEY_DESCRIPTION = "creditor.reference.description";
    public static final String CREDITOR_REFERENCE_KEY_MESSAGE_ERROR = "creditor.reference.error.message";

    public static final String FIRST_PAYMENT_SCHEME = "paymentScheme";
    public static final String FIRST_PAYMENT_SCHEME_DESCRIPTION = "payment.scheme.description";
    public static final String FIRST_PAYMENT_SCHEME_LABEL = "payment.scheme.label";
    public static final String FIRST_PAYMENT_MESSAGE_ERROR = "payment.scheme.error.message";

    public static final String MANDATE_PAYIN_SCHEME = "mandateScheme";
    public static final String MANDATE_PAYIN_SCHEME_DESCRIPTION = "mandate.schema.description";
    public static final String MANDATE_PAYIN_SCHEME_LABEL = "mandate.schema.label";
    public static final String MANDATE_PAYIN_MESSAGE_ERROR = "mandate.schema.error.message";

    public static final String SIGNATURE_APPROVAL_METHOD = "signatureApproval";
    public static final String SIGNATURE_APPROVAL_METHOD_DESCRIPTION = "signature.approval.description";
    public static final String SIGNATURE_APPROVAL_METHOD_LABEL = "signature.approval.label";
    public static final String SIGNATURE_APPROVAL_METHOD_MESSAGE_ERROR = "signature.approval.message.error";

    public static final String PAYMENT_PROCESSOR = "paymentProcessor";
    public static final String PAYMENT_PROCESSOR_DESCRIPTION = "payment.processor.description";
    public static final String PAYMENT_PROCESSOR_LABEL = "payment.processor.label";
    public static final String PAYMENT_PROCESSOR_MESSAGE_ERROR = "payment.processor.message.error";


    public static final String MANDATE_STANDARD_KEY = "mandateStandard";
    public static final String MANDATE_STANDARD_DESCRIPTION = "mandate.standard.description";
    public static final String MANDATE_STANDARD_LABEL = "mandate.standard.label";
    public static final String MANDATE_STANDARD_MESSAGE_ERROR = "mandate.standard.message.error";

    public static final String API_TEST_MESSAGE_ERROR = "api.test.message.error";

    //PARTNER CONFIGURATION KEY
    public static final String APP_KEY = "appName";
    public static final String APP_KEY_MESSAGE_ERROR = "app.name.message.error";

    public static final String APP_SECRET = "appSecret";
    public static final String APP_SECRET_MESSAGE_ERROR = "app.secret.message.error";
    //URLS
    public static final String API_URL_KEY = "apiUrl";
    public static final String API_PROFILE_KEY = "apiProfile";
    public static final String API_NS_KEY = "apiNs";


    //REQUESTS
    public static final String TOKEN_ENDPOINT = "/oauth/token";
    public static final String CREATE_ORDER_URL = "#create-orders";
    public static final String CREATE_PAYOUT_URL = "#create-payouts";
    public static final String GET_ORDER_URL = "#get-orders";
    public static final String CANCEL_PAYMENT_URL = "#cancel-payment";
    public static final String GET_PAYMENT_URL = "#search-payment-by-id";
    public static final String SEARCH_PAYMENT_URL = "#search-payments";
    public static final String GET_PAYMENT_ISSUES_URL = "#get-payment-issues";

    //INTERNAL KEYS
    public static final String ORDER_REFERENCE = "orderReference";
    public static final String ORDER_ID = "orderId";
    public static final int ERROR_MAX_LENGTH = 50;


    private SlimpayConstants() {
        //ras
    }
}
