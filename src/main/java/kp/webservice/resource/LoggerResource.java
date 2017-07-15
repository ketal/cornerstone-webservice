package kp.webservice.resource;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import kp.webservice.authorization.AdminRole;

@Path("/logger")
@AdminRole
public class LoggerResource {

//    private final static Logger logger = LogManager.getLogger(LoggerResource.class);
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/root/{level}")
    public Response setRootLogLevel(@PathParam("level") String level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
        loggerConfig.setLevel(Level.getLevel(level.toUpperCase()));
        ctx.updateLoggers();
        return Response.ok().build();
    }
    
    @PUT
    @Path("/{logName}/{level}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setLogLevel(@PathParam("logName") String logName, @PathParam("level") String level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig loggerConfig = config.getLoggerConfig(logName);
        LoggerConfig specificConfig = loggerConfig;

        // We need a specific configuration for this logger,
        // otherwise we would change the level of all other loggers
        // having the original configuration as parent as well

        if (!loggerConfig.getName().equals(logName)) {
            specificConfig = new LoggerConfig(logName, Level.getLevel(level.toUpperCase()), true);
            specificConfig.setParent(loggerConfig);
            config.addLogger(logName, specificConfig);
        }
        specificConfig.setLevel(Level.getLevel(level.toUpperCase()));
        ctx.updateLoggers();
        return Response.ok().build();
    }
    
}
