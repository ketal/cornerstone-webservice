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
package kp.webservice;

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

import io.dropwizard.util.Generics;
import kp.webservice.authorization.AdminRoleFilter;
import kp.webservice.configuration.ConfigurationException;
import kp.webservice.configuration.WebserviceConfiguration;
import kp.webservice.configuration.injection.Config;
import kp.webservice.configuration.injection.ConfigInjectionFactoryProvider;
import kp.webservice.configuration.parser.ConfigurationParser;
import kp.webservice.configuration.parser.YamlConfigurationParser;
import kp.webservice.resource.HealthCheckResource;
import kp.webservice.resource.LoggerResource;

public abstract class WebserviceApplication<T extends WebserviceConfiguration> extends ResourceConfig {
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
        } catch (ConfigurationException | IOException e) {
            logger.error("Exception initializing webservice.", e);
            // TODO: shutdownapp
        }
    }

    public abstract void initializeJersey();

    public abstract void initApp();

    public abstract void destroyApp();

    public abstract String getApplicationName();

    private Class<T> getConfigurationClass() {
        return Generics.getTypeParameter(getClass(), WebserviceConfiguration.class);
    }

    private T loadConfig() throws IOException, ConfigurationException {
        return this.loadConfig(new YamlConfigurationParser<T>(this.getConfigurationClass()).getConfigPath());
    }

    public T loadConfig(String path) throws ConfigurationException, IOException {
        ConfigurationParser<T> configurationParser = new YamlConfigurationParser<T>(this.getConfigurationClass());
        if (path.toLowerCase().endsWith("json")) {
            // TODO: build json config parser
            configurationParser = null;
        }

        return this.loadConfig(path, configurationParser);
    }

    private T loadConfig(String path, ConfigurationParser<T> configurationParser) throws IOException, ConfigurationException {
        if (this.configuration == null) {
            this.configuration = configurationParser.build(path);

            register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(configuration).to(WebserviceConfiguration.class);
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

    protected void registerDefaults() throws ConfigurationException, IOException {
        metricsRegistryFeature = new MetricsRegistryFeature(this);
        metricsRegistryFeature.registerMetrics();

        this.registerHealthCheckRegistry();

        registerApplicationEventListener();

        this.loadConfig();

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

        register(LoggerResource.class);
        
        register(AdminRoleFilter.class);
        // this.healthCheckRegistry = new HealthCheckRegistry();
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