package com.geaviation.techpubs.services.util;

import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.exceptions.PdfPrintException;
import com.geaviation.techpubs.services.api.IPDFConverter;
import com.lowagie.text.DocumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Component
public class PDFConverter implements IPDFConverter {

    @Autowired
    protected IResourceData iResourceData;

    private static final String PAGE_SIZE = "B4";
    private static final Logger log = LogManager.getLogger(PDFConverter.class);
    
    @Override
    public ByteArrayOutputStream convertHTMLToPDF(String htmlURL, String directHtmlURL,
        String inputHTML, String ssoId, String portalId, String header, String footer)
        throws PdfPrintException {

        ByteArrayOutputStream baos = null;
        String resourceHTML = inputHTML;
        try {
            String appUrl = getApplicationURL(htmlURL, portalId);
            log.info("In convertHTMLToPDF(), appUrl==> " + appUrl);
            if (isNotNullandEmpty(appUrl) && resourceHTML.contains(appUrl)) {
                resourceHTML = resourceHTML.replace(appUrl, directHtmlURL);
            }

            String serverUri = getServerUri(resourceHTML);
            String html = readHTMLFromURL(resourceHTML, ssoId, portalId);
            Document doc = parseHTML(html);
            String convertedHtml = processHTMLDoc(doc, serverUri, ssoId, portalId, header, footer);
            baos = createPDF(convertedHtml, ssoId, portalId);

        } catch (IOException | DocumentException | RuntimeException e) {
            log.error(e);
            throw new PdfPrintException(0, e.getMessage(), e.getCause());
        }
        return baos;
    }

    /**
     * Creates a PDF document given a processed HTML string
     *
     * @param convertedHtml => the processed html string
     * @return the byte array output stream
     * @throws Exception => a pdf print exceptions
     */
    private ByteArrayOutputStream createPDF(String convertedHtml, String ssoId, String portalId) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        log.info("ITextRenderer initialized...");
        renderer.getSharedContext().setReplacedElementFactory(new MediaReplacedElementFactory(
            renderer.getSharedContext().getReplacedElementFactory(), ssoId, portalId));

        log.info("Replacing images...");
        renderer.setDocumentFromString(convertedHtml);
        renderer.layout();
        renderer.createPDF(baos);
        log.info("PDF generated...");
        return baos;
    }

    /**
     * Reads content of HTML document given its url
     *
     * @param urlString => url where the document will be read from
     * @return content of the html document
     * @throws Exception => a pdf print exceptions
     */
    private String readHTMLFromURL(String urlString, String ssoId, String portalId) throws IOException {
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();

        urlConnection.addRequestProperty("sm_ssoid", ssoId);
        urlConnection.addRequestProperty("portal_id", portalId);

        BufferedReader in = new BufferedReader(
            new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        StringBuilder urlContent = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            urlContent.append(inputLine);
            urlContent.append(" ");
        }
        in.close();

        return urlContent.toString();
    }

    private String getServerUri(String inputHTML) throws MalformedURLException {
        URL fileURL = new URL(inputHTML);
        return fileURL.getProtocol() + "://" + fileURL.getHost() + ":" + fileURL.getPort();
    }

    private Document parseHTML(String html) {
        String htmlString = html.replaceAll("&bull;", "*");
        htmlString = htmlString.replaceAll("&ndash;", "-");
        htmlString = htmlString.replaceAll("&nbsp;", " ");
        htmlString = htmlString
            .replaceAll("&DocumentTypemiddot;", "<span class='alignMid'>.</span>");
        return Jsoup.parse(htmlString);
    }

    private String processHTMLDoc(Document doc, String serverUri, String ssoId, String portalId, String header, String footer)
        throws IOException {
        Boolean isIcn = false;
        String programType = "";
        String programTitle = "";
        List<Node> nodes = doc.childNodes();

        // Fixes doctype publicid systemid issue for new passport docs
        for (Node node : nodes) {
            if (node instanceof DocumentType) {
                DocumentType documentType = (DocumentType) node;
                documentType.attr("publicid", "");
                break;
            }
        }

        Elements svgLinks = doc.select("a[href~=ICN.*.svg");
        if (!svgLinks.isEmpty()) {
            isIcn = true;

            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> links = new ArrayList<>();
            
            for (Element link : svgLinks) {
                String href = link.attr("href");
                
                link.attr("href", "#" + href);
                if (!links.contains(href)) {
                    links.add(href);
                    titles.add(link.text());
                }
            }

            if (!links.isEmpty()) {
                doc.body().append("<h3 class='glossary'>LIST OF FIGURES</h3>");
            }

            for (int i = 0; i < links.size(); i++) {
                doc.body().append("<center class='figure'><figure><img id='" + links.get(i) + "' src='" + links.get(i)
                        + "' width='500'></img><br/><figcaption><b>" + titles.get(i)
                        + "</b></figcaption></figure><center>");
            }
        }

        // Find links that have a href attribute containing ICN and .htm (meaning it's an image viewer doc)
        Elements links = doc.select("a[href~=ICN.*.htm");
        if (!links.isEmpty()) {
            isIcn = true;
            for (Element link : links) {
                String href = link.attr("href");

                String html = readHTMLFromURL(
                    serverUri + href, ssoId, portalId); // Read contents to extract image src and title
                Document icnDoc = parseHTML(html);

                Elements h3 = icnDoc.select("h3"); // Selects all h3 elements
                // Selects all images elements with extensions matching regex.

                Elements images = icnDoc.select("img[src~=(?i)\\.(png|jpe?g|svg)]");

                // Gets image title and src. ICN docs will only have 1 title and 1 image.
                String title = h3.get(0).text();
                String imgSrc = images.get(0).attr("src");

                // based on observation, the span before the link will contain figure title (if
                // any; otherwise empty string) and the link text will be the sheet title
                String figureTitle = "";
                List<TextNode> textNodes = link.parent().textNodes();
                if (!textNodes.isEmpty()) {
                    TextNode node = textNodes.get(0);
                    figureTitle = node.toString() + " ";
                }

                link.parent()
                    .append("<center class='procedure-image'><figure><img src='" + imgSrc
                        + "' width='500'></img><br/><figcaption><b>" + figureTitle + link.text()
                        + ": " + title
                        + "</b></figcaption></figure><center>");
            }
        }

        // To Handle Special Chars - start
        Document.OutputSettings settings = doc.outputSettings();
        settings.prettyPrint(false);
        settings.escapeMode(Entities.EscapeMode.xhtml);
        settings.charset("UTF-8");

        log.info("Document OutputSettings Done");
        // To Handle Special Chars - end
        Element scriptEl = doc.select("script").first();
        if (scriptEl != null) {
            scriptEl.empty();
        }

        String style =
            "<style>\n img{height:100%; width:100%;}\n" + ".alignMid {vertical-align: middle;}\n"
                + ".sheetdiv table { table-layout: fixed; }"
                + ".footerContentStyle {text-align:justify;border-left:1px solid;"
                + "border-right:1px solid;margin-top:3px;padding:0px 3px;}\n" + "div.header {\n"
                + "display: block; text-align:center;font-weight:bold;"
                + "border-bottom:1px solid #000000;\n"
                + "position: running(header);}\n" + "div.footer {\n"
                + "display: block; text-align: center;border-top: 1px solid;\n"
                + "position:  running(footer);}\n"
                + "div.content {page-break-after: always;}"
                + "@media print { @page { size: " + PAGE_SIZE
                + "; margin-top: 2.0cm; margin-bottom: 2.5cm; margin-left: 1cm; margin-right: 1cm; } }"
                + "@page { size: " + PAGE_SIZE
                + "; margin-top: 2.0cm; margin-bottom: 2.5cm; margin-left: 1cm; margin-right: 1cm; }"
                + "@page { @top-center { content: element(header) }}\n "
                + "@page { @bottom-center { content: element(footer) }}"
                + "#pagetext:before { content: \" Page  \" } "
                + "#pagenumber:before { content:  counter(page) \" of \" counter(pages)  "
                + "}</style>";

        Element body = doc.body();
        Element meta = doc.select("body[data-techpubmeta").first();
        if (meta != null) {
            String metadata = meta.attr("data-techpubmeta");
            try {
                JSONObject json = new JSONObject(metadata);
                programType = json.getString("type");
                programTitle = json.getString("programtitle");
            } catch (JSONException e) {
                log.error(e);
            }
        }

        Element styleEl = doc.head();
        // if its passport doc use a special stylesheet for corrections.
        switch (programTitle) {
            case "PASSPORT20":
                styleEl
                    .append("<style>\n" + iResourceData.getStylesheet("s1000d.css") + "</style>");
                // if the passport doc has no procedure images or its a frontmatter doc then ensure table rows are displayed correctly.
                if (programType.equalsIgnoreCase("FM") || !isIcn) {
                    styleEl.append("<style>\n tr {page-break-inside:avoid;} </style>");
                }
                break;
            default:
                break;
        }
        styleEl.append(style);

        if (isNotNullandEmpty(footer)) {
            String changedFooter = footer
                .replace("@Page", "<div id='pagetext'/><div id='pagenumber'/>");
            String footerString = generateFooter(changedFooter);
            body.prepend(footerString);
        }
        log.info("Footer settings Done");
        if (isNotNullandEmpty(header)) {
            String changedHeader = header.replace("@Page", "<span id='pagenumber'/>");
            String headerString = generateHeader(changedHeader, programType);
            body.prepend(headerString);
        }
        log.info("header settings Done");
        // Header Footer - end
        scriptEl = doc.select("link").first();
        if (null != scriptEl) {
            String linkref = scriptEl.attr("href").toString();
            scriptEl.attr("href", serverUri + linkref);
        }
        for (Element img : doc.select("img[src]")) {
            img.attr("src", serverUri + img.attr("src"));
            img.attr("style", "");
        }

        log.info("Loop settings Done");
        return doc.toString();
    }

    private String generateHeader(String header, String programType) {
        String[] headerElements = header.split("\\|");
        StringBuilder html = new StringBuilder();
        int i = 0;

        // If its a frontmatter document
        if (programType.equalsIgnoreCase("FM")) {
            for (String element : headerElements) {
                html.append("<center><b>" + element + "</b></center>");
            }
        } else {
            html.append("<div class='header'><table width=\"100%\" >");
            for (int j = 0; j < 3; j++) {
                html.append(
                    "<tr><td align='left'>" + headerElements[i++] + "</td><td align='center'>"
                        + headerElements[i++] + "</td><td align='right'>" + headerElements[i++]
                        + "</td></tr>");
            }
            html.append("</table> </div>");
        }
        return html.toString();
    }

    private String generateFooter(String footer) {
        String[] footerElements = footer.split("\\|");
        int i = 0;
        StringBuilder html = new StringBuilder();
        html.append("<div class='footer'><table width=\"100%\" >");
        for (int j = 0; j < 3; j++) {
            html.append("<tr>" + "<td width='15%' align=\"left\" >" + footerElements[i++] + "</td>"
                + "<td><div class=\"footerContentStyle\">" + footerElements[i++] + "</div></td>"
                + "<td width='15%' align=\"right\" >" + footerElements[i++] + "</td>" + "</tr>");
        }
        html.append("</table> </div>");
        return html.toString();
    }

    private String getApplicationURL(String htmlURL, String portalId) {
        String appUrl = "";
        if (isNotNullandEmpty(portalId)) {
            Map<String, String> portalAppUrlMap = getPortalConfigAsMap(htmlURL);
            appUrl = portalAppUrlMap.get(portalId.toUpperCase());
        }
        return appUrl;
    }

    private Map<String, String> getPortalConfigAsMap(String portalConfigString) {
        List<String> portalIdList = null;
        Map<String, String> portalConfigMap = new HashMap<>();
        portalIdList = new ArrayList<>(Arrays.asList(portalConfigString.split("\\|")));
        for (String id : portalIdList) {
            String[] parts = id.split("~", 2);
            portalConfigMap.put(parts[0], parts[1]);
        }
        return portalConfigMap;
    }

    private static boolean isNotNullandEmpty(final String strData) {
        boolean isValid = false;
        if (strData != null && !("").equals(strData.trim())) {
            isValid = true;
        }
        return isValid;
    }
}