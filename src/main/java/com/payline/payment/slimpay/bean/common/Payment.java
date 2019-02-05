package com.payline.payment.slimpay.bean.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//A payment order item object
//https://dev.slimpay.com/hapi/reference/order-items#payment-order-item-representation

public class Payment extends SlimpayBean {

    private static final transient Logger LOGGER = LogManager.getLogger(Payment.class);

    private String action;
    private String reference;
    private String scheme;
    private String direction;
    private String category;
    private String  amount;
    private String currency;
    private String executionDate;
    public String capture; //DateTime, ISO8601, Read-Only.
    public String correlationId;
    public String label;


    private Payment(){}

    private Payment(Payment.Builder builder) {
        this.action = builder.action;
        this.reference = builder.reference;
        this.scheme = builder.scheme;
        this.direction = builder.direction;
        this.category = builder.category;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.executionDate = builder.executionDate;
        this.capture = builder.capture;
        this.correlationId = builder.correlationId;
        this.label = builder.label;
    }

    public static class Builder {
        private String action;
        private String reference;
        private String scheme;
        private String direction;
        private String category;
        private String  amount;
        private String currency;
        private String executionDate;
        public String capture;
        public String correlationId;
        public String label;


        public static Payment.Builder aPaymentBuilder(){
            return new Payment.Builder();
        }

        public Payment.Builder withLabel(String label){
            this.label = label;
            return this;
        }
        public Payment.Builder withAction(String action){
           this.action = action;
            return this;
        }
        public Payment.Builder withReference (String reference){
            this.reference = reference;
            return this;
        }
        public Payment.Builder withScheme (String scheme){
            this.scheme = scheme;
            return this;
        }

        public Payment.Builder withDirection (String direction){
            this.direction = direction;
            return this;
        }

        public Payment.Builder withCategory (String category ){
            this.category = category;
            return this;
        }

        public Payment.Builder withAmount (String amount ){
            this.amount = amount;
            return this;
        }

        public Payment.Builder withCurrency (String currency){
            this.currency = currency;
            return this;
        }

        public Payment.Builder withExecutionDate (String executionDate){
            this.executionDate = executionDate;
            return this;
        }


        public Payment.Builder withCapture (String capture){
            this.capture = capture;
            return this;
        }


        public Payment.Builder withCorrelationId (String correlationId){
            this.correlationId = correlationId;
            return this;
        }

        public Payment.Builder verifyIntegrity() {

            //to do logger les champs manquants obligatoire ??
            if (this.reference == null) {
                LOGGER.warn ("Payment must have a reference when built");
            }
            if (this.scheme == null) {
                LOGGER.warn ("Payment must have a scheme when built");
            }
            if (this.amount == null) {
                LOGGER.warn ("Payment must have a amount when built");
            }
            if (this.currency == null) {
                LOGGER.warn ("Payment must have a currency when built");
            }
            if (this.action == null) {
                LOGGER.warn ("Payment must have a action when built");
            }
            if (this.direction == null) {
                LOGGER.warn ("Payment must have a direction when built");
            }

            if (this.direction != null && ( this.direction !="IN" && this.direction !="OUT") ) {
                LOGGER.warn ("Payment direction value must be 'IN' or 'OUT' ");
            }

            return this;
        }

        public Payment build() {
            return new Payment(this.verifyIntegrity());
        }


    }
}
