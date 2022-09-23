package com.geaviation.techpubs.services.api.logic;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemSMMDownloadModel;
import com.geaviation.techpubs.models.DocumentItemTDModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IPageblkEnablement {

   List<DocumentItemModel> getEnabledFilesForOnlineFilename(String ssoId, String portalId,
      String program, ProgramItemModel programItem, Map<String, List<DocumentItemModel>> bookKeyToTPSDocumentMap,
      String type) throws TechpubsException;

  Map<String, List<DocumentItemSMMDownloadModel>> getBookkeyFromDownloadFilename(List<String> filenames);

  List<DocumentItemSMMDownloadModel> getEnabledFilesForOfflineFileName(String ssoId, String portalId,
      String program, ProgramItemModel programItem, Map<String, List<DocumentItemSMMDownloadModel>> bookKeyToTPSDocumentMap,
      String type, String downloadType) throws TechpubsException;

  boolean isPageblkEnabled(String ssoId, String portalId, String program, String manual, String onlineFileName)
      throws TechpubsException;
}
