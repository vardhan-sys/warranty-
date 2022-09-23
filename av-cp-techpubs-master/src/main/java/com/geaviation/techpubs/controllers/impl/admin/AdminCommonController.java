package com.geaviation.techpubs.controllers.impl.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;

@RestController
@RequestMapping("/admin")
public class AdminCommonController {

    @Value("${DEPLOYEDVERSION}")
    private String deployedVersion;

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ping() {
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
}
