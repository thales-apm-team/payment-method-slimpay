package com.payline.payment.slimpay.bean.response;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.payline.payment.slimpay.exception.MalformedResponseException;

public class SlimpayOrderResponse extends SlimpayResponse {

    private String reference;
    private String id;
    private String paymentScheme;
    private String locale;
    private boolean started; //must be true to automatically start order after creation
    private String dateStarted; //DateTime, ISO8601, Read-Only.
    private String dateCreated; //DateTime, ISO8601, Read-Only.
    private String checkoutActor;
    private String state;
    private boolean sendUserApproval; // if approval link has been sent to subscriber email
    //User approval link
    private transient String urlApproval; //url de confirmation

    public void setUrlApproval(String urlApproval) {
        this.urlApproval = urlApproval;
    }

    public String getUrlApproval() {
        return urlApproval;
    }

    public String getState() {
        return state;
    }

    public String getReference() {
        return reference;
    }

    public String getId() {
        return id;
    }

    public String getPaymentScheme() {
        return paymentScheme;
    }

    public String getLocale() {
        return locale;
    }

    public boolean isStarted() {
        return started;
    }

    public String getDateStarted() {
        return dateStarted;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getCheckoutActor() {
        return checkoutActor;
    }

    public boolean isSendUserApproval() {
        return sendUserApproval;
    }

    /**
     * Create a SlimpayPaymentResponse from a  json returned by Slimpay server
     * @param json a SlimPay payment
     * @return a SlimpayPaymentResponse
     */
    public static SlimpayOrderResponse fromJson(String json) throws MalformedResponseException {
        Gson parser = new Gson();
        try {
            return parser.fromJson(json, SlimpayOrderResponse.class);
        }
        catch( JsonSyntaxException e ){
            throw new MalformedResponseException( e );
        }    }
}
