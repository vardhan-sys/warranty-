package com.geaviation.techpubs.data.html.filter;

import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IPageblkLookupData;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.techlib.dto.CortonaTargetDto;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.filters.DefaultFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Use Cyberneko (cyberneko.org) to modify HTML content at request time to allow the DVD content to
 * be operational on the CWC.
 */
public class UpdateTargetAnchorsFilter extends DefaultFilter {

    private boolean lazyLoadingImages;

    /**
     * Filters for external links
     */
    private static final List<String> VALIDLINKS = new ArrayList<>(
        Arrays.asList(new String[]{"http", "https", "ftp", "file", "#"}));
    public static final String TRG_URI_PATH = "/trg/";
    public static final String PORTALS_UI_SRC_WIDGETS_GE_TECH_PUB_SRC_CORTONA_SOLO_UNIVIEW_HTML = "/portals-ui/src/widgets/GE_TechPub/src/cortona-solo/uniview.html?";
    public static final String PORTALS_UI_SRC_WIDGETS_GE_TECH_PUB_DIST_CORTONA_SOLO_UNIVIEW_HTML = "/portals-ui/src/widgets/GE_TechPub/dist/static/cortona-solo/uniview.html?";

    protected String url;
    protected ProgramItemModel programItem;
    protected String docNbr;
    protected String fullURL;
    protected String queryParam;
    private Logger logger;
    private boolean isNOPAGE;
    private boolean isInScript;
    private StringBuilder sbScriptText;
    private static final String RES = "/res/";
    private static final String FILE = "/file/";
    private static final String MANS = "/mans/";
    private static final String PGMS = "/pgms/";

    private IResourceData resourceData;

    private IBookcaseVersionData iBookcaseVersionData;

    private IPageblkLookupData iPageblkLookupData;

    /**
     * Constructs a script object with the specified configuration.
     */
    public UpdateTargetAnchorsFilter(String url, ProgramItemModel programItem, String docNbr,
        String fullURL, String queryParam, IResourceData resourceData,
        IBookcaseVersionData iBookcaseVersionData, IPageblkLookupData iPageblkLookupData,
        Logger logger) {
        this.url = url;
        this.programItem = programItem;
        this.docNbr = docNbr;
        this.fullURL = fullURL;
        this.queryParam = queryParam;
        this.sbScriptText = new StringBuilder();
        this.logger = logger;
        this.resourceData = resourceData;
        this.iBookcaseVersionData = iBookcaseVersionData;
        this.iPageblkLookupData = iPageblkLookupData;
    }

    /**
     * Constructs UpdateTargetAnchorsFilter class with the feature flag passed.
     * REMOVE THIS CONSTRUCTOR WHEN CLEANING UP THIS PARTICULAR FEATURE FLAG!
     */
    public UpdateTargetAnchorsFilter(boolean lazyLoadingImages, String url, ProgramItemModel programItem, String docNbr,
     String fullURL, String queryParam, IResourceData resourceData, IBookcaseVersionData iBookcaseVersionData, IPageblkLookupData   iPageblkLookupData, Logger logger) {

        this(url,programItem,docNbr,fullURL,queryParam,resourceData,iBookcaseVersionData,iPageblkLookupData,logger);
        this.lazyLoadingImages = lazyLoadingImages;
    }
    /**
     * Start element.
     */
    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
        if ("a".equalsIgnoreCase(element.rawname)) {
            if (attrs != null) {
                try {
                    changeHref(attrs);
                } catch (TechpubsException e) {
                    logger.error(e.getMessage());
                }
                changeSbLink(attrs);
            }
        } else if ("script".equalsIgnoreCase(element.rawname)) {
            isInScript = true;
            sbScriptText = new StringBuilder();
        } else if ("embed".equalsIgnoreCase(element.rawname)) {
            embedElement(attrs);
        } else if ("area".equalsIgnoreCase(element.rawname)) {
            areaElement(attrs);
        } else if ("frame".equalsIgnoreCase(element.rawname)) {
            frameElement(attrs);
        } else if ("param".equalsIgnoreCase(element.rawname)) {
            paramElement(attrs);
        }

        if (lazyLoadingImages && "img".equalsIgnoreCase(element.rawname)) {
            QName loading = new QName("", "", "loading", "");
            attrs.addAttribute(loading, "CDATA", "lazy");
        }
        super.startElement(element, attrs, augs);
    }

    private void paramElement(XMLAttributes attrs) {
        int valueindex = attrs.getIndex("value");
        if ("src".equalsIgnoreCase(attrs.getValue("name")) && valueindex != -1) {
            // change value for embed tags
            String newsrc =
                url + PGMS + programItem.getProgramDocnbr() + DataConstants.VERSIONS_URI_PATH
                    + programItem
                    .getProgramOnlineVersion() + MANS + docNbr + "file/" + attrs
                    .getValue(valueindex) + this.queryParam;
            attrs.setValue(valueindex, newsrc);
        }
    }

    private void frameElement(XMLAttributes attrs) {
        if (attrs != null) {
            // change src for embed tags
            int srcindex = attrs.getIndex("src");
            if (srcindex != -1) {
                String newsrc =
                    url + PGMS + programItem.getProgramDocnbr() + DataConstants.VERSIONS_URI_PATH
                        + programItem
                        .getProgramOnlineVersion() + MANS + docNbr + FILE + attrs.getValue(srcindex)
                        + this.queryParam;
                attrs.setValue(srcindex, newsrc);
            }
        }
    }

    private void areaElement(XMLAttributes attrs) {
        int hrefindex = attrs.getIndex("href");
        if (hrefindex != -1) {
            String newhref = null;
            String oldhref = attrs.getValue(hrefindex);
            newhref = url + PGMS + programItem.getProgramDocnbr() + DataConstants.VERSIONS_URI_PATH
                + programItem
                .getProgramOnlineVersion() + MANS + docNbr + FILE + oldhref + this.queryParam;
            attrs.setValue(hrefindex, newhref);
        }
    }

    private void embedElement(XMLAttributes attrs) {
        if (attrs != null) {
            // change src for embed tags
            int srcindex = attrs.getIndex("src");
            if (srcindex != -1) {
                String newsrc =
                    url + "/pgms" + programItem.getProgramDocnbr() + DataConstants.VERSIONS_URI_PATH
                        + programItem
                        .getProgramOnlineVersion() + MANS + docNbr + FILE + attrs
                        .getValue(srcindex) + this.queryParam;
                attrs.setValue(srcindex, newsrc);
            }
        }
    }

    private void changeSbLink(XMLAttributes attrs) {
        // eipc can contain sb links which have a syntax like <a sb="72-0001">
        // which needs to be translated to <a href="sbs:genx-1b-sb-72-0001">
        int sbindex = attrs.getIndex("sb");
        if (sbindex != -1) {
            String newSbNumber = attrs.getValue(sbindex);
            // handle variable size sequence id which translates numbers like
            // sb="72-001" to sb"72-0001" so its found in the target index.
            Pattern patternWRLScript = Pattern
                .compile("^([0-9][0-9]-)[a,A]?([0-9][0-9]{0,3})(?:[r,R][0-9][0-9]?)?$",
                    Pattern.CASE_INSENSITIVE);

            Matcher matcher = patternWRLScript.matcher(newSbNumber);
            while (matcher.find()) {
                String sbSeqNum = "0000" + matcher.group(2);
                newSbNumber =
                    matcher.group(1) + sbSeqNum.substring(sbSeqNum.length() - 4, sbSeqNum.length());
            }
            String newhref =
                url + PGMS + programItem.getProgramDocnbr() + DataConstants.VERSIONS_URI_PATH
                    + programItem
                    .getProgramOnlineVersion() + "/mans/sbs/trg/" + programItem.getDvdSbModel()
                    .toLowerCase(Locale.getDefault()) + "-sb-" + newSbNumber + this.queryParam;
            attrs.removeAttributeAt(sbindex);
            QName newHref = new QName("", "href", "href", "");
            attrs.addAttribute(newHref, "CDATA", newhref);
        }

    }

    private void changeHref(XMLAttributes attrs) throws TechpubsException {
        // change href for external anchors
        int hrefindex = attrs.getIndex("href");
        if (hrefindex != -1) {
            String newhref = null;
            String oldhref = attrs.getValue(hrefindex);
            if (oldhref.startsWith("NOPAGE?")) {
                // process SPM links
                int iPos = oldhref.indexOf(':');
                if ("SPM".equalsIgnoreCase(oldhref.substring(iPos + 1, iPos + 4))) {
                    // find the target in the SPM link string
                    int iPosTargetStart = oldhref.indexOf("refint:");
                    int iPosTargetEnd = oldhref.length();
                    String spmProgramDocnbr = "gek108792";
                    String spmManualDocnbr = "gek9250";
                    String target = oldhref.substring(iPosTargetStart + 7, iPosTargetEnd)
                        .toLowerCase(Locale.getDefault());

                    String spmVersion = iBookcaseVersionData
                        .findOnlineBookcaseVersion(spmProgramDocnbr);

                    newhref =
                        url + PGMS + spmProgramDocnbr + DataConstants.VERSIONS_URI_PATH + spmVersion
                            + MANS + spmManualDocnbr + TRG_URI_PATH + target
                            + this.queryParam + (target.indexOf('#') == -1 ? "#" + target : "");

                } else {
                    // Do not output NOPAGE anchor
                    isNOPAGE = true;
                    return;
                }
            } else {
                newhref = externalRef(oldhref);
            }
            attrs.setValue(hrefindex, newhref);
        }

    }

    private String externalRef(String oldhref) throws TechpubsException {
        String newhref = null;
        if (isExternalRef(oldhref)) {
            newhref = oldhref;
        } else {
            int iPos = oldhref.indexOf(':');
            if (iPos != -1) {
                String docnbr = oldhref.substring(0, iPos).toLowerCase(Locale.getDefault());
                String target = oldhref.substring(iPos + 1).toLowerCase(Locale.getDefault());

                newhref = getNewhref(docnbr, target);
                if (newhref != null) {
                    return newhref;
                }

                newhref =
                    url + PGMS + programItem.getProgramDocnbr() + DataConstants.VERSIONS_URI_PATH
                        + programItem
                        .getProgramOnlineVersion() + MANS + docnbr + TRG_URI_PATH
                        + target + this.queryParam + (target.indexOf('#') == -1 ? "#" + target
                        : "");

                if(resourceData.cortonaTargetLookup(programItem, docnbr, target).size() == 1){
                    newhref = PORTALS_UI_SRC_WIDGETS_GE_TECH_PUB_DIST_CORTONA_SOLO_UNIVIEW_HTML + newhref;
                }

            } else {
                String[] oldhrefsplit = oldhref.split("#", 2);
                newhref = url + PGMS + programItem.getProgramDocnbr() + MANS + docNbr + FILE
                    + oldhrefsplit[0] + this.queryParam + (oldhrefsplit.length == 2 ? "#"
                    + oldhrefsplit[1] : "");

                if (resourceData.cortonaCheck(programItem, docNbr, oldhref)) {
                    newhref = PORTALS_UI_SRC_WIDGETS_GE_TECH_PUB_DIST_CORTONA_SOLO_UNIVIEW_HTML + newhref;
                } else {
                    // if file extension is htm or html we use /file endpoint
                    if ((oldhref.matches("(.*).htm")) || oldhref.matches("(.*).html")) {
                        newhref =
                            url + PGMS + programItem.getProgramDocnbr()
                                + DataConstants.VERSIONS_URI_PATH + programItem
                                .getProgramOnlineVersion() + MANS + docNbr + FILE
                                + oldhrefsplit[0] + this.queryParam + (oldhrefsplit.length == 2
                                ? "#" + oldhrefsplit[1] : "");

                    } else {
                        newhref =
                            url + PGMS + programItem.getProgramDocnbr()
                                + DataConstants.VERSIONS_URI_PATH + programItem
                                .getProgramOnlineVersion() + MANS + docNbr + RES
                                + oldhrefsplit[0] + this.queryParam + (oldhrefsplit.length == 2
                                ? "#" + oldhrefsplit[1] : "");
                    }
                }
            }
        }

        return newhref;
    }

    private String getNewhref(String docnbr, String target) throws TechpubsException {
        String newhref;
        List<String> cortonaBooks = resourceData.cortonaBooks();

        if (cortonaBooks.contains(programItem.getProgramDocnbr())) {
            List<CortonaTargetDto> cortonaTargetDtos = resourceData
                .cortonaTargetLookup(programItem, docnbr, target);

            if (cortonaTargetDtos.size() == 1) {
                CortonaTargetDto cortonaTargetDto = cortonaTargetDtos.get(0);
                String pageblkKey = cortonaTargetDto.getPageblkKey();
                String formattedTarget = target.indexOf('#') == -1 ? "#" + target : "";
                newhref = url + PGMS + programItem.getProgramDocnbr()
                    + DataConstants.VERSIONS_URI_PATH
                    + programItem.getProgramOnlineVersion() + MANS + pageblkKey
                    + TRG_URI_PATH + target
                    + this.queryParam + formattedTarget;

                return PORTALS_UI_SRC_WIDGETS_GE_TECH_PUB_DIST_CORTONA_SOLO_UNIVIEW_HTML + newhref;
            }
        }
        return null;
    }

    /**
     * Empty element.
     */
    @Override
    public void emptyElement(QName qname, XMLAttributes xmlattributes,
        Augmentations augmentations) {
        startElement(qname, xmlattributes, augmentations);
        isNOPAGE = false;
        isInScript = false;
        super.endElement(qname, augmentations);
    }

    /**
     * Validates External References
     */
    public boolean isExternalRef(String oldhref) {
        for (int i = 0; i < VALIDLINKS.size(); i++) {
            if (oldhref.startsWith(VALIDLINKS.get(i))) {
                return true;
            }
        }
        return false;
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
        if ("a".equalsIgnoreCase(element.rawname) && isNOPAGE) {
            // Do not output NOPAGE anchor
            isNOPAGE = false;
            return;
        } else if ("script".equalsIgnoreCase(element.rawname)) {
            isInScript = false;

            String newhref = "";
            String replaceHrefScript = "(.*?href:\")(.*?)(#(.*?))?(\".*?)";
            String newText = sbScriptText.toString().replaceAll(replaceHrefScript,
                "$1" + newhref + "$2" + this.queryParam + "$3$5");
            super.characters(new XMLString(newText.toCharArray(), 0, newText.length()), augs);
        }

        super.endElement(element, augs);
    }
}
