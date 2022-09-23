package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.data.impl.ResourceDataImpl;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.response.ArchivalEntitlement;
import com.geaviation.techpubs.services.impl.ArchivalService;
import com.geaviation.techpubs.services.impl.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RequestMapping("/archival")
public class ArchivalController {

    private final ArchivalService archivalService;
    private final UserService userService;
    private final ResourceDataImpl resourceData;

    @Autowired
    public ArchivalController(ArchivalService archivalService, UserService userService, ResourceDataImpl resourceData) {
        this.archivalService = archivalService;
        this.userService = userService;
        this.resourceData = resourceData;
    }

    @GetMapping(value = "/documents", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArchivalEntitlement getDocuments (
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId) throws TechpubsException {
        String icaoCode = userService.getIcaoCode(ssoId);
        return archivalService.getDocuments(icaoCode);
    }

    @GetMapping(value = "/access", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> hasAccess(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId) throws TechpubsException{
        String icaoCode = userService.getIcaoCode(ssoId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasAccess", archivalService.hasAccess(icaoCode));
        return response;
    }

    /**
     * Gets archived pdfs from the archival s3 bucket given a model, type, filename
     *
     * @param ssoId
     * @param portalId
     * @param model
     * @return
     */
    @GetMapping(value = "/model/{model}/type/{type}/pdf/{filename}",  produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<byte[]> archivePdf(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. CJ610", allowMultiple = false, required = true) @PathVariable("model") String model,
            @ApiParam(name = "type", value = "eg. Service Information", allowMultiple = false, required = true) @PathVariable("type") String type,
            @ApiParam(name = "filename", value = "eg. SB-Cj610.pdf", allowMultiple = false, required = true) @PathVariable("filename") String fileName) throws TechpubsException, IOException {
        String icaoCode = userService.getIcaoCode(ssoId);
        if(archivalService.hasAccess(icaoCode)) {
            byte[] pdf = archivalService.getPdf(model, type, fileName);

            return ResponseEntity.ok()
                    .body((pdf));
        }
        return ResponseEntity.ok().body(resourceData.getPrintNotAvailable());
    }
}
