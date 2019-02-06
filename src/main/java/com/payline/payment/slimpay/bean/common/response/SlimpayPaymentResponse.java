package com.payline.payment.slimpay.bean.common.response;

import com.google.gson.Gson;
import com.payline.payment.slimpay.bean.common.SlimpayBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//A payment order item
//https://dev.slimpay.com/hapi/reference/order-items#order-items-representation
public class SlimpayPaymentResponse extends SlimpayBean {

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
    public String dateCreated; //DateTime, ISO8601, Read-Only.
    public String dateModified; //DateTime, ISO8601, Read-Only.
    public String dateBooked; //DateTime, ISO8601, Read-Only.
    public String dateValued; //DateTime, ISO8601, Read-Only.
    public String capture; //DateTime, ISO8601, Read-Only.
    public boolean confirmed;
    public String processor;
    public String correlationId;
    public String label;

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
        public String dateCreated;
        public String dateModified;
        public String dateBooked;
        public String dateValued;
        public String capture;
        public boolean confirmed;
        public String processor;
        public String correlationId;
        public String label;



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
