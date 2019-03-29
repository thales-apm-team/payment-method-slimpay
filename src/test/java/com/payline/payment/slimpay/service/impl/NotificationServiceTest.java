package com.payline.payment.slimpay.service.impl;

import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationServiceTest {


    NotificationServiceImpl service;

    @BeforeAll
    public void setup() {
        service = new NotificationServiceImpl();
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void parse() {
        NotificationRequest notificationRequest = Mockito.mock(NotificationRequest.class);
        NotificationResponse response = service.parse(notificationRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    public void notifyTransactionStatus() {
        NotifyTransactionStatusRequest notifyTransactionStatusRequest = Mockito.mock(NotifyTransactionStatusRequest.class);
        service.notifyTransactionStatus(notifyTransactionStatusRequest);
        // void ras
    }
}
