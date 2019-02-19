package com.payline.payment.slimpay.bean.common;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

@PrepareForTest({BillingAddress.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BillingAddressTest {

    private  BillingAddress address;

    private Logger mockLogger;

    @BeforeEach
    public void setUp() {

        mockLogger = Mockito.mock(Logger.class);

        Whitebox.setInternalState(BillingAddress.class, "LOGGER", mockLogger);
    }


    @Test
    public  void billingAddressTestOK(){
        address = BillingAddress.Builder.aBillingAddressBuilder()
                .withStreet1("10 rue de la paix")
                .withStreet2("residence peace")
                .withCity("Versailles")
                .withCountry("FR")
                .withPostalCode("78000")
                .build();

        String jsonAddress= address.toString();
        Assertions.assertTrue(jsonAddress.contains("10 rue de la paix"));
        Assertions.assertTrue(jsonAddress.contains("residence peace"));
        Assertions.assertTrue(jsonAddress.contains("Versailles"));
        Assertions.assertTrue(jsonAddress.contains("FR"));
        Assertions.assertTrue(jsonAddress.contains("78000"));
    }

    @Test
    public  void billingAddressTestKO(){
        address = BillingAddress.Builder.aBillingAddressBuilder()
                .build();

        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(BillingAddress.STREET_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(BillingAddress.CITY_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(BillingAddress.COUNTRY_WARN));
        Mockito.verify(mockLogger, Mockito.times(1)).warn(Mockito.eq(BillingAddress.POSTAL_CODE_WARN));

//        String jsonAddress= address.toString();

    }
}
