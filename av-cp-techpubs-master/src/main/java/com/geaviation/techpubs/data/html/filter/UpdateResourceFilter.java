package com.geaviation.techpubs.data.html.filter;

import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.models.ProgramItemModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * Cyberneko filter to modify the references to resource URL upon user request so the pages are CWC
 * compatible.
 */
public class UpdateResourceFilter extends DefaultFilter {



    protected static final List<String> RESOURCEPATHS = new ArrayList<>(
        Arrays.asList(new String[]{"graphics/", "res/", "images/", "res_ATA2200_EIPC/"}));
    protected static final String LOGOIMAGE = "logo.png";

    protected String baseURL;
    protected ProgramItemModel programItem;
    protected String docnbr;
    protected String urll;

    private boolean isInScript;
    private StringBuilder sbScriptText;
    private static final String SCRIPT = "script";

    /**
     * Constructs a script object with the specified configuration.
     */
    public UpdateResourceFilter(String baseURL, ProgramItemModel programItem, String docnbr) {
        this.baseURL = baseURL;
        this.programItem = programItem;
        this.docnbr = docnbr;
        this.urll = this.baseURL + "/pgms/" + programItem.getProgramDocnbr()
            + DataConstants.VERSIONS_URI_PATH + programItem
            .getProgramOnlineVersion() + "/mans/" + docnbr + "/res/";
        this.sbScriptText = null;
    }

    /**
     * Constructs a script object with the specified configuration.
     */
    public UpdateResourceFilter(String baseURL, ProgramItemModel programItem, String docnbr,
        boolean isGraphicInSMMDirectory, String onlineFileName) {
        this.baseURL = baseURL;
        this.programItem = programItem;
        this.docnbr = docnbr;
        this.urll = this.baseURL + "/pgms/" + programItem.getProgramDocnbr()
            + DataConstants.VERSIONS_URI_PATH + programItem
            .getProgramOnlineVersion() + "/mans/" + docnbr + (isGraphicInSMMDirectory ?
            "/OnlineFileName/" + onlineFileName : "") + "/res/";

        this.sbScriptText = null;
    }

    /**
     * Start document.
     */
    @Override
    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) {
        isInScript = false;
        super.startDocument(locator, encoding, augs);
    }

    /**
     * Start element.
     */
    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
        if ("img".equalsIgnoreCase(element.rawname) || SCRIPT.equalsIgnoreCase(element.rawname)) {

            processStartElement(element, attrs);

            if (SCRIPT.equalsIgnoreCase(element.rawname)) {
                isInScript = true;
                sbScriptText = new StringBuilder();
            }
        } else if ("link".equalsIgnoreCase(element.rawname) && attrs != null) {
            changeHref(attrs);
        } else if (
            ("td".equalsIgnoreCase(element.rawname) || "span".equalsIgnoreCase(element.rawname))
                && (attrs != null)) {
            styleIndex(attrs);
        }
        super.startElement(element, attrs, augs);
    }

    public void processStartElement(QName element, XMLAttributes attrs) {
        if (attrs != null) {
            changeSrc(attrs);
            onClick(element, attrs);

        }
    }

    public void changeSrc(XMLAttributes attrs) {
        // change src
        int srcindex = attrs.getIndex("src");
        if (srcindex != -1) {
            String oldsrc = attrs.getValue(srcindex);
            if (oldsrc.endsWith(LOGOIMAGE)) {
                attrs.setValue(srcindex, this.baseURL + "/logo");
            } else {
                for (String resPrefix : RESOURCEPATHS) {
                    if (oldsrc.startsWith(resPrefix)) {
                        attrs.setValue(srcindex, urll + oldsrc);
                        break;
                    }
                }
            }
        }

    }

    public void onClick(QName element, XMLAttributes attrs) {
        if ("img".equalsIgnoreCase(element.rawname)) {
            int onclickindex = attrs.getIndex("onclick");
            if (onclickindex != -1) {
                String oldonclick = attrs.getValue(onclickindex);
                if (oldonclick.startsWith("swapImage")) {
                    attrs.removeAttributeAt(onclickindex);
                }
            }
        }
    }

    public void styleIndex(XMLAttributes attrs) {
        // change url in style attribute
        int styleindex = attrs.getIndex("style");
        if (styleindex != -1) {
            String oldStyle = attrs.getValue(styleindex);
            String newStyle = oldStyle;
            for (String resPrefix : RESOURCEPATHS) {
                newStyle = newStyle.replaceAll("(.*?url\\()" + resPrefix + "(.*?)(\\).*?)",
                    "$1" + urll + resPrefix + "$2$3");
            }
            attrs.setValue(styleindex, newStyle);

        }
    }

    public void changeHref(XMLAttributes attrs) {
        // change href
        int hrefindex = attrs.getIndex("href");
        if (hrefindex != -1) {
            String oldhref = attrs.getValue(hrefindex);
            for (String resPrefix : RESOURCEPATHS) {
                if (oldhref.startsWith(resPrefix)) {
                    String newhref = urll + oldhref;
                    attrs.setValue(hrefindex, newhref);
                    break;
                }
            }
        }
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
            emptyElement(qname, xmlattributes, augmentations);
            isInScript = false;
        }
        super.endElement(qname, augmentations);
    }

    /**
     * Characters.
     */
    @Override
    public void characters(XMLString text, Augmentations augs) {
        if (isInScript) {
            sbScriptText.append(text.toString());
        } else {
            super.characters(text, augs);
        }
    }

    /**
     * End element.
     */
    @Override
    public void endElement(QName element, Augmentations augs) {
        if (SCRIPT.equalsIgnoreCase(element.rawname)) {
            isInScript = false;
            String replaceISOImageScript = "(.*?iso_obj.OpenFile\\(\")(.*?)(\"\\).*?)";
            String replaceWRLScript = "(.*?(vrmlfile|cgmsrc):\")(.+?)(\".*?)";
            String newText = sbScriptText.toString()
                .replaceFirst(replaceISOImageScript, "$1" + urll + "$2$3")
                .replaceAll(replaceWRLScript, "$1" + urll + "$3$4");
            super.characters(new XMLString(newText.toCharArray(), 0, newText.length()), augs);
        }
        super.endElement(element, augs);
    }

    /** End document. */

}
