package com.geaviation.techpubs.controllers.impl;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.api.ISearchAppCaller;

@RestController
@RefreshScope
public class SearchControllerImpl {
	
    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;
    
    @Value("${techpubs.services.searchEndpoint}")
    private boolean searchEndpoint;
    
    @Autowired
    ISearchAppCaller searchAppCaller;
    
    @LogExecutionTimeWithArgs
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @RequestBody String payload,
            HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }
        
        if (!searchEndpoint) {
        	return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(searchAppCaller.callSearchEndpoint(ssoId, portalId, payload));

    }
}
