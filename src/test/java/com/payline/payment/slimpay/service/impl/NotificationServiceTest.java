package com.payline.payment.slimpay.service.impl;

import com.payline.payment.slimpay.utils.http.SlimpayHttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationServiceTest {


    @Spy
    SlimpayHttpClient httpClient;

    @InjectMocks
    NotificationServiceImpl service;

    @BeforeAll
    public void setup() {
        service = new NotificationServiceImpl();
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void parse() {
        // TODO

    }

    @Test
    public void notifyTransactionStatus() {
        // TODO
    }
}
