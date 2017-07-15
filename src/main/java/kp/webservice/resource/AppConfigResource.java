package kp.webservice.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import kp.webservice.configuration.WebserviceConfiguration;
import kp.webservice.configuration.injection.Config;

// Example config resource

//@Path("/appconfig")
public class AppConfigResource {

    @Config 
    private WebserviceConfiguration config;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public WebserviceConfiguration getAppConfig(@QueryParam("pretty") boolean pretty) {
        return config;
    }
    
}
