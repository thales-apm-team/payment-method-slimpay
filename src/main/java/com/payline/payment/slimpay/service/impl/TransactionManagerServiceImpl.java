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

    @Override
    public Map<String, String> readAdditionalData(String additionalDataJson, String s1) {
        Map<String, String> additionalDataMap = new HashMap<>();
        Gson gson = new Gson();

        if (null != additionalDataJson) {
            try {
                PaymentResponseSuccessAdditionalData paymentResponseSuccessAdditionalData = gson.fromJson(additionalDataJson, PaymentResponseSuccessAdditionalData.class);
                additionalDataMap.put("mandateReference", paymentResponseSuccessAdditionalData.getMandateReference());
                additionalDataMap.put("mandateId", paymentResponseSuccessAdditionalData.getMandateId());
                additionalDataMap.put("orderId", paymentResponseSuccessAdditionalData.getOrderId());
                additionalDataMap.put("orderReference", paymentResponseSuccessAdditionalData.getOrderReference());
                additionalDataMap.put("paymentId", paymentResponseSuccessAdditionalData.getPaymentId());
                additionalDataMap.put("paymentReference", paymentResponseSuccessAdditionalData.getPaymentReference());

            } catch (JsonSyntaxException e) {
                LOGGER.error("Additional data syntax incorrect", e);
                throw new JsonSyntaxException("Additional data syntax incorrect [{}]", e);

            }
        }
        return additionalDataMap;
    }
}
