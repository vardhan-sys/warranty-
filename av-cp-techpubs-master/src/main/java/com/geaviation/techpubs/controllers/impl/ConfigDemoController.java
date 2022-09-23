package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.controllers.impl.admin.AdminManagementController;
import com.geaviation.techpubs.controllers.impl.admin.AuditTrailController;
import com.geaviation.techpubs.controllers.impl.admin.AuthorizationController;
import com.geaviation.techpubs.controllers.impl.admin.CompaniesController;
import com.geaviation.techpubs.controllers.impl.admin.EngineModelsController;
import com.geaviation.techpubs.controllers.impl.admin.PublisherController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;


@RestController
@RefreshScope
public class ConfigDemoController {

    private static final Logger log = LogManager.getLogger(ConfigDemoController.class);

    @Value("${techpubs.services.feature1}")
    private boolean feature1;

    @Value("${DEPLOYEDVERSION}")
    private String deployedVersion;

    /**
     * Simple service that returns a 200 response. Can be used as a basic check to signify that the
     * application is up and accepting requests. Additionally, shows the application version as well
     * as the number of Methods and method names in the class that hosts the service endpoints
     */
    @GetMapping(value = "/config/ping", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity ping() {
        StringBuilder pingResponse = new StringBuilder();
        pingResponse.append("Deployed Version " + deployedVersion + "\n\n");
        Method[] adminManagementControllerMethods = AdminManagementController.class.getDeclaredMethods();
        Method[] auditTrailControllerMethods = AuditTrailController.class.getDeclaredMethods();
        Method[] authorizationControllerMethods = AuthorizationController.class.getDeclaredMethods();
        Method[] companiesControllerMethods = CompaniesController.class.getDeclaredMethods();
        Method[] engineModelControllerMethods = EngineModelsController.class.getDeclaredMethods();
        Method[] publisherControllerMethods = PublisherController.class.getDeclaredMethods();
        ArrayList<Method[]> controllerMethodsList = new ArrayList<>();
        controllerMethodsList.add(adminManagementControllerMethods);
        controllerMethodsList.add(auditTrailControllerMethods);
        controllerMethodsList.add(authorizationControllerMethods);
        controllerMethodsList.add(companiesControllerMethods);
        controllerMethodsList.add(engineModelControllerMethods);
        controllerMethodsList.add(publisherControllerMethods);
        pingResponse.append("Total number of deployed admin management services: " + adminManagementControllerMethods.length + "\n");
        pingResponse.append("Total number of deployed audit trail services: " + auditTrailControllerMethods.length + "\n");
        pingResponse.append("Total number of deployed authorization services: " + authorizationControllerMethods.length + "\n");
        pingResponse.append("Total number of deployed companies services: " + companiesControllerMethods.length + "\n");
        pingResponse.append("Total number of deployed engine models services: " + engineModelControllerMethods.length + "\n");
        pingResponse.append("Total number of deployed publisher services: " + publisherControllerMethods.length + "\n\n");
        pingResponse.append("Deployed Admin Services list:\n");
        for (int i = 0; i < controllerMethodsList.size(); i++) {
            Method[] currentMethodArray = controllerMethodsList.get(i);
            for (int j = 0; j < currentMethodArray.length; j++) {
                pingResponse.append((j + 1) + ". " + currentMethodArray[j].getReturnType().getName() + " " + currentMethodArray[j].getName() + "\n");
            }
        }
        return ResponseEntity.ok(pingResponse.toString());
    }


    @GetMapping(value = "/config/greeting")
    public String greeting() {
        if (feature1) {
            return "We're inside the feature 1 code, meaning feature 1 is on!!!";
        } else {
            return "Feature 1 is off!!";
        }

    }

}
