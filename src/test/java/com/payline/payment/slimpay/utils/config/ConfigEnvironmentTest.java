package com.payline.payment.slimpay.utils.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigEnvironmentTest {


    private ConfigEnvironment environment;

    @Test
    public void testEnvDev() {
        environment = ConfigEnvironment.DEV;
        String prefix = environment.getPrefix();

        Assertions.assertNotNull(environment);
        Assertions.assertEquals("dev", prefix);
    }

    @Test
    public void testEnvProd() {
        environment = ConfigEnvironment.PROD;
        String prefix = environment.getPrefix();

        Assertions.assertNotNull(environment);
        Assertions.assertEquals("prod", prefix);
    }


}
