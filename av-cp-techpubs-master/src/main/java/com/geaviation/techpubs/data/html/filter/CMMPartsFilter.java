package com.geaviation.techpubs.data.html.filter;

import com.geaviation.techpubs.data.util.DataConstants;
import java.util.List;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.filters.DefaultFilter;

public class CMMPartsFilter extends DefaultFilter {

    private String publication;
    private List<String[]> partsList;
    private boolean processingPartList = false;
    private static final QName TRELEMENT = new QName(null, null, "tr", null);
    private static final QName TDELEMENT = new QName(null, null, "td", null);

    public CMMPartsFilter(String publication, List<String[]> partsList) {
        this.publication = publication;
        this.partsList = partsList;
    }

    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
        if (DataConstants.SPAN.equalsIgnoreCase(element.rawname) && attrs != null) {
            int dataIdIndex = attrs.getIndex("data-id");
            if (dataIdIndex != -1 && "pubid".equalsIgnoreCase(attrs.getValue(dataIdIndex))) {
                super.startElement(element, attrs, augs);
                super.characters(
                        new XMLString(publication.toCharArray(), 0, publication.length()), augs);
            }
        } else if (attrs != null && DataConstants.TR.equalsIgnoreCase(element.rawname)) {
            int dataIdIndex = attrs.getIndex("data-id");
            if (dataIdIndex != -1 && "partliststart"
                .equalsIgnoreCase(attrs.getValue(dataIdIndex))) {
                displayPartsList(augs);

            }
        }
        if (!processingPartList) {
            super.startElement(element, attrs, augs);
        }
    }

    private void displayPartsList(Augmentations augs) {
        processingPartList = true;

        if (this.partsList != null) {
            XMLAttributes aAttributes = new XMLAttributesImpl();
            for (String[] parts : partsList) {
                super.startElement(TRELEMENT, aAttributes, augs);
                for (String part : parts) {
                    super.startElement(TDELEMENT, aAttributes, augs);
                    super.characters(new XMLString(part.toCharArray(), 0, part.length()), augs);
                    super.endElement(TDELEMENT, augs);
                }
                super.endElement(TRELEMENT, augs);
                lineBreak(augs);
            }
        }
    }

    private void lineBreak(Augmentations augs) {
        super.characters(new XMLString("\n".toCharArray(), 0, 1), augs);
    }

    @Override
    public void emptyElement(QName qname, XMLAttributes xmlattributes,
        Augmentations augmentations) {
        startElement(qname, xmlattributes, augmentations);
        endElement(qname, augmentations);
    }

    @Override
    public void endElement(QName element, Augmentations augs) {
        if (!processingPartList) {
            super.endElement(element, augs);
        } else if ("tr".equalsIgnoreCase(element.rawname)) {
                processingPartList = false;
        }
    }
}
