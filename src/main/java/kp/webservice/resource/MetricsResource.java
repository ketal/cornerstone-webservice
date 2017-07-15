package kp.webservice.resource;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kp.webservice.authorization.AdminRole;
import kp.webservice.configuration.WebserviceConfiguration;
import kp.webservice.configuration.injection.Config;

@Path("/metrics")
@AdminRole
public class MetricsResource {
//    private final static Logger logger = LogManager.getLogger(MetricsResource.class);
    
    @Inject
    private MetricRegistry registry;

    @Config
    private WebserviceConfiguration config;

    public MetricsResource() {
    }

    private ObjectMapper getObjectMapper() {
        final TimeUnit rateUnit = parseTimeUnit(config.getRateUnit(), TimeUnit.SECONDS);
        final TimeUnit durationUnit = parseTimeUnit(config.getDurationUnit(), TimeUnit.SECONDS);
        final boolean showSamples = false;

        MetricFilter filter = null;
        // MetricFilter filter = (MetricFilter) context.getAttribute(METRIC_FILTER);
        if (filter == null) {
            filter = MetricFilter.ALL;
        }

        return new ObjectMapper().registerModule(new MetricsModule(rateUnit, durationUnit, showSamples, filter));
    }

    private TimeUnit parseTimeUnit(String value, TimeUnit defaultValue) {
        try {
            return TimeUnit.valueOf(String.valueOf(value).toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public String getMetrics(@QueryParam("pretty") boolean pretty) throws JsonProcessingException {
        // TODO: resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        
//        logger.debug("MetricsResource->debug {}", logger.getName());
//        logger.info("MetricsResource->info {}", logger.getName());
        
        if (pretty) {
            return this.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(registry);
        } else {
            return this.getObjectMapper().writer().writeValueAsString(registry);
        }
    }

}
