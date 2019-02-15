package com.payline.payment.slimpay.bean.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.payline.payment.slimpay.bean.common.SlimpayBean;

public class PaymentResponseSuccessAdditionalData extends SlimpayBean {

    @SerializedName("mandateReference")
    private String mandateReference;

    @SerializedName("mandateId")
    private String mandateId;

    @SerializedName("paymentReference")
    private String paymentReference;

    @SerializedName("paymentId")
    private String paymentId;

    @SerializedName("orderReference")
    private String orderReference;

    @SerializedName("orderId")
    private String orderId;


    private PaymentResponseSuccessAdditionalData() {
        //ras
    }

    public String getMandateReference() {
        return mandateReference;
    }

    public String getMandateId() {
        return mandateId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public String getOrderId() {
        return orderId;
    }

    public static PaymentResponseSuccessAdditionalData fromJson(String jsonContent) {
        Gson gson = new Gson();
        return gson.fromJson(jsonContent, PaymentResponseSuccessAdditionalData.class);
    }

    public static final class Builder {
        private String mandateReference;
        private String mandateId;
        private String paymentReference;
        private String paymentId;
        private String orderReference;
        private String orderId;

        private Builder() {
        }

        public static PaymentResponseSuccessAdditionalData.Builder aPaymentResponseSuccessAdditionalData() {
            return new PaymentResponseSuccessAdditionalData.Builder();
        }

        public PaymentResponseSuccessAdditionalData.Builder withMandateReference(String mandateReference) {
            this.mandateReference = mandateReference;
            return this;
        }

        public PaymentResponseSuccessAdditionalData.Builder withMandateId(String mandateId) {
            this.mandateId = mandateId;
            return this;
        }

        public PaymentResponseSuccessAdditionalData.Builder withPaymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public PaymentResponseSuccessAdditionalData.Builder withPaymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public PaymentResponseSuccessAdditionalData.Builder withOrderReference(String orderReference) {
            this.orderReference = orderReference;
            return this;
        }

        public PaymentResponseSuccessAdditionalData.Builder withOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public PaymentResponseSuccessAdditionalData build() {
            PaymentResponseSuccessAdditionalData paymentResponseSuccessAdditionalData = new PaymentResponseSuccessAdditionalData();
            paymentResponseSuccessAdditionalData.mandateId = this.mandateId;
            paymentResponseSuccessAdditionalData.mandateReference = this.mandateReference;
            paymentResponseSuccessAdditionalData.paymentId = this.paymentId;
            paymentResponseSuccessAdditionalData.orderReference = this.orderReference;
            paymentResponseSuccessAdditionalData.paymentReference = this.paymentReference;
            paymentResponseSuccessAdditionalData.orderId = this.orderId;
            return paymentResponseSuccessAdditionalData;
        }
    }
}
