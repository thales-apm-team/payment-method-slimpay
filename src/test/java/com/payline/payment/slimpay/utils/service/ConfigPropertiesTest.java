package com.payline.payment.slimpay.utils.service;

import com.payline.payment.slimpay.utils.properties.constants.ConfigurationConstants;
import com.payline.payment.slimpay.utils.properties.service.ConfigProperties;
import com.payline.payment.slimpay.utils.properties.service.PropertiesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class ConfigPropertiesTest {

    private PropertiesService service = ConfigProperties.INSTANCE;

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
        key = ConfigProperties.INSTANCE.get("BadKey");
        Assertions.assertNull(key);

    }

    @Test
    public void getFromKeyOK() {
        key = ConfigProperties.INSTANCE.get("http.connectTimeout");
        Assertions.assertNotNull(key);
    }

}