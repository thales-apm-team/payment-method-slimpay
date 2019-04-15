package com.payline.payment.slimpay.utils.service;

import com.payline.payment.slimpay.utils.properties.constants.ConfigurationConstants;
import com.payline.payment.slimpay.utils.properties.service.PropertiesService;
import com.payline.payment.slimpay.utils.properties.service.ReleaseProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class ReleasePropertiesTest {

    private PropertiesService service = ReleaseProperties.INSTANCE;

    @Test
    void getFilename() {
        Assertions.assertEquals(ConfigurationConstants.RELEASE_PROPERTIES, service.getFilename());
    }


    @Test
    void getProperties() {

        Properties properties = service.getProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertFalse(properties.isEmpty());
    }
}