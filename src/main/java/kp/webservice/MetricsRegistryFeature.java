package kp.webservice;

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

import io.dropwizard.jersey.caching.CacheControlledResponseFeature;
import io.dropwizard.jersey.params.AbstractParamConverterProvider;
import io.dropwizard.jersey.sessions.SessionFactoryProvider;
import io.dropwizard.jersey.validation.FuzzyEnumParamConverterProvider;
import kp.webservice.resource.MetricsResource;

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
        this.application.register(CacheControlledResponseFeature.class);
        this.application.register(io.dropwizard.jersey.guava.OptionalMessageBodyWriter.class);
        this.application.register(io.dropwizard.jersey.guava.OptionalParamFeature.class);
        this.application.register(io.dropwizard.jersey.optional.OptionalMessageBodyWriter.class);
        this.application.register(io.dropwizard.jersey.optional.OptionalDoubleMessageBodyWriter.class);
        this.application.register(io.dropwizard.jersey.optional.OptionalIntMessageBodyWriter.class);
        this.application.register(io.dropwizard.jersey.optional.OptionalLongMessageBodyWriter.class);
        this.application.register(io.dropwizard.jersey.optional.OptionalParamFeature.class);
        this.application.register(AbstractParamConverterProvider.class);
        this.application.register(new FuzzyEnumParamConverterProvider());
        this.application.register(new SessionFactoryProvider.Binder());

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
