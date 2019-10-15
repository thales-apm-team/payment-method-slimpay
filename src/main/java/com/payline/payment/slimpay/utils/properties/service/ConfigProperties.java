package com.payline.payment.slimpay.utils.properties.service;

import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Utility class which reads and provides config properties.
 */
public class ConfigProperties extends PropertiesService {

    private static final String FILENAME = "config.properties";
    private static final Logger LOGGER = LogManager.getLogger(ConfigProperties.class);

    private final Properties properties;

    ConfigProperties() {
        properties = new Properties();
        // init of the Properties
        readProperties(properties);
    }

    private static class Holder {
        private static final ConfigProperties instance = new ConfigProperties();
    }

    public static ConfigProperties getInstance(){
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
