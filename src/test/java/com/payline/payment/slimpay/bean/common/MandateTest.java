package com.payline.payment.slimpay.bean.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultSignatory;


@PrepareForTest({Mandate.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MandateTest {

    private static final Logger LOGGER = LogManager.getLogger(MandateTest.class);

    public static final String CREATE_SEQUENCE_TYPE = "createSequenceType";
    public static final String SEPA_DIRECT_DEBIT_CORE = "SEPA.DIRECT_DEBIT.CORE";
    public static final String SEPA = "SEPA";
    public static final String REFERENCE = "reference";
    public static final String PAYMENT_SCHEME = "paymentScheme";
    public static final String SIGNATORY = "signatory";
    public static final String STANDARD = "standard";
    public static final String PAYMENT_REF_1 = "PAYMENT-REF-1";
    private Mandate mandate;

    private Logger mockLogger;

    @BeforeEach
    public void setUp() {

        mockLogger = Mockito.mock(Logger.class);

        Whitebox.setInternalState(Mandate.class, "LOGGER", mockLogger);

    }

    @Test
    public void testMandateOK() {

        mandate = Mandate.Builder.aMandateBuilder()
                .withReference(PAYMENT_REF_1)
                .withSignatory(createDefaultSignatory())
                .withStandard(SEPA)
                .withPaymentScheme(SEPA_DIRECT_DEBIT_CORE)
                .withCreateSequenceType(CREATE_SEQUENCE_TYPE)
                .build();
        String jsonMandate = mandate.toString();


        Mockito.verify(mockLogger, Mockito.never()).warn(Mockito.anyString());

        LOGGER.info(jsonMandate);
        Assertions.assertTrue(jsonMandate.contains(REFERENCE));
        Assertions.assertTrue(jsonMandate.contains(PAYMENT_SCHEME));
        Assertions.assertTrue(jsonMandate.contains(SIGNATORY));
        Assertions.assertTrue(jsonMandate.contains(STANDARD));
        Assertions.assertTrue(jsonMandate.contains(CREATE_SEQUENCE_TYPE));

    }


    @Test
    public void testMandateWoReference() {

        mandate = Mandate.Builder.aMandateBuilder()
                .withSignatory(createDefaultSignatory())
                .withStandard(SEPA)
                .withPaymentScheme(SEPA_DIRECT_DEBIT_CORE)
                .withCreateSequenceType(CREATE_SEQUENCE_TYPE)
                .build();

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Mandate.REFERENCE_WARN));

        String jsonMandate = mandate.toString();
        LOGGER.info(jsonMandate);
        Assertions.assertFalse(jsonMandate.contains(REFERENCE));
        Assertions.assertTrue(jsonMandate.contains(PAYMENT_SCHEME));
        Assertions.assertTrue(jsonMandate.contains(SIGNATORY));
        Assertions.assertTrue(jsonMandate.contains(STANDARD));
        Assertions.assertTrue(jsonMandate.contains(CREATE_SEQUENCE_TYPE));

    }


    @Test
    public void testMandateWoSignatory() {

        mandate = Mandate.Builder.aMandateBuilder()
                .withReference(PAYMENT_REF_1)
                .withStandard(SEPA)
                .withPaymentScheme(SEPA_DIRECT_DEBIT_CORE)
                .withCreateSequenceType(CREATE_SEQUENCE_TYPE)
                .build();
        String jsonMandate = mandate.toString();

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Mandate.SIGNATORY_WARN));

        LOGGER.info(jsonMandate);
        Assertions.assertTrue(jsonMandate.contains(REFERENCE));
        Assertions.assertTrue(jsonMandate.contains(PAYMENT_SCHEME));
        Assertions.assertFalse(jsonMandate.contains(SIGNATORY));
        Assertions.assertTrue(jsonMandate.contains(STANDARD));
        Assertions.assertTrue(jsonMandate.contains(CREATE_SEQUENCE_TYPE));

    }


    @Test
    public void testMandateWoPaymentScheme() {

        mandate = Mandate.Builder.aMandateBuilder()
                .withReference(PAYMENT_REF_1)
                .withSignatory(createDefaultSignatory())
                .withStandard(SEPA)
                .withCreateSequenceType(CREATE_SEQUENCE_TYPE)
                .build();
        String jsonMandate = mandate.toString();

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Mandate.PAYMENT_SCHEME_WARN));

        LOGGER.info(jsonMandate);
        Assertions.assertTrue(jsonMandate.contains(REFERENCE));
        Assertions.assertFalse(jsonMandate.contains(PAYMENT_SCHEME));
        Assertions.assertTrue(jsonMandate.contains(SIGNATORY));
        Assertions.assertTrue(jsonMandate.contains(STANDARD));
        Assertions.assertTrue(jsonMandate.contains(CREATE_SEQUENCE_TYPE));

    }


    @Test
    public void testMandateWoCreateSequenceType() {

        mandate = Mandate.Builder.aMandateBuilder()
                .withReference(PAYMENT_REF_1)
                .withSignatory(createDefaultSignatory())
                .withStandard(SEPA)
                .withPaymentScheme(SEPA_DIRECT_DEBIT_CORE)
                .build();
        String jsonMandate = mandate.toString();

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Mandate.CREATE_SEQUENCE_TYPE_WARN));

        LOGGER.info(jsonMandate);
        Assertions.assertTrue(jsonMandate.contains(REFERENCE));
        Assertions.assertTrue(jsonMandate.contains(PAYMENT_SCHEME));
        Assertions.assertTrue(jsonMandate.contains(SIGNATORY));
        Assertions.assertTrue(jsonMandate.contains(STANDARD));
        Assertions.assertFalse(jsonMandate.contains(CREATE_SEQUENCE_TYPE));

    }
}
