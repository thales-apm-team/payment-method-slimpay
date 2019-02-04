package com.payline.payment.slimpay.bean.common.request;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.slimpay.bean.common.SlimpayBean;
import com.payline.payment.slimpay.bean.common.SlimpayError;

public class SlimpayOrderRequest extends SlimpayBean {

    public String reference;
    public String paymentScheme;
    public String locale;
    public Creditor creditor;
    public Subscriber subscriber;
    public boolean started; //must be true to automatically start order after creation
    public String dateClosed; //DateTime, ISO8601, Read-Only.
    public String dateCreated; //DateTime, ISO8601, Read-Only.
    public String dateModified; //DateTime, ISO8601, Read-Only.
    public String pingAfter; //DateTime, ISO8601, Read-Only.
    public boolean mandateReused; //must be true to automatically start order after creation
    public boolean sendUserApproval; // if approval link will be sent to subscriber email after finishing order
    public Object[] items;
    public String successUrl;
    public String cancelUrl;
    public String failureUrl;


    @SerializedName("errors")
    public SlimpayError error;


    public class Creditor {
        public String reference;
        public String entity;

    }

    public class Subscriber {
        public String reference;
        public String entity;

    }
}
