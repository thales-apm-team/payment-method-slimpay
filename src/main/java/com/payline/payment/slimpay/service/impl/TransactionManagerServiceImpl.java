package com.payline.payment.slimpay.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.payline.payment.slimpay.bean.response.PaymentResponseSuccessAdditionalData;
import com.payline.pmapi.service.TransactionManagerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class TransactionManagerServiceImpl implements TransactionManagerService {

    private static final Logger LOGGER = LogManager.getLogger(TransactionManagerServiceImpl.class);
    private static final String MANDATE_REFERENCE = "mandateReference";
    private static final String ORDER_ID = "orderId";
    private static final String ORDER_REFERENCE = "orderReference";
    private static final String PAYMENT_ID = "paymentId";
    private static final String PAYMENT_REFERENCE = "paymentReference";

    @Override
    public Map<String, String> readAdditionalData(String additionalDataJson, String s1) {
        Map<String, String> additionalDataMap = new HashMap<>();
        Gson gson = new Gson();

        if (null != additionalDataJson) {
            try {
                PaymentResponseSuccessAdditionalData paymentResponseSuccessAdditionalData = gson.fromJson(additionalDataJson, PaymentResponseSuccessAdditionalData.class);
                additionalDataMap.put(MANDATE_REFERENCE, paymentResponseSuccessAdditionalData.getMandateReference());
                additionalDataMap.put(ORDER_ID, paymentResponseSuccessAdditionalData.getOrderId());
                additionalDataMap.put(ORDER_REFERENCE, paymentResponseSuccessAdditionalData.getOrderReference());
                additionalDataMap.put(PAYMENT_ID, paymentResponseSuccessAdditionalData.getPaymentId());
                additionalDataMap.put(PAYMENT_REFERENCE, paymentResponseSuccessAdditionalData.getPaymentReference());

            } catch (JsonSyntaxException e) {
                LOGGER.error("Additional data syntax incorrect", e);
                throw new JsonSyntaxException("Additional data syntax incorrect [{}]", e);
            }
        }
        return additionalDataMap;
    }
}
