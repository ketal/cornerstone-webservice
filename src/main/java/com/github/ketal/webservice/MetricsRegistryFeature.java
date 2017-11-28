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

import java.lang.management.ManagementFactory;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.github.ketal.webservice.resource.MetricsResource;

public class MetricsRegistryFeature {

    private WebserviceApplication<?> application;
    
    private MetricRegistry metricRegistry;
    
    private boolean metricsAreRegistered;
    private JmxReporter jmxReporter;
    
    public MetricsRegistryFeature(WebserviceApplication<?> application) {
       this.application = application;
    }
    
    protected void registerMetrics() {
        if (metricsAreRegistered) {
            return;
        }

        this.metricRegistry = new MetricRegistry();

        this.metricRegistry.register("jvm.attribute", new JvmAttributeGaugeSet());
        this.metricRegistry.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        this.metricRegistry.register("jvm.classloader", new ClassLoadingGaugeSet());
        this.metricRegistry.register("jvm.filedescriptor", new FileDescriptorRatioGauge());
        this.metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        this.metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        this.metricRegistry.register("jvm.threads", new ThreadStatesGaugeSet());
        jmxReporter = JmxReporter.forRegistry(this.metricRegistry).build();
        jmxReporter.start();

        this.application.register(new InstrumentedResourceMethodApplicationListener(this.metricRegistry));
        this.application.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(metricRegistry).to(MetricRegistry.class);
            }
        });

        this.application.register(MetricsResource.class);
        metricsAreRegistered = true;
    }

    protected void deregisterMetrics() {
        if (jmxReporter != null) {
            jmxReporter.close();
        }

        for (String metric : this.metricRegistry.getNames()) {
            this.metricRegistry.remove(metric);
        }
    }

    public MetricRegistry getMetricRegistry() {
        return this.metricRegistry;
    }
}
