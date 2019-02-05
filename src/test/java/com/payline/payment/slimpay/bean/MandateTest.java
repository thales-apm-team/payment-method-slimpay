package com.payline.payment.slimpay.bean;


import com.payline.payment.slimpay.bean.common.Mandate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultSignatory;

public class MandateTest {

    private Mandate mandate;

    @Test
    public void testMandateOK() {
        mandate = Mandate.Builder.aMandateBuilder()
                .withReference("PAYMENT-REF-1")
                .withSignatory(createDefaultSignatory())
                .withStandard("SEPA")
                .withPaymentScheme("SEPA.DIRECT_DEBIT.CORE")
                .build();
        String jsonMandate = mandate.toString();
        System.out.println(jsonMandate);
        Assertions.assertTrue(jsonMandate.contains("reference"));
        Assertions.assertTrue(jsonMandate.contains("paymentScheme"));
        Assertions.assertTrue(jsonMandate.contains("signatory"));
        Assertions.assertTrue(jsonMandate.contains("standard"));

    }
}
