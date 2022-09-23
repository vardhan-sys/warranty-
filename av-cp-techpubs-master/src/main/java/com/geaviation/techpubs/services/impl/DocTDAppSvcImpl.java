package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import java.util.Map;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Component
public class DocTDAppSvcImpl extends AbstractTDAppImpl implements IDocSubSystemApp {

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.TD;
    }

    @Override
    String setFileName() {

        return null;
    }

    @Override
    String setSubSystemResource(DocumentInfoModel documentInfo, DocumentItemModel documentItem,
        Map<String, String> queryParams) {

        return null;
    }
}
