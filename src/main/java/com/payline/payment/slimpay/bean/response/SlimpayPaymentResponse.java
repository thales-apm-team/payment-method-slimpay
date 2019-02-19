package com.payline.payment.slimpay.bean.response;

import com.google.gson.Gson;
import com.payline.payment.slimpay.utils.Required;

//A payment order item
//https://dev.slimpay.com/hapi/reference/order-items#order-items-representation
public class SlimpayPaymentResponse extends SlimpayResponse {

    @Required
    private String action;
    private String id;
    @Required
    private String reference;
    @Required
    private String scheme;
    @Required
    private String direction;
    private String category;
    @Required
    private Float amount;
    @Required
    private String currency;
    private String state;
    private String executionDate;
    private String executionStatus;
    private String sequenceType;
    private Integer replayCount;
    //DateTime, ISO8601, Read-Only.
    private String dateCreated;
    //DateTime, ISO8601, Read-Only.
    private String dateModified;
    //DateTime, ISO8601, Read-Only.
    private String dateBooked;
    //DateTime, ISO8601, Read-Only.
    private String dateValued;
    //DateTime, ISO8601, Read-Only.
    private String capture;
    private boolean confirmed;
    private String processor;
    private String correlationId;
    private String label;

    public String getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getScheme() {
        return scheme;
    }

    public String getDirection() {
        return direction;
    }

    public String getCategory() {
        return category;
    }

    public Float getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getState() {
        return state;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public String getSequenceType() {
        return sequenceType;
    }

    public Integer getReplayCount() {
        return replayCount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public String getDateBooked() {
        return dateBooked;
    }

    public String getDateValued() {
        return dateValued;
    }

    public String getCapture() {
        return capture;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getProcessor() {
        return processor;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getLabel() {
        return label;
    }

    public String getAction() {
        return action;
    }

    private SlimpayPaymentResponse() {
    }

    /**
     * Create a SlimpayPaymentResponse from a  json returned by Slimpay server
     *
     * @param json a SlimPay payment
     * @return a SlimpayPaymentResponse
     */
    public static SlimpayPaymentResponse fromJson(String json) {
        Gson parser = new Gson();
        return parser.fromJson(json, SlimpayPaymentResponse.class);

    }


}
