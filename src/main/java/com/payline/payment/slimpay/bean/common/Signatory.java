package com.payline.payment.slimpay.bean.common;

import com.payline.payment.slimpay.utils.Required;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Signatory extends SlimpayBean {

    private static final Logger LOGGER = LogManager.getLogger(Signatory.class);
    protected static final String FAMILY_NAME_WARN = "Signatory must have a familyName when built";
    protected static final String GIVEN_NAME_WARN = "Signatory must have a givenName when built";
    protected static final String TELEPHONE_WARN = "Signatory must have a telephone in international format when built";

    //Mr/Miss/Mrs
    private String honorificPrefix;
    @Required
    private String familyName;
    @Required
    private String givenName;
    private String email;
    //must a start by +
    private String telephone;
    private BillingAddress billingAddress;

    public String getHonorificPrefix() {
        return honorificPrefix;
    }

    public String getfamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public Signatory(Signatory.Builder builder) {
        this.honorificPrefix = builder.honorificPrefix;
        this.familyName = builder.familyName;
        this.givenName = builder.givenName;
        this.email = builder.email;
        this.telephone = builder.telephone;
        this.billingAddress = builder.billingAddress;
    }

    public static class Builder {
        private String honorificPrefix; //Mr/Miss/Mrs
        private String familyName;
        private String givenName;
        private String email;
        private String telephone; //must a start by +
        private BillingAddress billingAddress;

        public static Signatory.Builder aSignatoryBuilder() {
            return new Signatory.Builder();
        }

        public Signatory.Builder withHonorificPrefix(String honorificPrefix) {
            this.honorificPrefix = honorificPrefix;
            return this;
        }

        public Signatory.Builder withfamilyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Signatory.Builder withGivenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Signatory.Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Signatory.Builder withTelephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public Signatory.Builder withBilingAddress(BillingAddress billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        private Signatory.Builder verifyIntegrity() {

            //to do logger les champs manquants obligatoire ??
            if (this.familyName == null) {
                LOGGER.warn(FAMILY_NAME_WARN);
            }
            if (this.givenName == null) {
                LOGGER.warn(GIVEN_NAME_WARN);
            }
            if (this.telephone != null && !this.telephone.startsWith("+")) {
                LOGGER.warn(TELEPHONE_WARN);
            }
            return this;
        }

        public Signatory build() {
            return new Signatory(this.verifyIntegrity());
        }

    }
}
