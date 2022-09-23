package com.geaviation.techpubs.data.html.filter;

import com.geaviation.techpubs.data.util.DataConstants;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * Cyberneko filter to modify the references to resource URL upon user request so the pages are CWC
 * compatible.
 */
public class UpdateEmbedDocumentFilter extends DefaultFilter {

    protected String embedDocument;
    protected String contentType;

    public UpdateEmbedDocumentFilter(String embedDocument, String contentType) {
        this.embedDocument = embedDocument;
        this.contentType = contentType;
    }

    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
        if (attrs != null && DataConstants.EMBED.equalsIgnoreCase(element.rawname)) {

            int srcindex = attrs.getIndex(DataConstants.SRC);
            if (srcindex != -1) {
                attrs.setValue(srcindex, embedDocument);
            }
            if (contentType != null) {
                srcindex = attrs.getIndex(DataConstants.TYPE);
                if (srcindex != -1) {
                    attrs.setValue(srcindex, contentType);
                }
            }

        } else if (attrs != null && "object".equalsIgnoreCase(element.rawname)) {
            int srcindex = attrs.getIndex(DataConstants.DATA);
            if (srcindex != -1) {
                attrs.setValue(srcindex, embedDocument);

            }
        }
        super.startElement(element, attrs, augs);
    }

    @Override
    public void emptyElement(QName qname, XMLAttributes xmlattributes,
        Augmentations augmentations) {
        startElement(qname, xmlattributes, augmentations);
        super.endElement(qname, augmentations);
    }
}
