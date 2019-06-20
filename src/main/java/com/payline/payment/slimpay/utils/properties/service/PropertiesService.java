package com.payline.payment.slimpay.utils.properties.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface PropertiesService {

    /**
     * Get a config property by its name.
     * Warning, if the property is environment-dependent, use partnerConfiguration instead.
     *
     * @param properties : the used Properties object
     * @param key        The name of the property to recover
     * @return The property value. Can be null if the property has not been found.
     */
    default String getProperty(final Properties properties, final String key) {

        return properties.getProperty(key);
    }


    /**
     * Get the properties file's name
     *
     * @return the properties file's name
     */
    String getFilename();

    /**
     * Get the Properties object
     *
     * @return a Properties object
     */
    Properties getProperties();

    default String get(final String key) {
        return getProperty(getProperties(), key);
    }

    /**
     * Reads the properties file and stores the result.
     */
    default void readProperties(Properties properties) {

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

    void logError( String message, Throwable t );

}