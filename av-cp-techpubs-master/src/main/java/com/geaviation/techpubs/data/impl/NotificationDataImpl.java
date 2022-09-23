package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.techlib.IBookcaseData;
import com.geaviation.techpubs.data.api.techlib.INotificationBookcaseData;
import com.geaviation.techpubs.models.techlib.dto.NotificationDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationDataImpl {
    private static final Logger logger = LogManager.getLogger(NotificationDataImpl.class);

    @Autowired
    INotificationBookcaseData iNotificationBookcaseData;

    public List<NotificationDto> getNotifications(){
        List<NotificationDto> NotificationDtos = iNotificationBookcaseData.getNotifications();

        return NotificationDtos;
    }
}
