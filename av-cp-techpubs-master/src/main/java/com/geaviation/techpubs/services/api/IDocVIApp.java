package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import java.util.Map;

/**
 * The Interface IDocVIApp.
 */
public interface IDocVIApp extends IDocSubSystemApp {

    DocumentDataTableModel getDownloadVIDocTypes(String ssoId, String portalId, String family,
        Map<String, String> queryParams) throws TechpubsException;
}
