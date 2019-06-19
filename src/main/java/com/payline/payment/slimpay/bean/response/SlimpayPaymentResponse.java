package com.payline.payment.slimpay.bean.response;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.payline.payment.slimpay.exception.MalformedResponseException;
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

    private Boolean cancellable;

    public String getAction() {
        return action;
    }

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

    public Boolean isCancellable(){
        return cancellable;
    }

    private SlimpayPaymentResponse() {
    }

    /**
     * Create a SlimpayPaymentResponse from a json returned by Slimpay server.
     *
     * @param json a SlimPay payment
     * @param isCancellable true if the payment can be cancelled, false if it can't and null if we don't know
     * @return a SlimpayPaymentResponse
     * @throws MalformedResponseException if the JSON content is not properly formatted
     */
    public static SlimpayPaymentResponse fromJson(String json, Boolean isCancellable) throws MalformedResponseException {
        Gson parser = new Gson();
        try {
            SlimpayPaymentResponse response = parser.fromJson(json, SlimpayPaymentResponse.class);
            response.cancellable = isCancellable;
            return response;
        }
        catch( JsonSyntaxException e ){
            throw new MalformedResponseException( e );
        }
    }

    /**
     * Create a SlimpayPaymentResponse from a json returned by Slimpay server.
     * Default method for the case in which we don't pass isCancellable argument.
     *
     * @param json a SlimPay payment
     * @return a SlimpayPaymentResponse
     * @throws MalformedResponseException if the JSON content is not properly formatted
     */
    public static SlimpayPaymentResponse fromJson(String json) throws MalformedResponseException {
        return fromJson(json, null);
    }

}
