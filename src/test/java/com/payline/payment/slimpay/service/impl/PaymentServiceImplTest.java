package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentServiceImplTest {


    @Spy
    SlimpayHttpClient httpClient;

    @InjectMocks
    PaymentServiceImpl service;


    @BeforeAll
    public void setup() {
        service = new PaymentServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void paymentRequestOK() throws Exception {
        // TODO
    }

    @Test
    public void paymentRequestKO() throws Exception {
        // TODO
    }

}
