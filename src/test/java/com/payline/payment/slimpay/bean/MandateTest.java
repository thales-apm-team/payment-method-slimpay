package com.payline.payment.slimpay.bean;


import com.payline.payment.slimpay.bean.common.Mandate;
import com.payline.payment.slimpay.bean.common.Signatory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MandateTest {

    private Mandate mandate;

    @Test
    public void testMandateOK() {
        mandate = Mandate.Builder.aMandateBuilder()
                .withReference("PAYMENT-REF-1")
                .withAction("create")
                .withSignatory(Mockito.any(Signatory.class))
                .build();
        String jsonMandate = mandate.toString();
        Assertions.assertTrue(jsonMandate.contains("reference"));
        Assertions.assertTrue(jsonMandate.contains("scheme"));
        Assertions.assertTrue(jsonMandate.contains("signatory"));

    }
}
