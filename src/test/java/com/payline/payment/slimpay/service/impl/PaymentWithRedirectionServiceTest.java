package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentWithRedirectionServiceTest {

    @InjectMocks
    public PaymentWithRedirectionServiceImpl service;

    @Spy
    SlimpayHttpClient httpClient;

    @BeforeAll
    public void setup() {
        service = new PaymentWithRedirectionServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void finalizeRedirectionPaymentOK() throws Exception {
        // TODO
    }

    @Test
    public void finalizeRedirectionPaymentKO() throws Exception {
        // TODO
    }

    @Test
    public void handleSessionExpiredKo() throws Exception {
        // TODO
    }

    @Test
    public void handleSessionExpiredOk() throws Exception {
        // TODO
    }
}
