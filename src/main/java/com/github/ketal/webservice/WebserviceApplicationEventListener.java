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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.ketal.webservice.configuration.BaseWebserviceConfig;

public class WebserviceApplicationEventListener<T extends BaseWebserviceConfig> implements ApplicationEventListener {
    private final static Logger logger = LogManager.getLogger(WebserviceApplicationEventListener.class);

    private static final String NEWLINE = String.format("%n");
    
    private WebserviceApplication<T> webserviceApp;
    
    private volatile int requestCnt = 0;
    private Meter[] responses;
    private Timer connectionTimer;    
    private Counter activeRequests;
    private MetricRegistry metricRegistry;
    

    public WebserviceApplicationEventListener(WebserviceApplication<T> webserviceApp, MetricRegistry metricRegistry) {
        this.webserviceApp = webserviceApp;
        this.metricRegistry = metricRegistry;
        
        this.connectionTimer = this.metricRegistry.timer(MetricRegistry.name(WebserviceRequestEventListener.class, "connections"));
        this.activeRequests = this.metricRegistry.counter(MetricRegistry.name(WebserviceRequestEventListener.class, "active-requests"));
        this.responses = new Meter[]{
                this.metricRegistry.meter(MetricRegistry.name(WebserviceRequestEventListener.class, "1xx-responses")), // 1xx
                this.metricRegistry.meter(MetricRegistry.name(WebserviceRequestEventListener.class, "2xx-responses")), // 2xx
                this.metricRegistry.meter(MetricRegistry.name(WebserviceRequestEventListener.class, "3xx-responses")), // 3xx
                this.metricRegistry.meter(MetricRegistry.name(WebserviceRequestEventListener.class, "4xx-responses")), // 4xx
                this.metricRegistry.meter(MetricRegistry.name(WebserviceRequestEventListener.class, "5xx-responses"))  // 5xx
        };
    }
    
    @Override
    public void onEvent(ApplicationEvent event) {

        String appName = event.getResourceConfig().getApplicationName();

        switch (event.getType()) {
        case INITIALIZATION_FINISHED:
            logger.info("Starting {} Web Service.", appName);
            this.logComponents(event.getResourceConfig().getClasses(), event.getResourceConfig().getResources());

            // Do something, i.e. initialize application
            webserviceApp.initApp();
            
            logger.info("{} Web service Ready", appName);
            break;

        case DESTROY_FINISHED:
            logger.info("{} Web Service shutdown called.", appName);
            
            // Do something, i.e. shutdown application
            webserviceApp.destroyApp();
            webserviceApp.destroyDefaults();
            
            logger.info("{} Web Service destroyed.", appName);
            break;

        case INITIALIZATION_APP_FINISHED:
            logger.info("Jersey initialization finished.");
            break;

        case INITIALIZATION_START:
            logger.info("Starting {} Web Service initialization", appName);
            break;

        case RELOAD_FINISHED:
            break;

        default:
            logger.info("WebserviceApplicationConfiguration: Unknown Application event happend", event.getType());
            break;
        }
    }
    
    private void logComponents(Set<Class<?>> classes, Set<Resource> allResources) {
        final Set<String> resources = new HashSet<>();
        final StringBuilder resourcesSB = new StringBuilder();
        resourcesSB.append(NEWLINE).append("The following resource classes were found:").append(NEWLINE).append(NEWLINE);
        
        final Set<String> providers = new HashSet<>(); 
        final StringBuilder providersSB = new StringBuilder();
        providersSB.append("The following provider classes were found:").append(NEWLINE).append(NEWLINE);
        
        final Set<Class<?>> allResourcesClasses = new HashSet<>();
        
        classes.forEach(c -> {
            if(c.isAnnotationPresent((Class<? extends Annotation>) Path.class)) {
                resources.add(c.getCanonicalName());
                resourcesSB.append("    - ").append(c.getCanonicalName()).append(NEWLINE);
            } else if(c.isAnnotationPresent((Class<? extends Annotation>) Provider.class)) {
                providers.add(c.getCanonicalName());
                providersSB.append("    - ").append(c.getCanonicalName()).append(NEWLINE);
            }
            
            if (!c.isInterface() && Resource.from(c) != null) {
                allResourcesClasses.add(c);
            }
        });
        
        logger.info(resourcesSB.toString());
        logger.info(providersSB.toString());
        logger.info(new WebserviceEnpointLogger().getEndpointsInfo(allResourcesClasses, allResources));
    }

    

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
//        logger.trace("Request #{} received.", ++requestCnt);
//        // return the listener instance that will handle this request.
        return new WebserviceRequestEventListener(this.requestCnt, this.activeRequests, this.connectionTimer, this.responses);
    }

}
