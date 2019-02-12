package com.payline.payment.slimpay.bean.common.request;

import com.payline.payment.slimpay.bean.common.Creditor;
import com.payline.payment.slimpay.bean.common.SlimPayOrderItem;
import com.payline.payment.slimpay.bean.common.SlimpayBean;
import com.payline.payment.slimpay.bean.common.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlimpayOrderRequest extends SlimpayBean {

    private static final transient Logger LOGGER = LogManager.getLogger(SlimpayOrderRequest.class);


    public String reference;
    public String paymentScheme;
    public String locale;
    public Creditor creditor;
    public Subscriber subscriber;
    public boolean started; //must be true to automatically start order after creation
    public boolean mandateReused; //must be true to automatically start order after creation
    public boolean sendUserApproval; // if approval link will be sent to subscriber email after finishing order
    public SlimPayOrderItem[] items;
    public String successUrl;
    public String cancelUrl;
    public String failureUrl;






    public String getReference() {
        return reference;
    }

    public String getPaymentScheme() {
        return paymentScheme;
    }

    public String getLocale() {
        return locale;
    }

    public Creditor getCreditor() {
        return creditor;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isMandateReused() {
        return mandateReused;
    }

    public boolean isSendUserApproval() {
        return sendUserApproval;
    }

    public SlimPayOrderItem[] getItems() {
        return items;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public String getFailureUrl() {
        return failureUrl;
    }

    public SlimpayOrderRequest(Builder builder) {

        this.reference = builder.reference;
        this.paymentScheme = builder.paymentScheme;
        this.locale = builder.locale;
        this.creditor = builder.creditor;
        this.subscriber = builder.subscriber;
        this.started = builder.started;
        this.mandateReused =builder.mandateReused;
        this.sendUserApproval = builder.sendUserApproval;
        this.items = builder.items;
        this.successUrl = builder.successUrl;
        this.cancelUrl = builder.cancelUrl;
        this.failureUrl = builder.failureUrl;
    }

    public static class Builder {
        public String reference;
        public String paymentScheme;
        public String locale;
        public Creditor creditor;
        public Subscriber subscriber;
        public boolean started; //must be true to automatically start order after creation
        public boolean mandateReused;
        public boolean sendUserApproval; // if approval link will be sent to subscriber email after finishing order
        public SlimPayOrderItem[] items;
        public String successUrl;
        public String cancelUrl;
        public String failureUrl;


        public static SlimpayOrderRequest.Builder aSlimPayOrderRequestBuilder() {
            return new SlimpayOrderRequest.Builder();
        }

        public SlimpayOrderRequest.Builder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public SlimpayOrderRequest.Builder withPaymentScheme(String paymentScheme) {
            this.paymentScheme = paymentScheme;
            return this;
        }

        public SlimpayOrderRequest.Builder withLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public SlimpayOrderRequest.Builder withCreditor(Creditor creditor) {
            this.creditor = creditor;
            return this;
        }

        public SlimpayOrderRequest.Builder withSubscriber(Subscriber subscriber) {
            this.subscriber = subscriber;
            return this;
        }

        public SlimpayOrderRequest.Builder withStarted(boolean started) {
            this.started = started;
            return this;
        }

        public SlimpayOrderRequest.Builder withMandateReused(boolean mandateReused) {
            this.mandateReused = mandateReused;
            return this;
        }

        public SlimpayOrderRequest.Builder withSendUserApproval(boolean sendUserApproval) {
            this.sendUserApproval = sendUserApproval;
            return this;
        }

        public SlimpayOrderRequest.Builder withItems(SlimPayOrderItem[] items) {
            this.items = items;
            return this;
        }

        public SlimpayOrderRequest.Builder withSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
            return this;
        }

        public SlimpayOrderRequest.Builder withCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
            return this;
        }

        public SlimpayOrderRequest.Builder withFailureUrl(String failureUrl) {
            this.failureUrl = failureUrl;
            return this;
        }

        private SlimpayOrderRequest.Builder verifyIntegrity() {
            //to do logger les champs manquants obligatoire ??
            if (this.creditor == null) {
                LOGGER.warn ("SlimpayOrderRequest must have a creditor when built");
            }
            if (this.subscriber == null) {
                LOGGER.warn ("SlimpayOrderRequest must have a subscriber when built");
            }
            if (this.items == null) {
                LOGGER.warn ("SlimpayOrderRequest must have a items when built");
            }
            if (this.successUrl == null) {
                LOGGER.warn ("SlimpayOrderRequest must have a successUrl when built");
            }
            if (this.failureUrl == null) {
                LOGGER.warn ("SlimpayOrderRequest must have a failureUrl when built");
            }
            return this;
        }

        public SlimpayOrderRequest build(){
            return new SlimpayOrderRequest(this.verifyIntegrity());

        }
    }
}
