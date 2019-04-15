package com.payline.payment.slimpay.utils.service;

import com.payline.payment.slimpay.utils.properties.constants.LogoConstants;
import com.payline.payment.slimpay.utils.properties.service.LogoProperties;
import com.payline.payment.slimpay.utils.properties.service.PropertiesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class LogoPropertiesTest {

    private PropertiesService service = LogoProperties.INSTANCE;

    @Test
    void getFilename() {
        Assertions.assertEquals(LogoConstants.LOGO_PROPERTIES, service.getFilename());
    }


    @Test
    void getProperties() {

        Properties properties = service.getProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertFalse(properties.isEmpty());
        Assertions.assertEquals(7, properties.size());
    }
}