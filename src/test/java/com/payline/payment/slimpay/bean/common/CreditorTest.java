package com.payline.payment.slimpay.bean.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreditorTest {

    private Creditor creditor;

    @Test
    public void testCreditor(){
        creditor = new Creditor("creditor");

        Assertions.assertNotNull(creditor);
        Assertions.assertEquals("creditor",creditor.getReference());
    }
}
