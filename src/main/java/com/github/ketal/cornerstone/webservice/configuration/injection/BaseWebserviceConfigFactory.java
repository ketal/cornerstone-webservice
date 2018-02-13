package com.github.ketal.cornerstone.webservice.configuration.injection;

import org.glassfish.hk2.api.Factory;

import com.github.ketal.cornerstone.webservice.configuration.BaseWebserviceConfig;

public class BaseWebserviceConfigFactory implements Factory<BaseWebserviceConfig> {

    private BaseWebserviceConfig config;

    public BaseWebserviceConfigFactory(BaseWebserviceConfig config) {
        this.config = config;
    }

    @Override
    public BaseWebserviceConfig provide() {
        return config;
    }

    @Override
    public void dispose(BaseWebserviceConfig instance) {
    }

}
