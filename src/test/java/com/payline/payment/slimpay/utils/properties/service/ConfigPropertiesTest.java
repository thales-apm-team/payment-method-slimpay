package com.payline.payment.slimpay.utils.properties.service;

import com.payline.payment.slimpay.utils.properties.constants.ConfigurationConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class ConfigPropertiesTest {

    private ConfigProperties service = ConfigProperties.getInstance();

    private String key;

    @Test
    void getFilename() {
        Assertions.assertEquals(ConfigurationConstants.CONFIG_PROPERTIES, service.getFilename());
    }

    @Test
    void getProperties() {
        Properties properties = service.getProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertFalse(properties.isEmpty());
    }

    @Test
    public void getFromKeyKO() {
        key = service.get("BadKey");
        Assertions.assertNull(key);
    }

    @Test
    public void getFromKeyOK() {
        key = service.get("http.connectTimeout");
        Assertions.assertNotNull(key);
    }

}