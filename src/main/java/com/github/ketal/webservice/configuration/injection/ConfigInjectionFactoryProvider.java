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
package com.github.ketal.webservice.configuration.injection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;

import com.github.ketal.webservice.configuration.BaseWebserviceConfig;

@Singleton
public class ConfigInjectionFactoryProvider<T extends BaseWebserviceConfig> extends AbstractValueFactoryProvider {

    @Inject
    private BaseWebserviceConfig config;
    
    @Inject
    public ConfigInjectionFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
        super(mpep, locator, Parameter.Source.UNKNOWN);
    }

    @Override
    protected Factory<?> createValueFactory(final Parameter parameter) {
        final Class<?> classType = parameter.getRawType();

        final Config configAnnotation = parameter.getAnnotation(Config.class);
        if (configAnnotation == null) {
            return null;
        }
        
//        if(WebserviceConfiguration.class.isAssignableFrom(classType)) {
//            return new AbstractContainerRequestValueFactory<Object>() {
//                
//                @Override
//                public Object provide() {
//                    return config;
//                }
//
//            };
//        }

        if(BaseWebserviceConfig.class.isAssignableFrom(classType)) {
            return new AbstractContainerRequestValueFactory<T>() {
                
                @SuppressWarnings("unchecked")
                @Override
                public T provide() {
                    return (T) config;
                }

            };
        }
        
        return null;
    }
    
    public static class ConfigInjectionResolver extends ParamInjectionResolver<Config> {
        public ConfigInjectionResolver() {
            super(ConfigInjectionFactoryProvider.class);
        }
    }
}
