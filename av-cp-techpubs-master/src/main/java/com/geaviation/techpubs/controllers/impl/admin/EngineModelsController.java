package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.models.techlib.response.BookcaseKeyListResponse;
import com.geaviation.techpubs.services.api.admin.IEngineApp;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/admin/engine-models")
public class EngineModelsController {

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Autowired
    private IEngineApp iEngineApp;

    @GetMapping(value = "/{engineModel}/bookcase-keys", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookcaseKeyListResponse> getBookcaseKeyMappings(
            @ApiParam(name = "engineModel", value = "eg. CF34-10A") @PathVariable("engineModel") String engineModel) {

        if(sqlInjection) {
            engineModel = SecurityEscape.cleanString(engineModel);
        }

        return ResponseEntity.ok(iEngineApp.getBookcaseKeyMappings(engineModel));
    }
}
