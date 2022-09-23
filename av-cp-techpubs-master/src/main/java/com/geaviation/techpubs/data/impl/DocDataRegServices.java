package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IDocSubSystemData;
import com.geaviation.techpubs.models.SubSystem;
import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocDataRegServices {

    private final EnumMap<SubSystem, IDocSubSystemData> servicesBySubSystem = new EnumMap<>(
        SubSystem.class);

    @Autowired
    public void SubSystems(List<IDocSubSystemData> services) {
        for (IDocSubSystemData service : services) {
            register(service.getSubSystem(), service);
        }
    }

    public void register(SubSystem type, IDocSubSystemData service) {
        this.servicesBySubSystem.put(type, service);
    }

    public IDocSubSystemData getSubSystemService(SubSystem type) {
        return this.servicesBySubSystem.get(type);
    }
}