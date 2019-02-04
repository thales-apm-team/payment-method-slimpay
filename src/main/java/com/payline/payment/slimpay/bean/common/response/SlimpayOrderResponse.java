package com.payline.payment.slimpay.bean.common.response;

import com.google.gson.Gson;
import com.payline.payment.slimpay.bean.common.SlimpayBean;

public class SlimpayOrderResponse extends SlimpayBean {

    public String reference;
    public String id;
    public String paymentScheme;
    public String locale;
    public boolean started; //must be true to automatically start order after creation
    public String dateStarted; //DateTime, ISO8601, Read-Only.
    public String dateCreated; //DateTime, ISO8601, Read-Only.
    public String checkoutActor;
    public boolean sendUserApproval; // if approval link will be sent to subscriber email after finishing order






    /**
     * Create a PaymentResponse from a  json returned by Slimpay server
     * @param json a SlimPay payment
     * @return a PaymentResponse
     */
    public static SlimpayOrderResponse fromJson(String json) {
        Gson parser = new Gson();
        return parser.fromJson(json, SlimpayOrderResponse.class);

    }
}
