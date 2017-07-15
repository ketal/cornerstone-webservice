package kp.webservice.authorization;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import kp.webservice.configuration.WebserviceConfiguration;
import kp.webservice.configuration.injection.Config;

@AdminRole
public class AdminRoleFilter implements ContainerRequestFilter {

    @Config 
    private WebserviceConfiguration config;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(config.getAdminResourceRole() != null && !config.getAdminResourceRole().isEmpty()) {
            if (!requestContext.getSecurityContext().isUserInRole(config.getAdminResourceRole())) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Admin role is required.").build());
            }
        }
    }

}
