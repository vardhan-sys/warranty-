package com.geaviation.techpubs.services.impl.logic;

import com.geaviation.techpubs.data.api.IDocumentData;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.techlib.IPageBlkData;
import com.geaviation.techpubs.data.api.techlib.IStoredProcedureRepository;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemSMMDownloadModel;
import com.geaviation.techpubs.models.DocumentItemTDModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.dto.EnabledPageblkFileDto;
import com.geaviation.techpubs.services.api.logic.IPageblkEnablement;
import com.geaviation.techpubs.services.impl.ManualAppSvcImpl;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PageblkEnablement implements IPageblkEnablement {

  @Autowired
  private TechpubsAppUtil techpubsAppUtil;

  @Autowired
  private IPageBlkData iPageBlkData;

  @Autowired
  private IDocumentData iDocumentData;

  @Autowired
  private IProgramData iProgramData;

  @Autowired
  private IStoredProcedureRepository iStoredProcedureRepository;

  private static final Logger log = LogManager.getLogger(ManualAppSvcImpl.class);


  public Map<String, List<DocumentItemSMMDownloadModel>> getBookkeyFromDownloadFilename(List<String> filenames){

    return filenames.stream().filter(f -> f.contains(":"))
        .map(f -> new DocumentItemSMMDownloadModel(f.substring(f.indexOf(":") + 1), f.substring(0, f.indexOf(":"))))
        .collect(Collectors.groupingBy(DocumentItemSMMDownloadModel::getManualDocnbr));

  }

  public List<DocumentItemModel> getEnabledFilesForOnlineFilename(String ssoId, String portalId,
      String program, ProgramItemModel programItem, Map<String, List<DocumentItemModel>> bookKeyToTPSDocumentMap,
      String type) throws TechpubsException {

    List<DocumentItemModel> enabledDocuments = new ArrayList<>();

    if(AppConstants.LR.equalsIgnoreCase(type)) {
      for (String bookKey : bookKeyToTPSDocumentMap.keySet()) {

        enabledDocuments.addAll(getEnabledFilesByOnlineFilename(ssoId, portalId,
            program, programItem, bookKeyToTPSDocumentMap.get(bookKey), bookKey, type));
      }
    }
    else {
      enabledDocuments = bookKeyToTPSDocumentMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    return enabledDocuments;
  }

  public List<DocumentItemSMMDownloadModel> getEnabledFilesForOfflineFileName(String ssoId, String portalId,
      String program, ProgramItemModel programItem, Map<String, List<DocumentItemSMMDownloadModel>> bookKeyToDocumentMap,
      String type, String downloadType) throws TechpubsException {

    List<DocumentItemSMMDownloadModel> enabledDocuments = new ArrayList<>();

    if(AppConstants.PUB_TYPE_IC.equalsIgnoreCase(type) || AppConstants.LR.equalsIgnoreCase(type)) {
      for (String bookKey : bookKeyToDocumentMap.keySet()) {

        enabledDocuments.addAll(getEnabledFilesByOfflineFilename(ssoId, portalId,
            program, programItem, bookKeyToDocumentMap.get(bookKey), bookKey, type, downloadType));
      }
    }
    else {
      enabledDocuments = bookKeyToDocumentMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    return enabledDocuments;
  }



  public List<DocumentItemSMMDownloadModel> getEnabledFilesByOfflineFilename(String ssoId, String portalId,
      String program, ProgramItemModel programItem, List<DocumentItemSMMDownloadModel> requestedDocuments,
      String bookKey, String type, String downloadtype) throws TechpubsException {

    Map<String, String> criteriaMap = new HashedMap();
    criteriaMap.put("catalogtype", (type));
    criteriaMap.put("manualdocnbr", bookKey);
    List<DocumentItemModel> tpsDocuments = iDocumentData.getCatalogDocuments(programItem, downloadtype,criteriaMap);

    for(DocumentItemSMMDownloadModel requestedDocument : requestedDocuments){
      DocumentItemModel matchingTPSDocument = tpsDocuments.stream().filter(f -> isMatchingDocument(downloadtype, requestedDocument, f)).findAny()
            .orElse(null);

      if(matchingTPSDocument != null){
        requestedDocument.setOnlineFileName(matchingTPSDocument instanceof DocumentItemSMMDownloadModel
            ? ((DocumentItemSMMDownloadModel) matchingTPSDocument).getOnlineFileName() :  null);

        requestedDocument.setType(matchingTPSDocument.getType());
      }
    }

    List<DocumentItemModel> enabledDocuments = getEnabledFilesByOnlineFilename(ssoId, portalId, program, programItem, requestedDocuments, bookKey, type);

    return  enabledDocuments
        .stream()
        .filter(f -> f instanceof DocumentItemSMMDownloadModel)
        .map(f -> (DocumentItemSMMDownloadModel) f)
        .collect(Collectors.toList());

  }

  public List<DocumentItemModel> getEnabledFilesByOnlineFilename(String ssoId, String portalId,
      String program, ProgramItemModel programItem, List<? extends DocumentItemModel> tpsPageblks,
      String bookKey, String type) throws TechpubsException {

    List<DocumentItemModel> enabledTPSPageblks = new ArrayList<>();

    List<EnabledPageblkFileDto> userEnabledPageBlks = getEnabledFilesForUserAndBook(ssoId, portalId, program, bookKey,
        programItem);

    for(DocumentItemModel tpsPageblk:tpsPageblks ) {

        if (AppConstants.LR.equalsIgnoreCase(tpsPageblk.getType())) {
          enabledTPSPageblks.add(tpsPageblk);
        } else {
          String onlineFileName = getOnlineFileNameOfDocumentItemModel(tpsPageblk);
          EnabledPageblkFileDto matchingEnabledPageblk = userEnabledPageBlks.stream()
              .filter(f -> f.getOnline_filename().equalsIgnoreCase(onlineFileName)).findAny()
              .orElse(null);

          if (matchingEnabledPageblk == null && AppConstants.PUB_TYPE_IC.equalsIgnoreCase(tpsPageblk.getType())) {
            enabledTPSPageblks.add(tpsPageblk);
          }

          //filter out smm pageblks that do not have a techlevel - these pageblks are in the manual:dvd and manual:source downloads
          else if (matchingEnabledPageblk != null && !(
              AppConstants.DOCUMENT_TYPE_SMM.equalsIgnoreCase(tpsPageblk.getType())
                  && !matchingEnabledPageblk.isAnEnabledSMMPageblk())) {

            ((DocumentItemSMMDownloadModel) tpsPageblk)
                .setSMMEnabledPageblk(matchingEnabledPageblk.isAnEnabledSMMPageblk());
            enabledTPSPageblks.add(tpsPageblk);

          }
        }
    }

    return enabledTPSPageblks;
  }

  public boolean isPageblkEnabled(String ssoId, String portalId, String program, String manual, String onlineFileName)
      throws TechpubsException {

    ProgramItemModel programItem = null;

    try {
      programItem = iProgramData.getProgramItem(program, SubSystem.TD);
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    List<EnabledPageblkFileDto> userEnabledPageBlks = getEnabledFilesForUserAndBook(ssoId, portalId, program, manual, programItem);

    if(userEnabledPageBlks == null) {
      return false;
    }

    return userEnabledPageBlks.stream()
        .anyMatch(f -> f.getOnline_filename().equalsIgnoreCase(onlineFileName));
  }

  private String getOnlineFileNameOfDocumentItemModel(DocumentItemModel documentItemModel){
    String onlineFileName = null;
    if (documentItemModel instanceof DocumentItemSMMDownloadModel) onlineFileName = ((DocumentItemSMMDownloadModel) documentItemModel).getOnlineFileName();
    return onlineFileName;
  }

  private List<EnabledPageblkFileDto> getEnabledFilesForUserAndBook(String ssoId, String portalId, String bookcaseKey, String bookKey,
      ProgramItemModel programItem) throws TechpubsException {
    String icao = techpubsAppUtil.getCurrentIcaoCode(ssoId, portalId);
    return iStoredProcedureRepository.getbookcaseenabledpgblksforicao(icao, bookKey, bookcaseKey, programItem.getProgramOnlineVersion());
  }

  private boolean isMatchingDocumentbyDvdFilename(String downloadType, DocumentItemModel requestedDocument, DocumentItemModel documentFromTPS){

    return (AppConstants.DVD.equalsIgnoreCase(downloadType))
        &&
        (documentFromTPS instanceof DocumentItemSMMDownloadModel
            && ((DocumentItemSMMDownloadModel) documentFromTPS).getDvdFileName().equals(((DocumentItemTDModel) requestedDocument).getFilename()));
  }

  private boolean isMatchingDocumentbySourceFilename(String downloadType, DocumentItemModel requestedDocument, DocumentItemModel documentFromTPS){

    return (AppConstants.SOURCE.equalsIgnoreCase(downloadType))
        &&
        (documentFromTPS instanceof DocumentItemSMMDownloadModel
            && ((DocumentItemSMMDownloadModel) documentFromTPS).getSourcefilename().equals(((DocumentItemTDModel) requestedDocument).getFilename()));
  }

  private boolean isMatchingDocument(String downloadType, DocumentItemModel requestedDocument, DocumentItemModel documentFromTPS) {
    return isMatchingDocumentbyDvdFilename(downloadType,requestedDocument, documentFromTPS)
        || isMatchingDocumentbySourceFilename(downloadType,requestedDocument, documentFromTPS);

  }


}
