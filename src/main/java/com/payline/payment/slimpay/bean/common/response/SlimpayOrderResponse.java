package com.payline.payment.slimpay.bean.common.response;

import com.google.gson.Gson;

public class SlimpayOrderResponse extends SlimpayResponse {

    public String reference;
    public String id;
    public String paymentScheme;
    public String locale;
    public boolean started; //must be true to automatically start order after creation
    public String dateStarted; //DateTime, ISO8601, Read-Only.
    public String dateCreated; //DateTime, ISO8601, Read-Only.
    public String checkoutActor;
    public String state;
    public boolean sendUserApproval; // if approval link will be sent to subscriber email after finishing order
    //User approval link
    public transient String urlApproval; //url de confirmation

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
    public static SlimpayOrderResponse fromJson(String json) {
        Gson parser = new Gson();
        return parser.fromJson(json, SlimpayOrderResponse.class);

    }
}
