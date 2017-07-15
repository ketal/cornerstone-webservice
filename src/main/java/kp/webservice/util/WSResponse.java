package kp.webservice.util;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import kp.webservice.exception.NotModifiedException;

public class WSResponse {

    public static Response response(Request request, Object entity, Status status) throws NotModifiedException {

        Response.ResponseBuilder response = null;
        EntityTag tag = null;

        if (entity != null) {
            tag = new EntityTag(Integer.toString(entity.hashCode()));

            response = request.evaluatePreconditions(tag);
            if (response != null) {
                StatusType statusInfo = response.build().getStatusInfo();
                String responseString = "HTTP " + statusInfo.getStatusCode() + ' ' + statusInfo.getReasonPhrase();
                throw new NotModifiedException(responseString);
            }
        }

        response = Response.status(status).entity(entity);
        return response.tag(tag).build();
    }
}
