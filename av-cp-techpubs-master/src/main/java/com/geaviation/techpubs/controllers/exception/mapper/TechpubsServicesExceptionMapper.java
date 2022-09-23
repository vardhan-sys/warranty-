package com.geaviation.techpubs.controllers.exception.mapper;

import com.geaviation.techpubs.exceptions.TechpubsException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import org.springframework.stereotype.Service;

@Service
public class TechpubsServicesExceptionMapper implements ExceptionMapper<TechpubsException> {

    @Override
    public Response toResponse(TechpubsException tae) {
        Status responseStatus;

        switch (tae.getTechpubsAppError()) {
            case INTERNAL_ERROR:
                responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
                break;
            case INVALID_PARAMETER:
                responseStatus = Response.Status.BAD_REQUEST;
                break;
            case NO_PROGRAMS_AVAILABLE:
                responseStatus = Response.Status.FORBIDDEN;
                break;
            case NOT_AUTHORIZED:
                responseStatus = Response.Status.UNAUTHORIZED;
                break;
            case RESOURCE_NOT_FOUND:
                responseStatus = Response.Status.NOT_FOUND;
                break;
            default:
                responseStatus = Response.Status.SERVICE_UNAVAILABLE;
                break;
        }

        return Response.status(responseStatus).build();
    }

}
