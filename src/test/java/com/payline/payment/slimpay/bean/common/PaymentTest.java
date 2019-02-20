package com.payline.payment.slimpay.bean.common;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

@PrepareForTest({Payment.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentTest {

    private Payment payment;


    private Logger mockLogger;

    @BeforeEach
    public void setUp() {

        mockLogger = Mockito.mock(Logger.class);

        Whitebox.setInternalState(Payment.class, "LOGGER", mockLogger);
    }


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
        //test on logs
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.AMOUNT_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.DIRECTION_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.CURRENCY_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.REFERENCE_WARN));
    }



    @Test
    public void testPaymentWithWrongDirection(){
        payment = Payment.Builder.aPaymentBuilder()
                .withDirection("ouest")
                .build();
        System.out.println(payment);
        String jsonPayment = payment.toString();
        Assertions.assertTrue(jsonPayment.contains("direction"));

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.WRONG_DIRECTION_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.AMOUNT_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.CURRENCY_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Payment.REFERENCE_WARN));
    }
}
