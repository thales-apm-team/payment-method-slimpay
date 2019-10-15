package com.payline.payment.slimpay.utils.properties.service;

import com.payline.payment.slimpay.utils.properties.constants.LogoConstants;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Utility class which reads and provides config properties.
 */
public class LogoProperties extends PropertiesService {

    private static final String FILENAME = LogoConstants.LOGO_PROPERTIES;
    private static final Logger LOGGER = LogManager.getLogger(LogoProperties.class);

    private final Properties properties;

    LogoProperties() {
        properties = new Properties();
        // init of the Properties
        readProperties(properties);
    }

    private static class Holder {
        private static final LogoProperties instance = new LogoProperties();
    }

    public static LogoProperties getInstance(){
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
