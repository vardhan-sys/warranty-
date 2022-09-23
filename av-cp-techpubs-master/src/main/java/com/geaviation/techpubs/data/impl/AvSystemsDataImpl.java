package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.techlib.IAvSystemsNotificationData;
import com.geaviation.techpubs.models.techlib.dto.AvSystemsNotificationDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvSystemsDataImpl {
    private static final Logger logger = LogManager.getLogger(AvSystemsDataImpl.class);

    @Autowired
    IAvSystemsNotificationData   iAvSystemsNotificationData;

    public List<AvSystemsNotificationDto> getNotifications(){
        List<AvSystemsNotificationDto> AvSystemsDto = iAvSystemsNotificationData.getNotifications();

        return AvSystemsDto;
    }
}
