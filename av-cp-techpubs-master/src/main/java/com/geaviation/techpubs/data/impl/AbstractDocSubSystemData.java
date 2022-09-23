package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IDocSubSystemData;

public abstract class AbstractDocSubSystemData implements IDocSubSystemData {

    protected String getSubsystemValue() {
        return getSubSystem().toString();
    }
}