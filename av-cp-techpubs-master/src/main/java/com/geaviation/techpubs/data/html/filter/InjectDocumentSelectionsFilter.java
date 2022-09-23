package com.geaviation.techpubs.data.html.filter;

import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.ProgramItemModel;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.filters.DefaultFilter;
import org.dom4j.Element;

import java.util.List;


public class InjectDocumentSelectionsFilter extends DefaultFilter {
    private final IResourceData resourceData;
    private String url;
    private ProgramItemModel programItem;
    private List<Element> documentList;
    private String queryParam;
    private Logger logger;

    private static final QName LIELEMENT = new QName(null, null, "li", null);
    private static final QName AELEMENT = new QName(null, null, "a", null);
    private static final QName HREFATTRIBUTE = new QName(null, null, "href", null);

    // feature flag for versions of files
    private boolean featureUS342496;

    public InjectDocumentSelectionsFilter(String url, ProgramItemModel programItem,
        List<Element> documentList, String queryParam, IResourceData resourceData, Logger logger) {
        this.url = url;
        this.programItem = programItem;
        this.documentList = documentList;
        this.queryParam = (queryParam != null && queryParam.length() > 0 ? "?" + queryParam : "");
        this.resourceData = resourceData;
        this.logger = logger;
    }

    /**
     * Start element.
     */
    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {

        super.startElement(element, attrs, augs);

        if (documentList != null && !documentList.isEmpty()) {
            int idindex = attrs.getIndex("id");
            if ("ul".equalsIgnoreCase(element.rawname) && "documentlist"
                .equalsIgnoreCase(attrs.getValue(idindex))
                && idindex != -1) {
                try {
                    injectDocuemntList(augs);
                } catch (TechpubsException e) {
                    this.logger.error(e.getMessage());
                }
            }
        }
    }

    private void injectDocuemntList(Augmentations augs) throws TechpubsException {
        for (Element element : this.documentList) {
            lineBreak(augs);
            String docnbr = element.attributeValue("docnbr");
            String file = element.attributeValue("file");
            super.startElement(LIELEMENT, new XMLAttributesImpl(), augs);
            XMLAttributes aAttributes = new XMLAttributesImpl();
            StringBuilder sbHref = new StringBuilder(this.url);
            sbHref.append("/").append("pgms/").append(this.programItem.getProgramDocnbr());
            if (programItem.getProgramOnlineVersion() != null && !programItem
                .getProgramOnlineVersion().isEmpty()) {
                sbHref.append(DataConstants.VERSIONS_URI_PATH)
                    .append(programItem.getProgramOnlineVersion());
            }
            sbHref.append("/").append("mans/").append(docnbr);
            sbHref.append("/").append("file/");
            sbHref.append(element.attributeValue("file"));
            sbHref.append(this.queryParam);
            aAttributes.addAttribute(HREFATTRIBUTE, null,
                checkAndAppendCortona(sbHref.toString(), docnbr, file));
            super.startElement(AELEMENT, aAttributes, augs);
            String title = element.attributeValue("title");
            super.characters(new XMLString(title.toCharArray(), 0, title.length()), augs);
            super.endElement(AELEMENT, augs);
            super.endElement(LIELEMENT, augs);
        }
    }

    private String checkAndAppendCortona(String baseUrl, String docnbr, String file)
        throws TechpubsException {
            if (resourceData.cortonaCheck(programItem, docnbr, file)) {
                return "/portals-ui/src/widgets/GE_TechPub/dist/static/cortona-solo/uniview.html?" + baseUrl;
            }
        return baseUrl;
    }

    private void lineBreak(Augmentations augs) {
        super.characters(new XMLString("\n".toCharArray(), 0, 1), augs);
    }

    /**
     * Empty element.
     */
    @Override
    public void emptyElement(QName qname, XMLAttributes xmlattributes,
        Augmentations augmentations) {
        // if we come across an empty VML element then output both a start and
        // an end for it
        if ("v".equalsIgnoreCase(qname.prefix) && "urn:schemas-microsoft-com:vml"
            .equalsIgnoreCase(qname.uri)) {
            super.startElement(qname, xmlattributes, augmentations);
        } else {
            startElement(qname, xmlattributes, augmentations);
        }

        super.endElement(qname, augmentations);
    }
}
