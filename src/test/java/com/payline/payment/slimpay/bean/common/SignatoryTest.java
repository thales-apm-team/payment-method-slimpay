package com.payline.payment.slimpay.bean.common;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import static com.payline.payment.slimpay.utils.BeansUtils.createDefaultBillingAddress;

@PrepareForTest({Signatory.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignatoryTest {

    private Signatory signatory;

    private Logger mockLogger;

    @BeforeEach
    public void setUp() {

        mockLogger = Mockito.mock(Logger.class);

        Whitebox.setInternalState(Signatory.class, "LOGGER", mockLogger);
    }

    @Test
    public void signatoryOk(){
        signatory = Signatory.Builder.aSignatoryBuilder()
                .withfamilyName("Doe")
                .withGivenName("John")
                .withHonorificPrefix("Mr")
                .withBilingAddress(createDefaultBillingAddress())
                .withEmail("toto@emailcom")
                .withTelephone("+33725262729")
                .build();

        String jsonSignatory = signatory.toString();
        Assertions.assertTrue(jsonSignatory.contains("Doe"));
        Assertions.assertTrue(jsonSignatory.contains("John"));
        Assertions.assertTrue(jsonSignatory.contains("toto@emailcom"));
        Assertions.assertTrue(jsonSignatory.contains("+33725262729"));
        Assertions.assertTrue(jsonSignatory.contains("Mr"));
        Assertions.assertTrue(jsonSignatory.contains("billingAddress"));
    }

    @Test
    public void signatoryKO(){
        signatory = Signatory.Builder.aSignatoryBuilder()
                .withHonorificPrefix("Mr")
                .withBilingAddress(createDefaultBillingAddress())
                .withEmail("toto@emailcom")
                .withTelephone("+33725262729")
                .build();
        //test on logs
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Signatory.FAMILY_NAME_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Signatory.GIVEN_NAME_WARN));

    }

    @Test
    public void signatoryWrongTelephoneFormat(){
        signatory = Signatory.Builder.aSignatoryBuilder()
                .withHonorificPrefix("Mr")
                .withfamilyName("Doe")
                .withGivenName("John")
                .withBilingAddress(createDefaultBillingAddress())
                .withEmail("toto@emailcom")
                .withTelephone("000")
                .build();
        //test on logs
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(Signatory.TELEPHONE_WARN));

    }
}
