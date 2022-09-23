package com.geaviation.techpubs.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.response.PortalUserProperty;
import com.geaviation.techpubs.models.response.PortalUserResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String functionalSso;
    private final String portalUrl;

    public UserService(
            @Value("${functional.sso}") String functionalSso,
            @Value("${PORTAL.URL}") String portalUrl) {
        this.functionalSso = functionalSso;
        this.portalUrl = portalUrl;
    }

    /**
     * Calls Portal service to get the ICAO code of the user's company
     *
     * @param ssoId SSO of the user
     * @return The ICAO code of the company the user is apart of
     * @throws TechpubsException
     */
    public String getIcaoCode(String ssoId) throws TechpubsException {
        String icaoCode = "";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpUriRequest request = RequestBuilder
                    .get()
                    .setUri(portalUrl + "user/" + ssoId)
                    .addHeader("sm_ssoid", functionalSso)
                    .addHeader("portal_id", "CWC")
                    .build();

            CloseableHttpResponse response = client.execute(request);

            // If Portal service can't find user's SSO, a non 200 response is returned
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR, "Could not get ICAO code for user");
            }

            PortalUserResponse portalUserResponse = mapper.readValue(response.getEntity().getContent(), PortalUserResponse.class);

            for (PortalUserProperty prop : portalUserResponse.getOrgProperties()) {
                if (prop.getName().equalsIgnoreCase("org.icaocode")) {
                    icaoCode = prop.getValue();
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Could not make request to get ICAO code", e);
        }

        return icaoCode;
    }
}
