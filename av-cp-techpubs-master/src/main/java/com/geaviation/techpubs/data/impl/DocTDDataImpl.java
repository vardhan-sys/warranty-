package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IDocTDData;
import com.geaviation.techpubs.models.SubSystem;
import org.springframework.stereotype.Component;

@Component
public class DocTDDataImpl extends AbstractDocTDData implements IDocTDData {

    @Override
    public SubSystem getSubSystem() {
        return SubSystem.TD;
    }
}
