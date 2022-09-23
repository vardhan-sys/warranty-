package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IDocTDData;
import com.geaviation.techpubs.data.api.techlib.IPageBlkData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemICModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemSBModel;
import com.geaviation.techpubs.models.DocumentItemTRModel;
import com.geaviation.techpubs.models.ManualItemModel;
import com.geaviation.techpubs.models.PageblkDetailsDAO;
import com.geaviation.techpubs.models.ProgramItemModel;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RefreshScope
public abstract class AbstractDocTDData extends AbstractDocSubSystemData implements IDocTDData {

    private static final String TR = "tr";
    private static final String IC = "ic";
    private static final String SB = "sb";

    private static final String FILE = "file";
    private static final String SBNBR = "sbnbr";
    private static final String ALERT_COVER = "alert-cover";
    private static final String DOCNBR = "docnbr";
    private static final String ALERT = "alert";
    private static final String TYPE = "type";
    private static final String CATEGORY = "category";
    private static final String MFILE = "mfile";
    private static final String REVNBR = "revnbr";
    private static final String TOC = "toc";
    public static final String TITLE = "title";
    private static final String REVDATE = "revdate";

    private static final String ONE_TWO_THREE = "$1-$2-$3";
    private static final String D_4_D_2_D_2 = "(\\d{4})(\\d{2})(\\d{2})";
    private static final String TECHPUBS_DOCS_PGMS = "/techpubs/techdocs/pgms/";
    private static final String MANS_SBS_FILE = "/mans/sbs/file/";

    @Value("${techpubs.services.downloadOverlayReviewer}")
    private boolean downloadOverlayFeatureFlag;

    @Autowired
    private IPageBlkData iPageBlkData;

    @SuppressWarnings("unchecked")
    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getDocumentsByProgramType(ProgramItemModel programItemModel,
                                                             String type) {
        final List<String> allowedTypeList = Arrays.asList(new String[]{IC, TR, SB});
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        if (downloadOverlayFeatureFlag) {
            List<PageblkDetailsDAO> pageblkApprovedList;

            //Get all published document for bookcase key and type
            if (IC.equalsIgnoreCase(type) || TR.equalsIgnoreCase(type)) {
                pageblkApprovedList = iPageBlkData.findGroupByPageBlksByBookcaseAndTypeAndApprovedForPublish(programItemModel.getProgramDocnbr(), type, true,
                        programItemModel.getProgramOnlineVersion());
            } else {
                pageblkApprovedList = iPageBlkData.findSbPageBlksByBookcaseAndTypeAndApprovedForPublish(programItemModel.getProgramDocnbr(), type, true);
            }

            if (programItemModel != null && allowedTypeList.contains(type)
                    && programItemModel.getTocRoot() != null) {
                Element tocRoot = programItemModel.getTocRoot();
                Element listElement = (Element) tocRoot
                        .selectSingleNode("./" + type + "list[@docnbr='" + type + "s']");
                if (listElement != null) {
                    for (Element sectionElement : (List<Element>) listElement.elements()) {
                        createDocument(programItemModel, type, documentItemList, tocRoot,
                                sectionElement,pageblkApprovedList);
                    }
                }
            }
        } else {

            if (programItemModel != null && allowedTypeList.contains(type)
                    && programItemModel.getTocRoot() != null) {
                Element tocRoot = programItemModel.getTocRoot();
                Element listElement = (Element) tocRoot
                        .selectSingleNode("./" + type + "list[@docnbr='" + type + "s']");
                if (listElement != null) {
                    for (Element sectionElement : (List<Element>) listElement.elements()) {
                        createDocument(programItemModel, type, documentItemList, tocRoot,
                                sectionElement);
                    }
                }
            }
        }

        return documentItemList;
    }

    /**
     *
     */
    private void createDocument(ProgramItemModel programItemModel, String type,
                                List<DocumentItemModel> documentItemList, Element tocRoot, Element sectionElement, List<PageblkDetailsDAO> pageblkApprovedList) {

        Element manualElement = (Element) tocRoot
                .selectSingleNode("./*[@docnbr='" + sectionElement.attributeValue(DOCNBR) + "']");
        if (manualElement != null) {
            for (Node node : (List<Node>) sectionElement.selectNodes(".//" + type + "[@file]")) {
                if (SB.equalsIgnoreCase(type)) {
                    if (downloadOverlayFeatureFlag) {
                        Element element = (Element) node;
                        for (PageblkDetailsDAO approvedPageblk : pageblkApprovedList) {
                            if (approvedPageblk.getKey().equals(element.attributeValue("key"))) {
                                documentItemList.add(createSBDocument(programItemModel, manualElement, element));
                                break;
                            }
                        }
                    } else {
                        documentItemList.add(createSBDocument(programItemModel, manualElement, (Element) node));
                    }
                } else if (IC.equalsIgnoreCase(type)) {
                    if (downloadOverlayFeatureFlag) {
                        Element element = (Element) node;
                        for (PageblkDetailsDAO approvedPageblk : pageblkApprovedList) {
                            if (approvedPageblk.getKey().equals(element.attributeValue("key"))) {
                                documentItemList.add(createICDocument(programItemModel, manualElement, element));
                                break;
                            }
                        }
                    } else {
                        documentItemList.add(createICDocument(programItemModel, manualElement, (Element) node));
                    }
                } else if (TR.equalsIgnoreCase(type)) {
                    if (downloadOverlayFeatureFlag) {
                        Element element = (Element) node;
                        for (PageblkDetailsDAO approvedPageblk : pageblkApprovedList) {
                            if (approvedPageblk.getKey().equals(element.attributeValue("key"))) {
                                documentItemList.add(createTRDocument(programItemModel, manualElement, element));
                                break;
                            }
                        }
                    } else {
                        documentItemList.add(createTRDocument(programItemModel, manualElement, (Element) node));
                    }
                }
            }
        }
    }

    private void createDocument(ProgramItemModel programItemModel, String type,
            List<DocumentItemModel> documentItemList, Element tocRoot, Element sectionElement) {
        Element manualElement = (Element) tocRoot
                .selectSingleNode("./*[@docnbr='" + sectionElement.attributeValue(DOCNBR) + "']");
        if (manualElement != null) {
            for (Node node : (List<Node>) sectionElement.selectNodes(".//" + type + "[@file]")) {
                if (SB.equalsIgnoreCase(type)) {
                    documentItemList
                            .add(createSBDocument(programItemModel, manualElement, (Element) node));
                } else if (IC.equalsIgnoreCase(type)) {
                    documentItemList
                            .add(createICDocument(programItemModel, manualElement, (Element) node));
                } else if (TR.equalsIgnoreCase(type)) {
                    documentItemList
                            .add(createTRDocument(programItemModel, manualElement, (Element) node));
                }
            }
        }
    }

    private DocumentItemModel createSBDocument(ProgramItemModel programItemModel,
        Element manualElement,
        Element element) {
        DocumentItemSBModel documentItemSB = new DocumentItemSBModel();

        documentItemSB.setProgramItem(programItemModel);
        documentItemSB.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemSB.setFilename(element.attributeValue(FILE));
        documentItemSB.setMfilename(element.attributeValue(MFILE));
        documentItemSB.setFileType(getFileType(documentItemSB.getFilename()));
        documentItemSB.setCategory(element.attributeValue(CATEGORY));
        documentItemSB.setSbalert((ALERT.equalsIgnoreCase(element.attributeValue(TYPE))
            || ALERT_COVER.equalsIgnoreCase(element.attributeValue(TYPE)) ? true : false));
        documentItemSB.setId(element.attributeValue(SBNBR));
        String revisionDate = (element.attributeValue(REVDATE) != null
            ? element.attributeValue(REVDATE).replaceFirst(D_4_D_2_D_2, ONE_TWO_THREE) : null);
        documentItemSB.setRevisionDate(revisionDate);
        documentItemSB
            .setResourceUri(TECHPUBS_DOCS_PGMS + programItemModel.getProgramDocnbr()
                + DataConstants.VERSIONS_URI_PATH + programItemModel.getProgramOnlineVersion() + MANS_SBS_FILE
                + documentItemSB.getFilename());
        documentItemSB.setReleaseDate(revisionDate);
        documentItemSB.setTitle(element.attributeValue(TITLE));
        documentItemSB.setToctitle(
            (element.attributeValue(TOC) != null ? element.attributeValue(TOC)
                : element.attributeValue(TITLE)));
        documentItemSB.setVersion(element.attributeValue(REVNBR));

        return documentItemSB;
    }

    private String getFileType(String filename) {
        String fileType = "";

        if (filename != null) {
            String fileExtension = FilenameUtils.getExtension(filename);
            if ("html".equalsIgnoreCase(fileExtension) || "htm".equalsIgnoreCase(fileExtension)) {
                fileType = "HTML";
            } else {
                fileType = (fileExtension == null ? "" : fileExtension.toUpperCase());
            }
        }

        return fileType;
    }

    private DocumentItemModel createICDocument(ProgramItemModel programItemModel,
        Element manualElement,
        Element element) {
        DocumentItemICModel documentItemIC = new DocumentItemICModel();

        documentItemIC.setProgramItem(programItemModel);
        documentItemIC.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemIC.setFilename(element.attributeValue(FILE));
        documentItemIC.setMfilename(element.attributeValue(MFILE));
        documentItemIC.setFileType(getFileType(documentItemIC.getFilename()));
        documentItemIC.setId(element.attributeValue("key"));
        String revisionDate = (element.attributeValue("revisiondate") != null
            ? element.attributeValue("revisiondate").replaceFirst(D_4_D_2_D_2, ONE_TWO_THREE)
            : null);
        documentItemIC.setRevisionDate(revisionDate);
        documentItemIC
            .setResourceUri(TECHPUBS_DOCS_PGMS + programItemModel.getProgramDocnbr()
                + DataConstants.VERSIONS_URI_PATH + programItemModel.getProgramOnlineVersion() + "/mans/"
                + element.attributeValue(DOCNBR) + "/file/" + documentItemIC.getFilename());
        documentItemIC.setReleaseDate(element.attributeValue(REVDATE));
        documentItemIC.setTitle(element.attributeValue(TITLE));
        documentItemIC.setToctitle(
            (element.attributeValue(TOC) != null ? element.attributeValue(TOC)
                : element.attributeValue(TITLE)));

        return documentItemIC;
    }

    private DocumentItemModel createTRDocument(ProgramItemModel programItemModel,
        Element manualElement,
        Element element) {
        DocumentItemTRModel documentItemTR = new DocumentItemTRModel();

        documentItemTR.setProgramItem(programItemModel);
        documentItemTR.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemTR.setFilename(element.attributeValue(FILE));
        documentItemTR.setMfilename(element.attributeValue(MFILE));
        documentItemTR.setFileType(getFileType(documentItemTR.getFilename()));
        documentItemTR.setId(element.attributeValue("trnbr"));
        String revisionDate = (element.attributeValue(REVDATE) != null
            ? element.attributeValue(REVDATE).replaceFirst(D_4_D_2_D_2, ONE_TWO_THREE) : null);
        documentItemTR.setRevisionDate(revisionDate);
        documentItemTR
            .setResourceUri(TECHPUBS_DOCS_PGMS + programItemModel.getProgramDocnbr()
                + DataConstants.VERSIONS_URI_PATH + programItemModel.getProgramOnlineVersion() + "/mans/"
                + element.attributeValue(DOCNBR) + "/file/" + documentItemTR.getFilename());
        documentItemTR.setTitle(element.attributeValue(TITLE));
        documentItemTR.setToctitle(
            (element.attributeValue(TOC) != null ? element.attributeValue(TOC)
                : element.attributeValue(TITLE)));

        return documentItemTR;
    }

    private ManualItemModel createManualItem(ProgramItemModel programItem, Element manualElement) {
        ManualItemModel manualItem = new ManualItemModel();
        manualItem.setProgramItem(programItem);
        manualItem.setManualdocnbr(manualElement.attributeValue(DOCNBR));

        manualItem.setTitle(manualElement.attributeValue(TITLE));
        manualItem.setRevisiondate(manualElement.attributeValue(REVDATE));
        manualItem.setRevisionnumber(manualElement.attributeValue(REVNBR));
        manualItem.setMultibrowser((manualElement.attributeValue("multibrowser") == null ? "N"
            : manualElement.attributeValue("multibrowser").toUpperCase()));
        return manualItem;
    }
}
