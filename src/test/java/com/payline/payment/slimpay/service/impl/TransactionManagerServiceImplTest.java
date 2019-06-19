package com.payline.payment.slimpay.service.impl;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;


public class TransactionManagerServiceImplTest {

    private Map<String, String> additionalData;
    private TransactionManagerServiceImpl service = new TransactionManagerServiceImpl();
    @Test
    public void readAdditionalDataKo() {
        String malformedJson = "{mandateReference: \"RUMTEST01\",mandateId: \"Transaction01\", paymentReference: \"007\"";
        Assertions.assertThrows(JsonSyntaxException.class,()-> {
            additionalData = service.readAdditionalData(malformedJson, "PaymentResponseSuccessAdditionalData");
        });

    }


    @Test
    public void readAdditionalData() {
        String dataJson = "{mandateReference: \"ref0001\",mandateId: \"id001\"," +
                "orderReference: \"ref0001\",orderId: \"Transaction01\"," +
                "paymentReference: \"ref0001\",paymentId: \"Transaction01\"}";
        additionalData = service.readAdditionalData(dataJson, "PaymentResponseSuccessAdditionalData");
        Assertions.assertNotNull(additionalData);
        Assertions.assertEquals(5, additionalData.size());
        Assertions.assertEquals("ref0001", additionalData.get("mandateReference"));
        Assertions.assertEquals("ref0001", additionalData.get("orderReference"));
        Assertions.assertEquals("Transaction01", additionalData.get("orderId"));
        Assertions.assertEquals("ref0001", additionalData.get("paymentReference"));
        Assertions.assertEquals("Transaction01", additionalData.get("paymentId"));

    }

    @Test
    public void readAdditionalDataNull() {
        final TransactionManagerServiceImpl tmsi = new TransactionManagerServiceImpl();
        final Map<String, String> addData = tmsi.readAdditionalData(null, null);
        Assertions.assertTrue(addData.isEmpty());
    }


}
