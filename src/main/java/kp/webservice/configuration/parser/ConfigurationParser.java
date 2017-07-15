package kp.webservice.configuration.parser;

import java.io.File;
import java.io.IOException;

import kp.webservice.configuration.ConfigurationException;

public interface ConfigurationParser<T> {

    /**
     * Loads, parses, binds, and validates a configuration object from a file.
     *
     * @param file the path of the configuration file
     * @return a validated configuration object
     * @throws IOException            if there is an error reading the file
     * @throws ConfigurationException if there is an error parsing or validating the file
     */
    default T build(File path) throws IOException, ConfigurationException {
        return build(path.toString());
    }

    /**
     * Loads, parses, binds, and validates a configuration object from a file.
     *
     * @param file the path of the configuration file
     * @return a validated configuration object
     * @throws IOException            if there is an error reading the file
     * @throws ConfigurationException if there is an error parsing or validating the file
     */
    T build(String path) throws IOException, ConfigurationException;
    
    /**
     * Loads, parses, binds, and validates a configuration object from an empty document.
     *
     * @return a validated configuration object
     * @throws IOException            if there is an error reading the file
     * @throws ConfigurationException if there is an error parsing or validating the file
     */
    T build() throws IOException, ConfigurationException;
    
    /**
     * Finds configuration file path from class path. If there is 'APPCONFIG' environment variable defined
     * then it will overwrite configuration path from class path and return path set in environment variable.
     *
     * @return a validated configuration object
     * @throws IOException            if there is an error reading the file
     * @throws ConfigurationException if there is an error parsing or validating the file
     */
    String getConfigPath() throws ConfigurationException;
}
