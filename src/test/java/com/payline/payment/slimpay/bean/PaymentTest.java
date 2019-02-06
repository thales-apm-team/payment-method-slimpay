package com.payline.payment.slimpay.bean;

import com.payline.payment.slimpay.bean.common.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PaymentTest {

    private Payment payment;

    @Test
    public void testPaymentOK(){
        payment = Payment.Builder.aPaymentBuilder()
                .withReference("PAYMENT-REF-1")
                .withScheme("SEPA.DIRECT_DEBIT.CORE")
                .withDirection("IN")
                .withAction("create")
                .withAmount("100")
                .withCurrency("EUR")
                .withLabel("the label")
                .build();
        String jsonPayment = payment.toString();
        Assertions.assertTrue(jsonPayment.contains("reference"));
        Assertions.assertTrue(jsonPayment.contains("scheme"));
        Assertions.assertTrue(jsonPayment.contains("direction"));
        Assertions.assertTrue(jsonPayment.contains("amount"));
        Assertions.assertTrue(jsonPayment.contains("currency"));
        Assertions.assertTrue(jsonPayment.contains("label"));
    }


    @Test
    public void testPaymentKO(){
        payment = Payment.Builder.aPaymentBuilder()
                .withAction("payin")
                .withLabel("the label")
                .build();
        String jsonPayment = payment.toString();
        Assertions.assertFalse(jsonPayment.contains("amount"));
        Assertions.assertFalse(jsonPayment.contains("currency"));
        Assertions.assertFalse(jsonPayment.contains("scheme"));
        Assertions.assertFalse(jsonPayment.contains("direction"));
        Assertions.assertFalse(jsonPayment.contains("reference"));
        //faire test sur les logs
    }



    @Test
    public void testPaymentWithWrongDirection(){
        payment = Payment.Builder.aPaymentBuilder()
                .withDirection("ouest")
                .build();
        System.out.println(payment);
        String jsonPayment = payment.toString();
        Assertions.assertTrue(jsonPayment.contains("direction"));
        //todo faire test sur les logs

    }
}
