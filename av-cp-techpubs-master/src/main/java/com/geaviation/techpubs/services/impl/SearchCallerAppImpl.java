package com.geaviation.techpubs.services.impl;

import org.apache.commons.httpclient.HttpClientError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.services.api.ISearchAppCaller;
import com.geaviation.techpubs.services.util.AppConstants;
@Service
public class SearchCallerAppImpl implements ISearchAppCaller {

    private static final Logger log = LogManager.getLogger(SearchCallerAppImpl.class);

	private static final String CALLING_SEARCH = "Calling Search Endpoint: ";

	private static final String RESPONSE_STATUS = "Response Status: ";

	private static final String ERROR_WHILE_CALLING = "Error while calling /search";
    
	private RestTemplate restTemplate = new RestTemplate();
	
	@Value("${API_GATEWAY.SEARCH_URL}")
	private String searchAPIUrl;

	@Override
	public String callSearchEndpoint(String ssoId, String portalId, String payload) throws TechpubsException {

		try {

			HttpHeaders headers = new HttpHeaders();
			headers.set(AppConstants.SM_SSOID, ssoId);
			headers.set(AppConstants.PORTAL_ID, portalId);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(payload, headers);

			UriComponentsBuilder builder = UriComponentsBuilder
					.fromUriString(searchAPIUrl + "/search");

			log.info(CALLING_SEARCH + builder.toUriString());

			ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

			log.info(RESPONSE_STATUS + response.getStatusCodeValue());

			if (response.hasBody()) {
				return response.getBody();
			}

			return null;

		} catch (HttpClientErrorException e) {
			
			log.error(ERROR_WHILE_CALLING + e);
			throw new TechpubsException(TechpubsAppError.INVALID_PARAMETER, e.getResponseBodyAsString());
		
		} catch (HttpServerErrorException e) {
			
			log.error(ERROR_WHILE_CALLING + e);
			throw new TechpubsException(TechpubsAppError.INTERNAL_ERROR, e.getResponseBodyAsString());
		}

	}

}
