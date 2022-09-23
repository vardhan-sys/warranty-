package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IBookcaseContentApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;

@Service
public class
BookcaseContentAppRegServices {

    private final EnumMap<SubSystem, IBookcaseContentApp> servicesBySubSystem = new EnumMap<>(
        SubSystem.class);

    @Autowired
    public void SubSystems(List<IBookcaseContentApp> services) {
        for (IBookcaseContentApp service : services) {
            register(service.getSubSystem(), service);
        }
    }

    public void register(SubSystem type, IBookcaseContentApp service) {
        this.servicesBySubSystem.put(type, service);
    }

    public IBookcaseContentApp getSubSystemService(SubSystem type) {
        return this.servicesBySubSystem.get(type);
    }
}