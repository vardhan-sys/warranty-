package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocAppRegServices {

    private final EnumMap<SubSystem, IDocSubSystemApp> servicesBySubSystem = new EnumMap<>(
        SubSystem.class);

    @Autowired
    public void SubSystems(List<IDocSubSystemApp> services) {
        for (IDocSubSystemApp service : services) {
            register(service.getSubSystem(), service);
        }
    }

    public void register(SubSystem type, IDocSubSystemApp service) {
        this.servicesBySubSystem.put(type, service);
    }

    public IDocSubSystemApp getSubSystemService(SubSystem type) {
        return this.servicesBySubSystem.get(type);
    }
}