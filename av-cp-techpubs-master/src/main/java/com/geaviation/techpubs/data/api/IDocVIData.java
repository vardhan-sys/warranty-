package com.geaviation.techpubs.data.api;

import com.geaviation.techpubs.models.DocumentItemModel;
import java.util.List;

public interface IDocVIData extends IDocMongoData {

    List<DocumentItemModel> getVIDocumentTypes(List<String> modelList, List<String> tokenList);
}
