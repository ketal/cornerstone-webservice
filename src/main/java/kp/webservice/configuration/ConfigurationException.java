package kp.webservice.configuration;

import java.util.Collection;

public class ConfigurationException extends io.dropwizard.configuration.ConfigurationException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ConfigurationException for the given path with the given errors.
     *
     * @param path      the bad configuration path
     * @param errors    the errors in the path
     */
    public ConfigurationException(String path, Collection<String> errors) {
        super(path, errors);
    }

    /**
     * Creates a new ConfigurationException for the given path with the given errors and cause.
     *
     * @param path      the bad configuration path
     * @param errors    the errors in the path
     * @param cause     the cause of the error(s)
     */
    public ConfigurationException(String path, Collection<String> errors, Throwable cause) {
        super(path, errors, cause);
    }
}
