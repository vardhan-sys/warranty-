package com.geaviation.techpubs.data.html.filter;

import com.geaviation.techpubs.data.util.DataConstants;
import java.util.HashMap;
import java.util.Map;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.filters.DefaultFilter;

public class BrowserSupportFilter extends DefaultFilter {

    private boolean inSVG = false;
    private boolean containsVML = false;

    private static final QName SVGELEMENT = new QName(null, null, "svg", null);
    private static final QName RECTELEMENT = new QName(null, null, "rect", null);
    private static final QName LINEELEMENT = new QName(null, null, "line", null);
    private static final QName PATHELEMENT = new QName(null, null, "path", null);
    private static final QName WIDTHATTRIBUTE = new QName(null, null, "width", null);
    private static final QName HEIGHTATTRIBUTE = new QName(null, null, DataConstants.HEIGHT, null);
    private static final QName XATTRIBUTE = new QName(null, null, "x", null);
    private static final QName YATTRIBUTE = new QName(null, null, "y", null);
    private static final QName FILLATTRIBUTE = new QName(null, null, "fill", null);
    private static final QName STROKEATTRIBUTE = new QName(null, null, "stroke", null);
    private static final QName STROKEWIDTHATTRIBUTE = new QName(null, null, "stroke-width", null);
    private static final QName X1ATTRIBUTE = new QName(null, null, "x1", null);
    private static final QName Y1ATTRIBUTE = new QName(null, null, "y1", null);
    private static final QName X2ATTRIBUTE = new QName(null, null, "x2", null);
    private static final QName Y2ATTRIBUTE = new QName(null, null, "y2", null);
    private static final QName DATTRIBUTE = new QName(null, null, "d", null);
    private static final String FILLCOLOR = "fillcolor";
    private static final String STROKECOLOR = "strokecolor";
    private static final String WIDTH = "width";
    private static final String FILLED = "filled";

    /**
     * Constructs a script object with the specified configuration.
     */
    public BrowserSupportFilter() {
        // Do nothing BrowserSupportFilter.
    }

    /**
     * Start element.
     */
    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
        if ("urn:schemas-microsoft-com:vml".equalsIgnoreCase(element.uri) && "v"
            .equals(element.prefix)) {
            containsVML = true;
            if (!inSVG) {
                startSVGArea(augs);
            }
            convertVmlToSvg(element, attrs, augs);
        } else {
            if (inSVG) {
                endSVGArea(augs);
            }
            if (containsVML && "img".equalsIgnoreCase(element.rawname)) {
                // change z-index
                int styleindex = attrs.getIndex(DataConstants.STYLE);
                if (styleindex != -1) {
                    attrs.setValue(styleindex,
                        attrs.getValue(styleindex).replaceAll("z-index:-1", ""));
                }
            } else if ("table".equalsIgnoreCase(element.rawname) || "td"
                .equalsIgnoreCase(element.rawname)
                || "th".equalsIgnoreCase(element.rawname)) {
                // change 0.5pt for chrome/safari
                int styleindex = attrs.getIndex(DataConstants.STYLE);
                if (styleindex != -1) {
                    String style = attrs.getValue(styleindex).replaceAll("0.5pt", "1.0px");
                    if ("td".equalsIgnoreCase(element.rawname)) {
                        // Fix ipc revmarks for firefox
                        style = style.replaceAll("border-left:4 ", "border-left:4px ");
                    }
                    attrs.setValue(styleindex, style);
                }
            }
            super.startElement(element, attrs, augs);
        }
    }

    private void startSVGArea(Augmentations augs) {
        lineBreak(augs);
        XMLAttributes aAttributes = new XMLAttributesImpl();
        aAttributes.addAttribute(WIDTHATTRIBUTE, null, "100%");
        aAttributes.addAttribute(HEIGHTATTRIBUTE, null, "100%");
        super.startElement(SVGELEMENT, aAttributes, augs);
        inSVG = true;
    }

    private void endSVGArea(Augmentations augs) {
        lineBreak(augs);
        super.endElement(SVGELEMENT, augs);
        lineBreak(augs);
        inSVG = false;
    }

    private void convertVmlToSvg(QName element, XMLAttributes attrs, Augmentations augs) {
        lineBreak(augs);
        if ("rect".equals(element.localpart)) {
            convertVmlToSvgRect(attrs, augs);
        } else if ("line".equals(element.localpart)) {
            convertVmlToSvgLine(attrs, augs);
        } else if ("shape".equals(element.localpart)) {
            convertVmlToSvgPath(attrs, augs);
        }
    }

    private void convertVmlToSvgRect(XMLAttributes attrs, Augmentations augs) {
        Map<String, String> attrMap = parseElementAttributes(attrs);

        XMLAttributes aAttributes = new XMLAttributesImpl();
        aAttributes.addAttribute(XATTRIBUTE, null, attrMap.get("left"));
        aAttributes.addAttribute(YATTRIBUTE, null, attrMap.get("top"));
        aAttributes.addAttribute(WIDTHATTRIBUTE, null,
            ("0".equals(attrMap.get(WIDTH)) ? "1" : attrMap.get(WIDTH)));
        aAttributes.addAttribute(HEIGHTATTRIBUTE, null,
            ("0".equals(attrMap.get(DataConstants.HEIGHT)) ? "1"
                : attrMap.get(DataConstants.HEIGHT)));
        if (attrMap.get(FILLCOLOR) != null) {
            aAttributes.addAttribute(FILLATTRIBUTE, null, attrMap.get(FILLCOLOR));
        }
        if (attrMap.get(FILLED) != null && "false".equalsIgnoreCase(attrMap.get(FILLED))) {
            aAttributes.addAttribute(FILLATTRIBUTE, null, "rgba(0,0,0,0)");
        }
        if (attrMap.get(STROKECOLOR) != null) {
            aAttributes.addAttribute(STROKEATTRIBUTE, null, attrMap.get(STROKECOLOR));
        }

        super.startElement(RECTELEMENT, aAttributes, augs);
        super.endElement(RECTELEMENT, augs);
    }

    private void convertVmlToSvgLine(XMLAttributes attrs, Augmentations augs) {
        Map<String, String> attrMap = parseElementAttributes(attrs);

        if (attrMap.get(STROKECOLOR) != null && attrMap.get("strokeweight") != null) {
            XMLAttributes aAttributes = new XMLAttributesImpl();
            aAttributes.addAttribute(STROKEATTRIBUTE, null, attrMap.get(STROKECOLOR));
            aAttributes.addAttribute(STROKEWIDTHATTRIBUTE, null, attrMap.get("strokeweight"));
            if (attrMap.get("from") != null) {
                String[] coords = attrMap.get("from").split(",");
                if (coords.length == 2) {
                    aAttributes.addAttribute(X1ATTRIBUTE, null, coords[0]);
                    aAttributes.addAttribute(Y1ATTRIBUTE, null, coords[1]);
                }
            }
            if (attrMap.get("to") != null) {
                String[] coords = attrMap.get("to").split(",");
                if (coords.length == 2) {
                    aAttributes.addAttribute(X2ATTRIBUTE, null, coords[0]);
                    aAttributes.addAttribute(Y2ATTRIBUTE, null, coords[1]);
                }
            }

            super.startElement(LINEELEMENT, aAttributes, augs);
            super.endElement(LINEELEMENT, augs);
        }
    }

    private void convertVmlToSvgPath(XMLAttributes attrs, Augmentations augs) {
        Map<String, String> attrMap = parseElementAttributes(attrs);

        XMLAttributes aAttributes = new XMLAttributesImpl();
        aAttributes.addAttribute(DATTRIBUTE, null,
            attrMap.get("path").replaceAll("m", "M").replaceAll("l", "L"));
        if (attrMap.get(FILLCOLOR) != null) {
            aAttributes.addAttribute(FILLATTRIBUTE, null, attrMap.get(FILLCOLOR));
        }
        if (attrMap.get(FILLED) != null && "false".equalsIgnoreCase(attrMap.get(FILLED))) {
            aAttributes.addAttribute(FILLATTRIBUTE, null, "rgba(0,0,0,0)");
        }
        if (attrMap.get(STROKECOLOR) != null) {
            aAttributes.addAttribute(STROKEATTRIBUTE, null, attrMap.get(STROKECOLOR));
        }

        super.startElement(PATHELEMENT, aAttributes, augs);
        super.endElement(PATHELEMENT, augs);
    }

    private Map<String, String> parseElementAttributes(XMLAttributes attrs) {
        Map<String, String> attrMap = new HashMap<>();

        int numAttrs = attrs.getLength();
        for (int i = 0; i < numAttrs; i++) {
            if (DataConstants.STYLE.equalsIgnoreCase(attrs.getQName(i))) {
                for (String attr : attrs.getValue(i).split(";")) {
                    String[] attrArray = attr.split(":");
                    if (attrArray.length == 2) {
                        attrMap.put(attrArray[0], attrArray[1]);
                    }
                }
            } else {
                attrMap.put(attrs.getQName(i), attrs.getValue(i));
            }
        }

        return attrMap;
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
        startElement(qname, xmlattributes, augmentations);
        endElement(qname, augmentations);
    }

    /**
     * Characters.
     */
    @Override
    public void characters(XMLString text, Augmentations augs) {
        if (!inSVG) {
            super.characters(text, augs);
        }
    }

    /**
     * End element.
     */
    @Override
    public void endElement(QName element, Augmentations augs) {
        if (!"v".equals(element.prefix)) {
            super.endElement(element, augs);
        }
    }
}
