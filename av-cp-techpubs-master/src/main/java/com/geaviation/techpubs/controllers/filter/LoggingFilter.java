package com.geaviation.techpubs.controllers.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.net.URI;

@Provider
@Priority(value = 1)
public class LoggingFilter implements ContainerRequestFilter {

    private static final Logger log = LogManager.getLogger(LoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {

        UriInfo uriInfo = requestContext.getUriInfo();
        URI absolutePath = uriInfo.getAbsolutePath();
        String uri = absolutePath.getPath();
        String method = requestContext.getMethod();

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(true);
        StringBuilder sb = new StringBuilder();
        if (!queryParams.isEmpty()) {
            for (String queryParam : queryParams.keySet()) {
                sb.append(queryParam + "=" + queryParams.getFirst(queryParam));
                sb.append("&");
            }
            sb.insert(0, "?");
            sb.deleteCharAt(sb.length() - 1);
        }

        String query = sb.toString();

        log.info(" ");
        log.info(method + " " + uri + query);
    }

}
