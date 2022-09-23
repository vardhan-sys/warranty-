package com.geaviation.techpubs.data.impl;

import com.geaviation.techpubs.data.api.IManualData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.models.DocumentItemICModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemSBModel;
import com.geaviation.techpubs.models.DocumentItemTDModel;
import com.geaviation.techpubs.models.DocumentItemTRModel;
import com.geaviation.techpubs.models.ManualItemModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class ManualDataSvcImpl implements IManualData {

    private static final List<String> manualExlcudeList = Arrays
        .asList(new String[]{"og", "ics", "trs", "lrs", "cocs", "gni"});

    private static final String DATA_D = "(\\d{4})(\\d{2})(\\d{2})";
    private static final String VALUE_1_2_3 = "$1-$2-$3";

    @Override
    @LogExecutionTime
    @SuppressWarnings("unchecked")
    public List<ManualItemModel> getManualsByProgram(ProgramItemModel programItem) {
        List<ManualItemModel> manualList = new ArrayList<>();

        if (programItem != null && programItem.getTocRoot() != null) {
            for (Element element : (List<Element>) programItem.getTocRoot().elements()) {
                String docnbr = element.attributeValue(DataConstants.DOCUMENTS_DOCNBR);
                if (docnbr != null && !manualExlcudeList.contains(docnbr)) {
                    ManualItemModel manualItemModel = new ManualItemModel();
                    manualItemModel.setProgramItem(programItem);
                    manualItemModel.setOnlineVersion(programItem.getProgramOnlineVersion());
                    manualItemModel.setManualdocnbr(docnbr);
                    manualItemModel.setTitle(element.attributeValue(DataConstants.DOCUMENTS_TITLE));
                    manualItemModel
                        .setRevisionnumber(element.attributeValue(DataConstants.DOCUMENTS_REVNBR));
                    manualItemModel
                        .setMultibrowser(
                            (element.attributeValue(DataConstants.DOCUMENTS_MULTIBROWSER) == null
                                ? "N"
                                : element.attributeValue(DataConstants.DOCUMENTS_MULTIBROWSER)
                                    .toUpperCase()));
                    String revisionDate = (
                        element.attributeValue(DataConstants.DOCUMENTS_REVDATE) != null
                            ? element.attributeValue(DataConstants.DOCUMENTS_REVDATE)
                            .replaceFirst(DATA_D, VALUE_1_2_3)
                            : null);
                    manualItemModel.setRevisiondate(revisionDate);
                    manualList.add(manualItemModel);
                }
            }
        }
        return manualList;
    }

    @Override
    @LogExecutionTime
    public DocumentItemModel getDocumentItem(ProgramItemModel programItem, String manual,
        String filename) {
        DocumentItemModel documentItem = null;

        if (programItem != null) {
            Element manualElement = (Element) programItem.getTocRoot()
                .selectSingleNode("./*[@docnbr='" + manual + "']");
            String xpath = ".//*[@file='" + filename + "'] | .//*[@mfile='" + filename + "']";
            Element docElement = (Element) manualElement.selectSingleNode(xpath);
            if (docElement != null) {
                if (DataConstants.SB.equals(docElement.getName())) {
                    documentItem = createSBDocument(programItem, manualElement, docElement);
                } else if (DataConstants.TR_SMALL.equals(docElement.getName())) {
                    documentItem = createTRDocument(programItem, manualElement, docElement);
                } else if (DataConstants.IC.equals(docElement.getName())) {
                    documentItem = createICDocument(programItem, manualElement, docElement);
                } else {
                    documentItem = createTDDocument(programItem, manualElement, docElement);
                }
            }
        }

        return documentItem;
    }

    private DocumentItemModel createSBDocument(ProgramItemModel programItemModel,
        Element manualElement,
        Element element) {
        DocumentItemSBModel documentItemSB = new DocumentItemSBModel();

        documentItemSB.setProgramItem(programItemModel);
        documentItemSB.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemSB.setFilename(element.attributeValue(DataConstants.METADATA_FILE));
        documentItemSB.setMfilename(element.attributeValue(DataConstants.MFILE));
        documentItemSB.setFileType(getFileType(documentItemSB.getFilename()));
        documentItemSB.setCategory(element.attributeValue(DataConstants.DOCUMENTS_CATEGORY));
        documentItemSB
            .setSbalert((DataConstants.DOCUMENTS_ALERTS
                .equalsIgnoreCase(element.attributeValue(DataConstants.TYPE))
                || DataConstants.ALERT_COVER
                .equalsIgnoreCase(element.attributeValue(DataConstants.TYPE)) ? true
                : false));
        documentItemSB.setId(element.attributeValue("sbnbr"));
        String revisionDate = (element.attributeValue(DataConstants.DOCUMENTS_REVDATE) != null
            ? element.attributeValue(DataConstants.DOCUMENTS_REVDATE)
            .replaceFirst(DATA_D, VALUE_1_2_3) : null);
        documentItemSB.setRevisionDate(revisionDate);
        documentItemSB.setResourceUri(
            DataConstants.RESOURCE_URI_PGMS + programItemModel.getProgramDocnbr()
                + DataConstants.VERSIONS_URI_PATH
                + programItemModel.getProgramOnlineVersion() + "/mans/sbs/file/"
                + documentItemSB.getFilename());

        documentItemSB.setRevisionDate(revisionDate);
        documentItemSB.setTitle(element.attributeValue(DataConstants.DOCUMENTS_TITLE));
        documentItemSB.setToctitle((element.attributeValue(DataConstants.TOC) != null
            ? element.attributeValue(DataConstants.TOC)
            : element.attributeValue(DataConstants.DOCUMENTS_TITLE)));
        documentItemSB.setVersion(element.attributeValue("revnbr"));

        return documentItemSB;
    }

    private DocumentItemModel createTRDocument(ProgramItemModel programItemModel,
        Element manualElement,
        Element element) {
        DocumentItemTRModel documentItemTR = new DocumentItemTRModel();

        documentItemTR.setProgramItem(programItemModel);
        documentItemTR.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemTR.setFilename(element.attributeValue(DataConstants.METADATA_FILE));
        documentItemTR.setMfilename(element.attributeValue(DataConstants.MFILE));
        documentItemTR.setFileType(getFileType(documentItemTR.getFilename()));
        documentItemTR.setId(element.attributeValue("trnbr"));
        String revisionDate = (element.attributeValue(DataConstants.DOCUMENTS_REVDATE) != null
            ? element.attributeValue(DataConstants.DOCUMENTS_REVDATE)
            .replaceFirst(DATA_D, VALUE_1_2_3) : null);
        documentItemTR.setRevisionDate(revisionDate);
        documentItemTR
            .setResourceUri(DataConstants.RESOURCE_URI_PGMS + programItemModel.getProgramDocnbr()
                + DataConstants.RESOURCE_MANS + element
                .attributeValue(DataConstants.DOCUMENTS_DOCNBR)
                + DataConstants.RESOURCE_FILE + documentItemTR.getFilename());
        documentItemTR.setTitle(element.attributeValue(DataConstants.DOCUMENTS_TITLE));
        documentItemTR.setToctitle((element.attributeValue(DataConstants.TOC) != null
            ? element.attributeValue(DataConstants.TOC)
            : element.attributeValue(DataConstants.DOCUMENTS_TITLE)));

        return documentItemTR;
    }

    private DocumentItemModel createICDocument(ProgramItemModel programItemModel,
        Element manualElement,
        Element element) {
        DocumentItemICModel documentItemIC = new DocumentItemICModel();

        documentItemIC.setProgramItem(programItemModel);
        documentItemIC.setManualItem(createManualItem(programItemModel, manualElement));
        documentItemIC.setFilename(element.attributeValue(DataConstants.METADATA_FILE));
        documentItemIC.setMfilename(element.attributeValue(DataConstants.MFILE));
        documentItemIC.setFileType(getFileType(documentItemIC.getFilename()));
        documentItemIC.setId(element.attributeValue("key"));
        String revisionDate = (element.attributeValue("revisiondate") != null
            ? element.attributeValue("revisiondate").replaceFirst(DATA_D, VALUE_1_2_3) : null);
        documentItemIC.setRevisionDate(revisionDate);
        documentItemIC
            .setResourceUri(DataConstants.RESOURCE_URI_PGMS + programItemModel.getProgramDocnbr()
                + DataConstants.RESOURCE_MANS + element
                .attributeValue(DataConstants.DOCUMENTS_DOCNBR)
                + DataConstants.RESOURCE_FILE + documentItemIC.getFilename());
        documentItemIC.setReleaseDate(element.attributeValue(DataConstants.DOCUMENTS_REVDATE));
        documentItemIC.setTitle(element.attributeValue(DataConstants.DOCUMENTS_TITLE));
        documentItemIC.setToctitle((element.attributeValue(DataConstants.TOC) != null
            ? element.attributeValue(DataConstants.TOC)
            : element.attributeValue(DataConstants.DOCUMENTS_TITLE)));

        return documentItemIC;
    }

    private DocumentItemModel createTDDocument(ProgramItemModel programItemModel,
        Element manualElement,
        Element element) {
        DocumentItemTDModel documentItem = new DocumentItemTDModel();

        documentItem.setProgramItem(programItemModel);
        documentItem.setManualItem(createManualItem(programItemModel, manualElement));
        documentItem.setFilename(element.attributeValue(DataConstants.METADATA_FILE));
        documentItem.setMfilename(element.attributeValue(DataConstants.MFILE));
        documentItem.setFileType(getFileType(documentItem.getFilename()));
        documentItem.setId(element.attributeValue("key"));
        documentItem.setRevisionDate(element.attributeValue(DataConstants.DOCUMENTS_REVDATE));
        documentItem
            .setResourceUri(DataConstants.RESOURCE_URI_PGMS + programItemModel.getProgramDocnbr()
                + DataConstants.RESOURCE_MANS + element
                .attributeValue(DataConstants.DOCUMENTS_DOCNBR)
                + DataConstants.RESOURCE_FILE + documentItem.getFilename());
        documentItem.setTitle(element.attributeValue(DataConstants.DOCUMENTS_TITLE));
        documentItem.setToctitle((element.attributeValue(DataConstants.TOC) != null
            ? element.attributeValue(DataConstants.TOC)
            : element.attributeValue(DataConstants.DOCUMENTS_TITLE)));

        return documentItem;
    }

    private ManualItemModel createManualItem(ProgramItemModel programItem, Element manualElement) {
        ManualItemModel manualItem = new ManualItemModel();
        manualItem.setProgramItem(programItem);
        manualItem.setManualdocnbr(manualElement.attributeValue(DataConstants.DOCUMENTS_DOCNBR));

        manualItem.setTitle(manualElement.attributeValue(DataConstants.DOCUMENTS_TITLE));
        manualItem.setRevisiondate(manualElement.attributeValue(DataConstants.DOCUMENTS_REVDATE));
        manualItem.setRevisionnumber(manualElement.attributeValue("revnbr"));
        manualItem.setMultibrowser((manualElement.attributeValue("multibrowser") == null ? "N"
            : manualElement.attributeValue("multibrowser").toUpperCase()));
        return manualItem;
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

    @Override
    @LogExecutionTime
    public List<DocumentItemModel> getDocumentsByParentTocId(ProgramItemModel programItem,
        String manual,
        String parentnodeid) {
        List<DocumentItemModel> documentItemList = new ArrayList<>();

        if (programItem != null) {
            Element manualElement = (Element) programItem.getTocRoot()
                .selectSingleNode("./*[@docnbr='" + manual + "']");
            Element parentElement = (Element) manualElement
                .selectSingleNode("descendant-or-self::node()[@nodeid='" + parentnodeid + "']");
            if (parentElement != null) {
                for (Element childElement : (List<Element>) parentElement.elements()) {
                    if (childElement.attributeValue(DataConstants.METADATA_FILE) != null) {
                        DocumentItemModel documentItem;
                        if (DataConstants.SB.equals(childElement.getName())) {
                            documentItem = createSBDocument(programItem, manualElement,
                                childElement);
                        } else if (DataConstants.TR_SMALL.equals(childElement.getName())) {
                            documentItem = createTRDocument(programItem, manualElement,
                                childElement);
                        } else if (DataConstants.IC.equals(childElement.getName())) {
                            documentItem = createICDocument(programItem, manualElement,
                                childElement);
                        } else {
                            documentItem = createTDDocument(programItem, manualElement,
                                childElement);
                        }
                        documentItemList.add(documentItem);
                    }
                }
            }
        }

        return documentItemList;
    }
}
