package com.payline.payment.slimpay.utils.properties.service;

import java.util.Properties;

/**
 * Utility class which reads and provides config properties.
 */
public enum ConfigPropertiesEnum implements PropertiesService {

    INSTANCE;

    private static final String FILENAME = "config.properties";

    private final Properties properties;

    /* This class has only static methods: no need to instantiate it */
    ConfigPropertiesEnum() {
        properties = new Properties();
        // init of the Properties
        readProperties(properties);
    }


    @Override
    public String getFilename() {
        return FILENAME;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

}
