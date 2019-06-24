package com.payline.payment.slimpay.utils.properties.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertiesService {

    /**
     * Get the properties file's name
     *
     * @return the properties file's name
     */
    protected abstract String getFilename();

    /**
     * Get the Properties object
     *
     * @return a Properties object
     */
    protected abstract Properties getProperties();

    /**
     * Get a configuration property by its name.
     * Warning, if the property is environment-dependent, use partnerConfiguration instead.
     *
     * @param key The name/key of the property to get.
     * @return The property value. Can be null if the property has not been found.
     */
    public String get(final String key) {
        return getProperties().getProperty(key);
    }

    /**
     * Read the properties files using the filename returned by the method getFilename(),
     * then store the result in the {@link Properties} object given in argument.
     *
     * @param properties The storage-support object of read properties.
     */
    protected void readProperties(Properties properties) {

        String fileName = getFilename();

        if (fileName == null || fileName.isEmpty()) {

            throw new RuntimeException("No file's name found");
        }

        try {

            InputStream inputStream = PropertiesService.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);

        } catch (IOException e) {
            logError( "Unable to load the file " + fileName, e );
            throw new RuntimeException(e);
        }

    }

    /**
     * Log an error. This method is abstract so that subclasses can log messages using their own logger.
     *
     * @param message The message to log
     * @param t The associated exception
     */
    protected abstract void logError( String message, Throwable t );

}