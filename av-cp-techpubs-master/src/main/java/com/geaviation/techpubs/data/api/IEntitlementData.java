package com.geaviation.techpubs.data.api;

import java.util.List;

/**
 * The Interface IEntitlementData.
 */
public interface IEntitlementData {

    /**
     * Gets the cwcadmin entitlements.
     *
     * @param ssoId the sso id
     * @return List the cwcadmin entitlements
     */
    @Deprecated
    //Remove this  during US478605 feature flag cleanup
    List<String> getAdminEntitlements(String ssoId);

    /**
     * Gets the entitlements.
     *
     * @param company the company
     * @param portalId the portal id
     * @param subSystem the sub system
     * @param modelList the model list
     * @return List the entitlements
     */
    List<String> getEntitlements(String company, String portalId, String subSystem,
        List<String> modelList);
}
