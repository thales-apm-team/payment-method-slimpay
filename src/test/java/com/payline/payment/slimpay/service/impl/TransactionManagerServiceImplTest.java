package com.payline.payment.slimpay.service.impl;

import com.google.gson.JsonSyntaxException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

public class TransactionManagerServiceImplTest {


    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private Map<String, String> additionalData;
    private TransactionManagerServiceImpl service;

    @Before
    public void setup() {
        service = new TransactionManagerServiceImpl();

    }

    @Test
    public void readAdditionalDataKo() {
        expectedEx.expect(JsonSyntaxException.class);
        expectedEx.expectMessage("Additional data syntax incorrect [{}]");
        String malformedJson = "{mandateReference: \"RUMTEST01\",mandateId: \"Transaction01\", paymentReference: \"007\"";
        additionalData = service.readAdditionalData(malformedJson, "PaymentResponseSuccessAdditionalData");


    }

    @Test
    public void readAdditionalData() {
        String dataJson = "{mandateReference: \"ref0001\",mandateId: \"id001\"," +
                "orderReference: \"ref0001\",orderId: \"Transaction01\"," +
                "paymentReference: \"ref0001\",paymentId: \"Transaction01\"}";
        additionalData = service.readAdditionalData(dataJson, "PaymentResponseSuccessAdditionalData");
        Assert.assertNotNull(additionalData);
        Assert.assertEquals(6, additionalData.size());
        Assert.assertEquals("ref0001", additionalData.get("mandateReference"));
        Assert.assertEquals("id001", additionalData.get("mandateId"));
        Assert.assertEquals("ref0001", additionalData.get("orderReference"));
        Assert.assertEquals("Transaction01", additionalData.get("orderId"));
        Assert.assertEquals("ref0001", additionalData.get("paymentReference"));
        Assert.assertEquals("Transaction01", additionalData.get("paymentId"));

    }

    @Test
    public void readAdditionalDataNull() {
        final TransactionManagerServiceImpl tmsi = new TransactionManagerServiceImpl();
        final Map<String, String> addData = tmsi.readAdditionalData(null, null);
        Assert.assertTrue(addData.isEmpty());
    }
}
