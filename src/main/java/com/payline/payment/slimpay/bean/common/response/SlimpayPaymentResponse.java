package com.payline.payment.slimpay.bean.common.response;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//A payment order item
//https://dev.slimpay.com/hapi/reference/order-items#order-items-representation
public class SlimpayPaymentResponse extends SlimpayResponse {

    private static final transient Logger LOGGER = LogManager.getLogger(SlimpayPaymentResponse.class);

    private String action;
    private String id;
    private String reference;
    private String scheme;
    private String direction;
    private String category;
    private Float amount;
    private String currency;
    private String state;
    private String executionDate;
    private String executionStatus;
    private String sequenceType;
    private Integer replayCount;
    private String dateCreated; //DateTime, ISO8601, Read-Only.
    private String dateModified; //DateTime, ISO8601, Read-Only.
    private String dateBooked; //DateTime, ISO8601, Read-Only.
    private String dateValued; //DateTime, ISO8601, Read-Only.
    private String capture; //DateTime, ISO8601, Read-Only.
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

    private SlimpayPaymentResponse(){}

    public SlimpayPaymentResponse(SlimpayPaymentResponse.Builder builder) {
        this.action = builder.action;
        this.id = builder.id;
        this.reference = builder.reference;
        this.scheme = builder.scheme;
        this.direction = builder.direction;
        this.category = builder.category;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.state = builder.state;
        this.executionDate = builder.executionDate;
        this.executionStatus = builder.executionStatus;
        this.sequenceType = builder.sequenceType;
        this.replayCount = builder.replayCount;
        this.capture = builder.capture;
        this.confirmed = builder.confirmed;
        this.processor = builder.processor;
        this.correlationId = builder.correlationId;
        this.label = builder.label;
        this.dateCreated = builder.dateCreated;
        this.dateModified = builder.dateModified;
        this.dateBooked = builder.dateBooked;
        this.dateValued = builder.dateValued;
        this.capture = builder.capture;
    }

    /**
     * SlimpayPaymentResponse Builder
     */
    public static class Builder {
        private String action;
        private String id;
        private String reference;
        private String scheme;
        private String direction;
        private String category;
        private Float amount;
        private String currency;
        private String state;
        private String executionDate;
        private String executionStatus;
        private String sequenceType;
        private Integer replayCount;
        private String dateCreated;
        private String dateModified;
        private String dateBooked;
        private String dateValued;
        private String capture;
        private boolean confirmed;
        private String processor;
        private String correlationId;
        private String label;



        public static SlimpayPaymentResponse.Builder aPaymentResponseBuilder(){
            return new SlimpayPaymentResponse.Builder();
        }


        public SlimpayPaymentResponse.Builder verifyIntegrity() {

            //to do logger les champs manquants obligatoire ??
            if (this.reference == null) {
                LOGGER.warn ("SlimpayPaymentResponse must have a reference when built");
            }
            if (this.scheme == null) {
                LOGGER.warn ("SlimpayPaymentResponse must have a scheme when built");
            }
            if (this.amount == null) {
                LOGGER.warn ("SlimpayPaymentResponse must have a amount when built");
            }
            if (this.currency == null) {
                LOGGER.warn ("SlimpayPaymentResponse must have a currency when built");
            }
            if (this.action == null) {
                LOGGER.warn ("SlimpayPaymentResponse must have a action when built");
            }
            if (this.direction == null) {
                LOGGER.warn ("SlimpayPaymentResponse must have a direction when built");
            }

            return this;
        }

        public SlimpayPaymentResponse build() {
            return new SlimpayPaymentResponse(this.verifyIntegrity());
        }


        /**
         * Create a SlimpayPaymentResponse from a  json returned by Slimpay server
         * @param json a SlimPay payment
         * @return a SlimpayPaymentResponse
         */
        public static SlimpayPaymentResponse fromJson(String json) {
            Gson parser = new Gson();
            SlimpayPaymentResponse.Builder paymentResponse = parser.fromJson(json, SlimpayPaymentResponse.Builder.class);
            //check if mandatory fields are not missing before building the object
            return new SlimpayPaymentResponse(paymentResponse.verifyIntegrity());
        }


    }
}
