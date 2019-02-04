package com.payline.payment.slimpay.bean.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mandate extends SlimpayBean {

    private static final transient Logger LOGGER = LogManager.getLogger(Mandate.class);

    private String reference;
    private String action;
    private boolean autoGenReference;
    private Signatory signatory;
    private String paymentScheme;
    private String createSequenceType;
    private String standard;
    private String dateSigned; //DateTime, ISO8601,

    private  Mandate(){}

    private Mandate(Mandate.Builder builder) {
        this.reference = builder.reference;
        this.action = builder.action;
        this.autoGenReference = builder.autoGenReference;
        this.signatory = builder.signatory;
        this.paymentScheme = builder.paymentScheme;
        this.createSequenceType = builder.createSequenceType;
        this.dateSigned = builder.dateSigned;
        this.standard = builder.standard;
    }

    public static class Builder {
        private String reference;
        private String action;
        private boolean autoGenReference;
        private Signatory signatory;
        private String paymentScheme;
        private String createSequenceType;
        private String dateSigned; //DateTime, ISO8601,
        private String standard;

        public static Mandate.Builder aMandateBuilder(){
            return new Mandate.Builder();
        }

        public Mandate.Builder withReference(String ref){
            this.reference = ref;
            return this;
        }

        public Mandate.Builder withStandard(String standard){
            this.standard = standard;
            return this;
        }


        public Mandate.Builder withAction(String action){
            this.action = action;
            return this;
        }


        public Mandate.Builder withAutoGenReference(boolean autoGenReference){
            this.autoGenReference = autoGenReference;
            return this;
        }

        public Mandate.Builder withPaymentScheme(String paymentScheme){
            this.paymentScheme = paymentScheme;
            return this;
        }


        public Mandate.Builder withCreateSequenceType(String createSequenceType){
            this.createSequenceType = createSequenceType;
            return this;
        }


        public Mandate.Builder withDateSigned(String dateSigned){
            this.dateSigned = dateSigned;
            return this;
        }
        public Mandate.Builder withSignatory(Signatory signatory){
            this.signatory = signatory;
            return this;
        }

        public Mandate.Builder verifyIntegrity() {

            //to do logger les champs manquants obligatoire ??
            if (this.reference == null) {
                LOGGER.warn ("Mandate must have a reference when built");
            }
            if (this.signatory == null) {
                LOGGER.warn ("Mandate must have a signatory when built");
            }

            if (this.paymentScheme == null) {
                LOGGER.warn ("Mandate must have a paymentScheme when built");
            }
            if (this.action == null) {
                LOGGER.warn ("Mandate must have a action when built");
            }
            if (this.createSequenceType == null) {
                LOGGER.warn ("Mandate must have a createSequenceType when built");
            }

            if (this.action != null && ( this.action !="sign" || this.action !="amendBankAccount") ) {
                LOGGER.warn ("Mandate action value must be 'sign' or 'amendBankAccount' ");
            }

            return this;
        }

        public Mandate build() {
            return new Mandate(this.verifyIntegrity());
        }

    }

}
