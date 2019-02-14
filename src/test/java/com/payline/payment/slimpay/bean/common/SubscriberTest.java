package com.payline.payment.slimpay.bean.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubscriberTest {

    private Subscriber subscriber;

    @Test
    public void testCreditor(){
        subscriber = new Subscriber("client");

        Assertions.assertNotNull(subscriber);
        Assertions.assertEquals("client",subscriber.getReference());
    }
}
