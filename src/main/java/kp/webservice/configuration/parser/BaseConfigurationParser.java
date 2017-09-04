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
package kp.webservice.configuration.parser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import kp.webservice.configuration.ConfigurationException;

public abstract class BaseConfigurationParser<T> implements ConfigurationParser<T> {
    private final static Logger logger = LoggerFactory.getLogger(BaseConfigurationParser.class);

    private final Class<T> klass;
    private ConfigurationFactory<T> configurationFactory;

    public BaseConfigurationParser(Class<T> klass, ConfigurationFactory<T> configurationFactory) {
        this.klass = klass;
        this.configurationFactory = configurationFactory;
    }

    public String getConfigPath() throws ConfigurationException {
        String path = null;

        // Load config file from classpath
        URL pathUrl = Thread.currentThread().getContextClassLoader().getResource("application.yaml");
        if(pathUrl == null) {
            pathUrl = Thread.currentThread().getContextClassLoader().getResource("application.yml");
        }
        
        if(pathUrl == null) {
            pathUrl = Thread.currentThread().getContextClassLoader().getResource("application.json");
        }
        
        if (pathUrl != null && !pathUrl.getFile().isEmpty()) {
            path = pathUrl.getFile();
        }

        // override config path if 'APPCONFIG' environment variable is set
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            String envPath = (String) env.lookup("APPCONFIG");
            if (envPath != null && !envPath.isEmpty()) {
                path = envPath;
            }
        } catch (NameNotFoundException e) {
            // Ignore error if 'APPCONFIG' variable is not found.
        } catch (NamingException ex) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add(ex.getMessage());
            throw new ConfigurationException("Environment variable 'APPCONFIG'", errors, ex);
        }
        
        return path;
    }

    @Override
    public T build() throws IOException, ConfigurationException {
        String nullPath = null;
        return this.build(nullPath);
    }

    @Override
    public T build(String path) throws IOException, ConfigurationException {
        try {
            if (path != null) {
                logger.info("Loading application configuration from path '{}'", path);
                return configurationFactory.build(new FileConfigurationSourceProvider(), path);
            }

            logger.info("Loading default application configuration '{}'", klass.getName());
            return configurationFactory.build();
        } catch (io.dropwizard.configuration.ConfigurationException e) {
            if(path == null) {
                path = "Default application configuration";
            }
            throw new ConfigurationException(path, e.getErrors(), e);
        }
    }

}