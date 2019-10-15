package com.payline.payment.slimpay.utils.properties.service;

import com.payline.payment.slimpay.utils.properties.constants.ConfigurationConstants;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Utility class which reads and provides release properties.
 */
public class ReleaseProperties extends PropertiesService {

    private static final String FILENAME = ConfigurationConstants.RELEASE_PROPERTIES;
    private static final Logger LOGGER = LogManager.getLogger(ReleaseProperties.class);

    private final Properties properties;

    ReleaseProperties() {
        properties = new Properties();
        // init of the Properties
        readProperties(properties);
    }

    private static class Holder {
        private static final ReleaseProperties instance = new ReleaseProperties();
    }

    public static ReleaseProperties getInstance(){
        return Holder.instance;
    }

    @Override
    protected String getFilename() {
        return FILENAME;
    }

    @Override
    protected Properties getProperties() {
        return properties;
    }

    @Override
    protected void logError(String message, Throwable t) {
        LOGGER.error( message, t );
    }

}
