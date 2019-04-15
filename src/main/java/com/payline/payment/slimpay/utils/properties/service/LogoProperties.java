package com.payline.payment.slimpay.utils.properties.service;

import com.payline.payment.slimpay.utils.properties.constants.LogoConstants;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Utility class which reads and provides config properties.
 */
public enum LogoProperties implements PropertiesService {

    INSTANCE;

    private static final String FILENAME = LogoConstants.LOGO_PROPERTIES;
    private static final Logger LOGGER = LogManager.getLogger(LogoProperties.class);

    private final Properties properties;

    /* This class has only static methods: no need to instantiate it */
    LogoProperties() {
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
