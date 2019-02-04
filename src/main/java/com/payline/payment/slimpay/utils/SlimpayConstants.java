package com.payline.payment.slimpay.utils;


public class SlimpayConstants {


    //i18n
    public static final String I18N_SERVICE_DEFAULT_LOCALE = "en";
    public static final String RESOURCE_BUNDLE_BASE_NAME = "messages";

    //CONTRACT CONFIGURATION KEY
    // appId:appSecret
    public static final String AUTHORISATIONKEY_KEY ="authorizationKey" ;

    public static final String CREDITOR_REFERENCE_KEY ="creditorReference" ;
    public static final String CREDITOR_REFERENCE_KEY_LABEL ="creditor.reference.label" ;
    public static final String CREDITOR_REFERENCE_KEY_DESCRIPTION ="creditor.reference.description" ;
    public static final String CREDITOR_REFERENCE_KEY_MESSAGE_ERROR ="creditor.reference.error.message" ;

    public static final String FIRST_PAYMENT_SCHEME ="paymentScheme" ;
    public static final String FIRST_PAYMENT_SCHEME_DESCRIPTION ="payment.scheme.description" ;
    public static final String FIRST_PAYMENT_SCHEME_LABEL ="payment.scheme.label" ;
    public static final String FIRST_PAYMENT_MESSAGE_ERROR ="payment.scheme.error.message" ;

    public static final String MANDATE_PAYIN_SCHEME ="mandateScheme" ;
    public static final String MANDATE_PAYIN_SCHEME_DESCRIPTION ="mandate.schema.description" ;
    public static final String MANDATE_PAYIN_SCHEME_LABEL ="mandate.schema.label" ;
    public static final String MANDATE_PAYIN_MESSAGE_ERROR ="mandate.schema.error.message" ;

    public static final String SIGNATURE_APPROVAL_METHOD ="signatureApproval" ;
    public static final String SIGNATURE_APPROVAL_METHOD_DESCRIPTION ="signature.approval.description" ;
    public static final String SIGNATURE_APPROVAL_METHOD_LABEL ="signature.approval.label" ;
    public static final String SIGNATURE_APPROVAL_METHOD_MESSAGE_ERROR ="signature.approval.message.error" ;

    public static final String PAYMENT_PROCESSOR ="paymentProcessor" ;
    public static final String PAYMENT_PROCESSOR_DESCRIPTION ="payment.processor.description" ;
    public static final String PAYMENT_PROCESSOR_LABEL ="payment.processor.label" ;
    public static final String PAYMENT_PROCESSOR_MESSAGE_ERROR ="payment.processor.message.error" ;


    //PARTNER CONFIGURATION KEY
    public static final String APP_KEY ="appName" ;
    public static final String APP_KEY_MESSAGE_ERROR ="app.name.message.error" ;

    public static final String APP_SECRET ="appSecret" ;
    public static final String APP_SECRET_MESSAGE_ERROR ="app.secret.message.error" ;

    private SlimpayConstants(){
        //ras
    }
}
