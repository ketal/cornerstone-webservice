package kp.webservice.resource;

import java.util.Map;
import java.util.SortedMap;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.json.HealthCheckModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kp.webservice.authorization.AdminRole;

@Path("/healthcheck")
@AdminRole
public class HealthCheckResource {
//    private final static Logger logger = LogManager.getLogger(HealthCheckResource.class);

    @Inject
    private HealthCheckRegistry registry;
    
    private ObjectMapper mapper;
    
    public HealthCheckResource() {
        this.mapper = new ObjectMapper().registerModule(new HealthCheckModule());
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response getHealthchecks(@QueryParam("pretty") boolean pretty) throws JsonProcessingException {
//        TODO: resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        final SortedMap<String, HealthCheck.Result> healthCheckResults = this.runHealthChecks();
        
//        logger.debug("HealthCheckRegistry->debug {}", logger.getName());
//        logger.info("HealthCheckRegistry->info {}", logger.getName());
        
        if(healthCheckResults.isEmpty()) {
            return Response.status(Status.NOT_IMPLEMENTED).entity("").build();
        }
        
        Status status = Status.INTERNAL_SERVER_ERROR;
        if(isAllHealthy(healthCheckResults)) {
            status = Status.OK;
        }
        
        String result;
        if(pretty) {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(healthCheckResults);
        } else {
            result = mapper.writer().writeValueAsString(healthCheckResults);
        }
        
        return Response.status(status).entity(result).build();
    }
    
    private static boolean isAllHealthy(Map<String, HealthCheck.Result> results) {
        for (HealthCheck.Result result : results.values()) {
            if (!result.isHealthy()) {
                return false;
            }
        }
        return true;
    }
    
    private SortedMap<String, HealthCheck.Result> runHealthChecks() {
//        if (executorService == null) {
            return registry.runHealthChecks();
//        }
//        return registry.runHealthChecks(executorService);
    }
}
