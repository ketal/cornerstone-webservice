package kp.webservice.configuration.parser;

import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;

public class YamlConfigurationParser<T> extends BaseConfigurationParser<T> {

    public YamlConfigurationParser(Class<T> klass) {
        super(klass, new YamlConfigurationFactory<T>(klass, Validators.newValidatorFactory().getValidator(), Jackson.newObjectMapper(), ""));
    }

}
