package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.techlib.IReachNotificationData;
import com.geaviation.techpubs.models.techlib.dto.ReachNotificationDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReachNotificationDataImpl {
    private static final Logger logger = LogManager.getLogger(ReachNotificationDataImpl.class);

    @Autowired
    IReachNotificationData iReachNotificationData;

    public List<ReachNotificationDto> getNotifications(){
        List<ReachNotificationDto> ReachNotificationDtos = iReachNotificationData.getNotifications();

        return ReachNotificationDtos;
    }
}
