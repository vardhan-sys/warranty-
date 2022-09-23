package com.geaviation.techpubs.controllers.impl;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.response.EntitlementResponse;
import com.geaviation.techpubs.services.impl.SalesforceSvcImpl;
import com.geaviation.techpubs.services.impl.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;



@RestController
@RefreshScope
public class EntitlementControllerImpl {

    private static final Logger log = LogManager.getLogger(EntitlementControllerImpl.class);

    @Autowired
    private UserService userService;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${techpubs.services.openSearchEntitlementEndpoint}")
    private boolean openSearchEntitlementEndpoint;

    @Autowired
    private SalesforceSvcImpl salesforceSvc;


    /**
     * Get a list of Salesforce entitlements for a given company
     *
     * @param ssoId User making the request
     * @return List of company entitlements
     */
    @LogExecutionTimeWithArgs
    @GetMapping(value = "/entitlements", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntitlementResponse> getEntitlements(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (openSearchEntitlementEndpoint) {
            String icaoCode = userService.getIcaoCode(ssoId);
            EntitlementResponse entitlements = salesforceSvc.getEntitlements(icaoCode);
            if (entitlements.getEntitlementList().isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {

                return ResponseEntity.ok(entitlements);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
