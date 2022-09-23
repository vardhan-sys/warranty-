package com.geaviation.techpubs.controllers.requests;

import java.util.List;

public class EnableStatusBody {
   
	private boolean enabled;
    private List<String> companyIds;

    public EnableStatusBody() {
    }

    public EnableStatusBody(boolean enabled, List<String> companyIds) {
		this.enabled = enabled;
		this.companyIds = companyIds;
	}

	public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	public List<String> getCompanyIds() {
		return companyIds;
	}

	public void setCompanyIds(List<String> companyIds) {
		this.companyIds = companyIds;
	}
    
    
}
