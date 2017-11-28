/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ketal.webservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerResponseFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.github.ketal.webservice.authorization.AdminRoleFilter;
import com.github.ketal.webservice.configuration.BaseWebserviceConfig;
import com.github.ketal.webservice.configuration.ConfigException;
import com.github.ketal.webservice.configuration.injection.Config;
import com.github.ketal.webservice.configuration.injection.ConfigInjectionFactoryProvider;
import com.github.ketal.webservice.configuration.parser.ConfigFactory;
import com.github.ketal.webservice.configuration.parser.ConfigParser;
import com.github.ketal.webservice.resource.HealthCheckResource;
import com.github.ketal.webservice.resource.LoggerResource;
import com.github.ketal.webservice.util.Generics;

public abstract class WebserviceApplication<T extends BaseWebserviceConfig> extends ResourceConfig {
    private final static Logger logger = LogManager.getLogger(WebserviceApplication.class);

    protected T configuration;

    private MetricsRegistryFeature metricsRegistryFeature;
    private HealthCheckRegistry healthCheckRegistry;

    public WebserviceApplication() {
        super();
        try {
            setApplicationName(getApplicationName());
            registerDefaults();
            initializeJersey();
        } catch (ConfigException | IOException e) {
            logger.error("Exception initializing webservice.", e);
            // TODO: shutdownapp
        }
    }

    public abstract void initializeJersey();

    public abstract void initApp();

    public abstract void destroyApp();

    public abstract String getApplicationName();

    private Class<T> getConfigurationClass() {
        return Generics.getTypeParameter(getClass(), BaseWebserviceConfig.class);
    }
    
    public T getWebserviceConfiguration() {
        return this.configuration;
    }

    private T loadConfig() throws IOException, ConfigException {
        return this.loadConfig(ConfigFactory.getConfigPath());
    }

    public T loadConfig(String path) throws ConfigException, IOException {
        ConfigParser<T> configurationParser = ConfigFactory.getParser(path, this.getConfigurationClass());
        return this.loadConfig(path, configurationParser);
    }

    private T loadConfig(String path, ConfigParser<T> configurationParser) throws IOException, ConfigException {
        if (this.configuration == null) {
            
            this.configuration = (path == null) ? configurationParser.build() : configurationParser.build(path);

            register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(configuration).to(BaseWebserviceConfig.class);
                    bind(configuration).to(getConfigurationClass());

                    bind(ConfigInjectionFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
                    bind(ConfigInjectionFactoryProvider.ConfigInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Config>>() {
                    }).in(Singleton.class);
                }
            });

            // register(AppConfigResource.class);
        }
        return this.configuration;
    }

    protected void registerDefaults() throws ConfigException, IOException {

        this.loadConfig();
        
        metricsRegistryFeature = new MetricsRegistryFeature(this);
        metricsRegistryFeature.registerMetrics();

        this.registerHealthCheckRegistry();

        registerApplicationEventListener();

        if (this.configuration.isRegisterCORSFilter()) {
            register(getCORSFilter());
        }

        if (this.configuration.isRegisterRolesAllowedDynamicFeature()) {
            logger.debug("Registering RolesAllowedDynamicFeature.class");
            register(RolesAllowedDynamicFeature.class);
        }

        if (this.configuration.isRegisterServerProperties()) {
            getServerProperties().entrySet().forEach(p -> property(p.getKey(), p.getValue()));
        }
        
        packages("com.github.ketal.webservice.exception.mapper");

        register(LoggerResource.class);
        register(AdminRoleFilter.class);
        
    }

    private void registerHealthCheckRegistry() {
        this.healthCheckRegistry = new HealthCheckRegistry();
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthCheckRegistry).to(HealthCheckRegistry.class);
            }
        });
        register(HealthCheckResource.class);
    }

    protected void destroyDefaults() {
        this.getHealthchecks().shutdown();
        this.metricsRegistryFeature.deregisterMetrics();
    }

    private boolean isComponentRegistered(final Class<?> type) {
        boolean result = false;
        for (Class<?> c : getClasses()) {
            if (c.isInstance(type)) {
                result = true;
            }
        }
        return result;
    }

    protected ApplicationEventListener getApplicationEventListener() {
        return new WebserviceApplicationEventListener<T>(this, this.metricsRegistryFeature.getMetricRegistry());
    }

    private void registerApplicationEventListener() {
        if (!isComponentRegistered(RequestEventListener.class)) {
            logger.debug("Registering {} as application event listener.", getApplicationEventListener());
            register(getApplicationEventListener());
        }
    }

    protected ContainerResponseFilter getCORSFilter() {
        return new CORSFilter();
    }

    protected Map<String, Boolean> getServerProperties() {
        HashMap<String, Boolean> serverProperties = new HashMap<>();
        serverProperties.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, Boolean.FALSE);
        serverProperties.put(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, Boolean.TRUE);
        return serverProperties;
    }

    public HealthCheckRegistry getHealthchecks() {
        return healthCheckRegistry;
    }
}