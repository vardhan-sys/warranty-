package com.geaviation.techpubs.data.html.filter;

import com.geaviation.techpubs.data.util.DataConstants;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.cyberneko.html.filters.DefaultFilter;

public class InjectMetaDataFilter extends DefaultFilter {

    private String metaData;
    private static final QName META_DATA_ATTRIBUTE = new QName(null, null, "data-techpubmeta",
        null);

    public InjectMetaDataFilter(String metaData) {
        this.metaData = metaData;
    }

    public InjectMetaDataFilter() {
    }

    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {

        if (DataConstants.BODY.equalsIgnoreCase(element.rawname)) {

            attrs.addAttribute(META_DATA_ATTRIBUTE, null, metaData);
        }
        super.startElement(element, attrs, augs);
    }

    @Override
    public void emptyElement(QName qname, XMLAttributes xmlattributes,
        Augmentations augmentations) {
        // if we come across an empty VML element then output both a start and
        // an end for it
        if (DataConstants.V.equalsIgnoreCase(qname.prefix) && DataConstants.URN_SCHEMAS
            .equalsIgnoreCase(qname.uri)) {
            super.startElement(qname, xmlattributes, augmentations);
        } else {
            startElement(qname, xmlattributes, augmentations);
        }

        super.endElement(qname, augmentations);
    }

}
