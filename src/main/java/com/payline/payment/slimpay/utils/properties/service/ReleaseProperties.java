package com.payline.payment.slimpay.utils.properties.service;

import com.payline.payment.slimpay.utils.properties.constants.ConfigurationConstants;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Utility class which reads and provides config properties.
 */
public enum ReleaseProperties implements PropertiesService {

    INSTANCE;

    private static final String FILENAME = ConfigurationConstants.RELEASE_PROPERTIES;
    private static final Logger LOGGER = LogManager.getLogger(ReleaseProperties.class);

    private final Properties properties;

    /* This class has only static methods: no need to instantiate it */
    ReleaseProperties() {
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

    @Override
    public void logError(String message, Throwable t) {
        LOGGER.error( message, t );
    }

}
