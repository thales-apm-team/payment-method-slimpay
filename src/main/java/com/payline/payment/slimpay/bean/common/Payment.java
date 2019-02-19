package com.payline.payment.slimpay.bean.common;

import com.payline.payment.slimpay.utils.Required;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//A payment order item object
//https://dev.slimpay.com/hapi/reference/order-items#payment-order-item-representation

public class Payment extends SlimpayBean {

    private static final Logger LOGGER = LogManager.getLogger(Payment.class);

    protected static final String REFERENCE_WARN = "Payment must have a reference when built";
    protected static final String SCHEME_WARN = "Payment must have a scheme when built";
    protected static final String AMOUNT_WARN = "Payment must have a amount when built";
    protected static final String CURRENCY_WARN = "Payment must have a currency when built";
    protected static final String ACTION_WARN = "Payment must have a action when built";
    protected static final String DIRECTION_WARN = "Payment must have a direction when built";
    protected static final String WRONG_DIRECTION_WARN = "Payment direction value must be 'IN' or 'OUT'";
    protected static final String CREDITOR_WARN = "Payment with direction 'OUT' must have a creditor when built";
    protected static final String SUBSCRIBER_WARN = "Payment with direction 'OUT'  must have a subscriber when built";

    @Required
    private String action;
    @Required
    private String reference;
    @Required
    private String scheme;
    @Required
    private String direction;
    private String category;
    @Required
    private String amount;
    @Required
    private String currency;
    private String executionDate;
    private String capture; //DateTime, ISO8601, Read-Only.
    private String correlationId;
    private String label;

    //only usefull for payout
    private Creditor creditor;
    private Subscriber subscriber;
    private Mandate mandate;

    public Mandate getMandate() {
        return mandate;
    }

    public String getCapture() {
        return capture;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getLabel() {
        return label;
    }

    public Creditor getCreditor() {
        return creditor;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public String getAction() {
        return action;
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

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    private Payment() {
    }

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
        this.creditor = builder.creditor;
        this.subscriber = builder.subscriber;
        this.mandate = builder.mandate;
    }

    public static class Builder {
        private String action;
        private String reference;
        private String scheme;
        private String direction;
        private String category;
        private String amount;
        private String currency;
        private String executionDate;
        private String capture;
        private String correlationId;
        private String label;
        private Creditor creditor;
        private Subscriber subscriber;
        private Mandate mandate;

        public static Payment.Builder aPaymentBuilder() {
            return new Payment.Builder();
        }

        public Payment.Builder withLabel(String label) {
            this.label = label;
            return this;
        }

        public Payment.Builder withAction(String action) {
            this.action = action;
            return this;
        }

        public Payment.Builder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public Payment.Builder withScheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Payment.Builder withDirection(String direction) {
            this.direction = direction;
            return this;
        }

        public Payment.Builder withCategory(String category) {
            this.category = category;
            return this;
        }

        public Payment.Builder withAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public Payment.Builder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Payment.Builder withExecutionDate(String executionDate) {
            this.executionDate = executionDate;
            return this;
        }


        public Payment.Builder withCapture(String capture) {
            this.capture = capture;
            return this;
        }


        public Payment.Builder withCorrelationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Payment.Builder withSubscriber(Subscriber subscriber) {
            this.subscriber = subscriber;
            return this;
        }

        public Payment.Builder withCreditor(Creditor creditor) {
            this.creditor = creditor;
            return this;
        }

        public Payment.Builder withMandate(Mandate mandate) {
            this.mandate = mandate;
            return this;
        }

        public Payment.Builder verifyIntegrity() {

            //to do logger les champs manquants obligatoire ??
            if (this.reference == null) {
                LOGGER.warn(REFERENCE_WARN);
            }
            if (this.scheme == null) {
                LOGGER.warn(SCHEME_WARN);
            }
            if (this.amount == null) {
                LOGGER.warn(AMOUNT_WARN);
            }
            if (this.currency == null) {
                LOGGER.warn(CURRENCY_WARN);
            }
            if (this.action == null) {
                LOGGER.warn(ACTION_WARN);
            }
            if (this.direction == null) {
                LOGGER.warn(DIRECTION_WARN);
            } else if (!"IN".equals(this.direction) && !"OUT".equals(this.direction)) {
                LOGGER.warn(WRONG_DIRECTION_WARN);
            } else if ("OUT".equals(this.direction)) {

                if (this.creditor == null) {
                    LOGGER.warn(CREDITOR_WARN);
                }

                if (this.subscriber == null) {
                    LOGGER.warn(SUBSCRIBER_WARN);
                }
            }


            return this;
        }

        public Payment build() {
            return new Payment(this.verifyIntegrity());
        }


    }
}
