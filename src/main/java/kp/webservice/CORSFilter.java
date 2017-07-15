package kp.webservice;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        MultivaluedMap<String, Object> headers = response.getHeaders();
        
        if(headers.containsKey("Access-Control-Allow-Origin") 
                || headers.containsKey("Access-Control-Allow-Headers")
                || headers.containsKey("Access-Control-Allow-Credentials")
                || headers.containsKey("Access-Control-Allow-Methods")) {
            return;
        }
        
        headers.add("Access-Control-Allow-Origin", "*");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
        headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}
