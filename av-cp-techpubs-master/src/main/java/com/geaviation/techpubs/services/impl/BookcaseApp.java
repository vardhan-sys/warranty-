package com.geaviation.techpubs.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.api.techlib.*;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.impl.BookcaseTOCData;
import com.geaviation.techpubs.data.s3.Util;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookcaseTocDAO;
import com.geaviation.techpubs.models.PageblkDetailsDAO;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import com.geaviation.techpubs.models.techlib.dto.PageblkLookupDto;
import com.geaviation.techpubs.models.techlib.response.EngineModelListResponse;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import static com.geaviation.techpubs.data.util.DataConstants.*;


@Component
@RefreshScope
public class BookcaseApp {

  private static final Logger logger = LogManager.getLogger(BookcaseApp.class);
  public static final String NO_MATCH_FOUND = "No match found";

  @Autowired
  IProgramData iProgramData;

  @Autowired
  private IProgramApp iProgramApp;

  @Autowired
  private TechpubsAppUtil techpubsAppUtil;

  @Autowired
  private IPageBlkData iPageBlkData;

  @Autowired
  private IResourceData iResourceData;

  @Autowired
  private IPageblkLookupData iPageblkLookupData;

  @Autowired
  private S3Config s3Config;

  @Autowired
  private AmazonS3ClientFactory amazonS3ClientFactory;

  @Autowired
  private BookcaseTOCData bookcaseTOCData;

  @Autowired
  private IEngineModelProgramData iEngineModelProgramData;

  @Autowired
  private IBookcaseVersionData iBookcaseVersionData;

  @Autowired
  private IBookData iBookData;

  @Autowired
  private IBookcaseData iBookcaseData;

  @Value("${techpubs.services.downloadOverlayReviewer}")
  private boolean downloadOverlayFeatureFlag;

  public List<String> getAuthorizedBookcaseKeysForRequest(String sso, String portalId,
      Map<String, String> searchFilter) throws TechpubsException {
    List<String> bookcaseKeyList = getBookcaseKeysForRequest(sso, portalId, searchFilter);
    List<String> authorizedBookcaseKeyList = iProgramApp
        .getAuthorizedPrograms(sso, portalId, SubSystem.TD);

    List<String> finalBookcaseKeyList = new ArrayList<>();
    for (String bookcaseKey : bookcaseKeyList) {
      if (authorizedBookcaseKeyList.contains(bookcaseKey)) {
        finalBookcaseKeyList.add(bookcaseKey);
      }
    }

    return finalBookcaseKeyList;
  }

  private List<String> getBookcaseKeysForRequest(String sso, String portalId,
      Map<String, String> searchFilter) throws TechpubsException {
    List<String> bookcaseKeys = new ArrayList<>();

    String family = searchFilter.get(AppConstants.FAMILY);
    String model = searchFilter.get(AppConstants.MODEL);
    String aircraft = searchFilter.get(AppConstants.AIRCRAFT);
    String tail = searchFilter.get(AppConstants.TAIL);
    List<String> esnList = new ArrayList<>();

    if (TechpubsAppUtil.isNotNullandEmpty(searchFilter.get(AppConstants.ESN))) {
      esnList = Arrays.asList(searchFilter.get(AppConstants.ESN).split("\\|"));
    }

    if (TechpubsAppUtil.isNotNullandEmpty(model)) {
      bookcaseKeys = iProgramData.getProgramsByModel(model, SubSystem.TD);
    } else if (TechpubsAppUtil.isNotNullandEmpty(family)) {
      bookcaseKeys = iProgramData.getProgramsByFamily(family, SubSystem.TD);
    } else if (TechpubsAppUtil.isCollectionNotEmpty(esnList)) {
      for (String esn : esnList) {
        for (String derivedModel : techpubsAppUtil
            .getModelList(sso, portalId, family, model, aircraft, tail, esn)) {
          bookcaseKeys = iProgramData.getProgramsByModel(derivedModel, SubSystem.TD);
        }
      }
    } else if (TechpubsAppUtil.isNotNullandEmpty(aircraft) || TechpubsAppUtil
        .isNotNullandEmpty(tail)) {
      for (String derivedModel : techpubsAppUtil
          .getModelList(sso, portalId, family, model, aircraft, tail,
              null)) {
        bookcaseKeys = iProgramData.getProgramsByModel(derivedModel, SubSystem.TD);
      }
    } else {
      // 'ALL/ALL' Selected - Return all Techpubs 'mapped' programs that
      // have been authorized....
      bookcaseKeys = iProgramData.getProgramsByFamily(null, SubSystem.TD);
    }

    return bookcaseKeys;
  }

  public PageblkDetailsDAO getFileDetailsTarget(String bookcaseKey, String version, String bookKey,
      String target) {

    //get online version if no version passed in
    version = StringUtils.isEmpty(version) ? iBookcaseVersionData.findOnlineBookcaseVersion(bookcaseKey) : version;

    PageblkDetailsDAO pageblkDetailsDAO = null;

    ProgramItemModel programItem = iProgramData
        .getProgramItemVersion(new BookcaseVersionEntity(bookcaseKey, version), SubSystem.TD);

    List<PageblkLookupDto> entities = iResourceData
        .pageblkTargetLookup(programItem, bookKey, target);
    String filename;

    switch (entities.size()) {
      case 0:
        filename = target + ".htm";

        pageblkDetailsDAO = getFileDetails(bookcaseKey, version, bookKey, filename);
        break;
      case 1:
        filename = entities.get(0).getOnlineFilename();

        pageblkDetailsDAO = getFileDetails(bookcaseKey, version, bookKey, filename);
        break;
      default:
        filename = entities.get(0).getOnlineFilename();

        List<PageblkDetailsDAO> pageblkDetailsDAOList = iPageBlkData
            .findPageBlkByVersionFileName(bookcaseKey, version, bookKey, filename);

        if (!pageblkDetailsDAOList.isEmpty()) {
          pageblkDetailsDAO = pageblkDetailsDAOList.get(0);
        } else {
          pageblkDetailsDAO = new PageblkDetailsDAO();
          pageblkDetailsDAO.setBookcaseKey(bookcaseKey);
          pageblkDetailsDAO.setVersion(version);
          pageblkDetailsDAO.setBookKey(bookKey);
          pageblkDetailsDAO.setFileName(filename);
        }
        pageblkDetailsDAO.setSuccess(true);
        pageblkDetailsDAO.setMultiMatch(true);
    }

    return pageblkDetailsDAO;
  }

  public PageblkDetailsDAO getFileDetails(String bookcaseKey, String version, String bookKey,
      String filename) {

    //get online version if no version passed in
    version = StringUtils.isEmpty(version) ? iBookcaseVersionData.findOnlineBookcaseVersion(bookcaseKey) : version;

    ProgramItemModel programItem = iProgramData
        .getProgramItemVersion(new BookcaseVersionEntity(bookcaseKey, version), SubSystem.TD);

    PageblkDetailsDAO pageblkDetailsDAO = null;

    List<PageblkDetailsDAO> pageblkDetailsDAOList = iPageBlkData
        .findPageBlkByVersionFileName(bookcaseKey, version, bookKey, filename);

    //IPC file names are mapped to "m-" file name
    if(pageblkDetailsDAOList.isEmpty() && filename.startsWith("m-")){
      String fileNameNoM = filename.replace("m-","");
      pageblkDetailsDAOList = iPageBlkData
          .findPageBlkByVersionFileName(bookcaseKey, version, bookKey, fileNameNoM);
    }

    if (!pageblkDetailsDAOList.isEmpty()) {
      pageblkDetailsDAO = pageblkDetailsDAOList.get(0);
      pageblkDetailsDAO.setSuccess(true);

      //If doc type is manual or empty always set approvedForPublish to true
      if(MANUAL_PAGEBLK_TYPE.equals(pageblkDetailsDAO.getType())
              || StringUtils.isEmpty(pageblkDetailsDAO.getType())){
        pageblkDetailsDAO.setApprovedForPublish(true);
      }
      if(downloadOverlayFeatureFlag) {
        if (SB_PAGEBLK_TYPE.equals(pageblkDetailsDAO.getType())) {

          //Return all approved SB pageblk keys for any of the versions
          List<PageblkDetailsDAO> pageblkKeyList = iPageBlkData.findPageBlksKeysAndRevisionForSbType(bookcaseKey);
          if (CollectionUtils.isNotEmpty(pageblkKeyList)) {
              for (PageblkDetailsDAO pageList : pageblkKeyList) {
                if (pageList.getKey().equals(pageblkDetailsDAO.getKey())
                        && pageList.getRevision().equals(pageblkDetailsDAO.getRevision()))
                {
                  pageblkDetailsDAO.setApprovedForPublish(true);
                }
              }
          }
        }
      }

      // check to see if cortona3d file exists based off of htm name and return its name and extension if it exists
      try {
        if (iResourceData.cortonaCheck(programItem, bookKey, filename)) {
          pageblkDetailsDAO.setFileExtension("cortona3d");
        } else {
          pageblkDetailsDAO.setFileExtension("htm");
        }
      } catch (TechpubsException e) {
        //ADD log
      }
      if (pageblkDetailsDAOList.size() > 1) {
        pageblkDetailsDAO.setMultiMatch(true);
      }
    } else {
      //check for archived SB, no need for cortona check
      Map<String, String> archiveSBFile = bookcaseTOCData.getTPSArichiveSB( bookcaseKey,  filename);
      if(archiveSBFile != null){

        Date revDate = null;
        try {
          revDate = new SimpleDateFormat("yyyyMMdd").parse(archiveSBFile.get(TPS_REV_DATE));
        } catch (ParseException e) {
          //Date can be wrong format, ignore if date not valid
        }

        pageblkDetailsDAO = new PageblkDetailsDAO(bookKey, "Archived SBs",
            archiveSBFile.get(TPS_VIEW_FILENAME), archiveSBFile.get(TPS_TOC_KEY),
            archiveSBFile.get(TPS_TITLE), revDate,
            bookcaseKey, "", "sb", null, version);

        pageblkDetailsDAO.setFileExtension("htm");
        pageblkDetailsDAO.setSuccess(true);
        //For Archived SBs always set approvedForPublish to true
        pageblkDetailsDAO.setApprovedForPublish(true);

      } else {

        List<BookcaseTocDAO> cesmBookList = bookcaseTOCData.getCESMFromTPS(bookcaseKey, version);

        BookcaseTocDAO cesmMatch = null;

        if (cesmBookList != null && !cesmBookList.isEmpty()) {
          // Loop and find by file name
          for (BookcaseTocDAO cesm : cesmBookList.get(0).getChildren()) {
            if (cesm.getFileName().equals(filename)) {
              cesmMatch = cesm;
              break;
            }
          }
        }
        if (cesmMatch != null) {
          Date revDate = null;
          try {
            revDate = new SimpleDateFormat("yyyyMMdd").parse(cesmMatch.getRevisionDate());
          } catch (ParseException e) {
            //Date can be wrong format, ignore if date not valid
          }

          pageblkDetailsDAO = new PageblkDetailsDAO(bookKey, "Commercial Engine Service Memorandum",
              cesmMatch.getFileName(), cesmMatch.getKey(),
              cesmMatch.getTitle(), revDate,
              bookcaseKey, "", "manual", null, version);

          pageblkDetailsDAO.setFileExtension("htm");
          pageblkDetailsDAO.setSuccess(true);
          //For CESMs always set approvedForPublish to true
          pageblkDetailsDAO.setApprovedForPublish(true);

        } else {

          boolean doesFileExist = false;

          AmazonS3 amazonS3Client = null;
          try {
            amazonS3Client = amazonS3ClientFactory.getS3Client();
          } catch (TechpubsException techpubsException) {
            logger.info(techpubsException.getMessage());
          }

          String s3ObjKey = Util.createVersionFolderS3ObjKey(programItem, bookKey, filename);
          doesFileExist = amazonS3Client
              .doesObjectExist(s3Config.getS3Bucket().getBucketName(), s3ObjKey);

          if (doesFileExist) {
            pageblkDetailsDAOList = iPageBlkData
                .findBookByVersion(bookcaseKey, version, bookKey);

            if (!pageblkDetailsDAOList.isEmpty() && pageblkDetailsDAOList.size() > 0) {
              pageblkDetailsDAO = pageblkDetailsDAOList.get(0);
            }
            pageblkDetailsDAO.setFileName(filename);
            pageblkDetailsDAO.setFileExtension("htm");
            pageblkDetailsDAO.setApprovedForPublish(true);
            pageblkDetailsDAO.setSuccess(true);
          } else {
            pageblkDetailsDAO = new PageblkDetailsDAO();
            pageblkDetailsDAO.setApprovedForPublish(true);
            pageblkDetailsDAO.setBookcaseKey(bookcaseKey);
            pageblkDetailsDAO.setVersion(version);
            pageblkDetailsDAO.setBookKey(bookKey);
            pageblkDetailsDAO.setFileName(filename);
            pageblkDetailsDAO.setSuccess(false);
            pageblkDetailsDAO.setError(NO_MATCH_FOUND);
            logger.info(
                "No match found for file : {}, {}, {}, {} ", bookcaseKey, version, bookKey,
                filename);
          }
        }
      }
    }

    return pageblkDetailsDAO;
  }

  public EngineModelListResponse getEngineModelsByBookcaseKey(String bookcaseKey) {
      return new EngineModelListResponse(iEngineModelProgramData.findEngineModelsByBookcaseKey(bookcaseKey));
  }

  public boolean updateEmailNotificationDate(String bookcaseKey,String action){
    Date date = new Date();
    boolean isUpdate = false;
    String message = "No match found for bookcase";
    Timestamp timestamp = null;

    if(action.equals("update")){
      timestamp = new Timestamp(date.getTime());
    }
    //new Timestamp(date.getTime())
    if(iBookcaseData.updateLastEmailSentDate(timestamp, bookcaseKey)==1){
      isUpdate = true;
      message ="Last Notification date was successfully updated for bookcase";
    }
    logger.info(message+" : {},", bookcaseKey);
    return isUpdate;
  }

  public boolean updateEmailFlag(boolean emailFlag,String bookcaseKey){
    boolean isUpdate=false;

    String message = "No match found for bookcase";
    if(iBookcaseData.updateSendEmail(emailFlag,bookcaseKey) == 1) {
      isUpdate=true;
      message ="Email Flag was successfully updated for bookcase";
    }
    logger.info(message+" : {},", bookcaseKey);
    return isUpdate;
  }

}

