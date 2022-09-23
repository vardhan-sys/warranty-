package com.geaviation.techpubs.data.html.filter;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.cyberneko.html.filters.DefaultFilter;

public class ReplaceStyleSheetFilter extends DefaultFilter {

    protected String oldCssPrefix;
    protected String newCssPrefix;

    /**
     * Constructs a script object with the specified configuration.
     */
    public ReplaceStyleSheetFilter(String oldCssTargetPrefix, String newCssTargetPrefix) {
        oldCssPrefix = oldCssTargetPrefix;
        newCssPrefix = newCssTargetPrefix;
    }

    /**
     * Start element.
     */
    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
        if ("link".equalsIgnoreCase(element.rawname) && (attrs != null)) {

            // change value of named anchors
            int hrefindex = attrs.getIndex("href");
            if (hrefindex != -1) {
                String oldhref = attrs.getValue(hrefindex);
                if (oldhref.startsWith(oldCssPrefix)) {
                    String newhref = oldhref.replaceFirst(oldCssPrefix, newCssPrefix);
                    attrs.setValue(hrefindex, newhref);
                }
            }
        }
        super.startElement(element, attrs, augs);
    }

    /**
     * Empty element.
     */
    @Override
    public void emptyElement(QName qname, XMLAttributes xmlattributes,
        Augmentations augmentations) {
        startElement(qname, xmlattributes, augmentations);
        super.endElement(qname, augmentations);
    }

}
