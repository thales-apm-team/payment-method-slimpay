package com.payline.payment.slimpay.bean.common;

import com.payline.payment.slimpay.utils.Required;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mandate extends SlimpayBean {

    private static final Logger LOGGER = LogManager.getLogger(Mandate.class);

    protected static final String REFERENCE_WARN = "Mandate must have a reference when built";
    protected static final String SIGNATORY_WARN = "Mandate must have a signatory when built";
    protected static final String PAYMENT_SCHEME_WARN = "Mandate must have a paymentScheme when built";
    protected static final String CREATE_SEQUENCE_TYPE_WARN = "Mandate must have a createSequenceType when built";

    @Required
    private String reference;
    private String action;
    private boolean autoGenReference;
    @Required
    private Signatory signatory;
    @Required
    private String paymentScheme;
    @Required
    private String createSequenceType;
    private String sequenceType;
    private String standard;
    private String dateSigned; //DateTime, ISO8601,

    public String getReference() {
        return reference;
    }

    public String getAction() {
        return action;
    }

    public boolean isAutoGenReference() {
        return autoGenReference;
    }

    public Signatory getSignatory() {
        return signatory;
    }

    public String getPaymentScheme() {
        return paymentScheme;
    }

    public String getCreateSequenceType() {
        return createSequenceType;
    }

    public String getStandard() {
        return standard;
    }

    public String getDateSigned() {
        return dateSigned;
    }

    public String getSequenceType() {
        return sequenceType;
    }

    private Mandate() {
    }

    private Mandate(Mandate.Builder builder) {
        this.reference = builder.reference;
        this.action = builder.action;
        this.autoGenReference = builder.autoGenReference;
        this.signatory = builder.signatory;
        this.paymentScheme = builder.paymentScheme;
        this.createSequenceType = builder.createSequenceType;
        this.dateSigned = builder.dateSigned;
        this.standard = builder.standard;
        this.sequenceType = builder.sequenceType;
    }

    public static class Builder {
        private String reference;
        private String action;
        private boolean autoGenReference;
        private Signatory signatory;
        private String paymentScheme;
        private String createSequenceType;
        private String sequenceType;
        //DateTime, ISO8601,
        private String dateSigned;
        private String standard;

        public static Mandate.Builder aMandateBuilder() {
            return new Mandate.Builder();
        }

        public Mandate.Builder withReference(String ref) {
            this.reference = ref;
            return this;
        }

        public Mandate.Builder withStandard(String standard) {
            this.standard = standard;
            return this;
        }


        public Mandate.Builder withAction(String action) {
            this.action = action;
            return this;
        }


        public Mandate.Builder withAutoGenReference(boolean autoGenReference) {
            this.autoGenReference = autoGenReference;
            return this;
        }

        public Mandate.Builder withPaymentScheme(String paymentScheme) {
            this.paymentScheme = paymentScheme;
            return this;
        }


        public Mandate.Builder withCreateSequenceType(String createSequenceType) {
            this.createSequenceType = createSequenceType;
            return this;
        }


        public Mandate.Builder withSequenceType(String sequenceType) {
            this.sequenceType = sequenceType;
            return this;
        }

        public Mandate.Builder withSignatory(Signatory signatory) {
            this.signatory = signatory;
            return this;
        }

        public Mandate.Builder verifyIntegrity() {

            if (this.reference == null) {
                LOGGER.warn(REFERENCE_WARN);
            }
            if (this.signatory == null) {
                LOGGER.warn(SIGNATORY_WARN);
            }

            if (this.paymentScheme == null) {
                LOGGER.warn(PAYMENT_SCHEME_WARN);
            }
            if (this.createSequenceType == null) {
                LOGGER.warn(CREATE_SEQUENCE_TYPE_WARN);
            }

            return this;
        }

        public Mandate build() {
            return new Mandate(this.verifyIntegrity());
        }

    }

}
