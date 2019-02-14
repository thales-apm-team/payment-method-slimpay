package com.payline.payment.slimpay.bean.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BillingAddressTest {

    private  BillingAddress address;

    @Test
    public  void billingAdressTestOK(){
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
    public  void billingAdressTestKO(){
        address = BillingAddress.Builder.aBillingAddressBuilder()
                .build();

        String jsonAddress= address.toString();
//todo test sur le log d'erreurs 4 lignes attendues
    }
}
