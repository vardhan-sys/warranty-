package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.PdfPrintException;
import java.io.ByteArrayOutputStream;

/**
 * The Interface IPDFConverter.
 */
@FunctionalInterface
public interface IPDFConverter {

    /**
     * Convert HTML to PDF.
     *
     * @param htmlURL the html URL
     * @param directHtmlURL the direct html URL
     * @param inputHTML the input HTML
     * @return the byte array output stream
     * @throws PdfPrintException the pdf print exceptions
     */
    ByteArrayOutputStream convertHTMLToPDF(String htmlURL, String directHtmlURL, String inputHTML, String ssoId, String portalId, String header, String footer)
        throws PdfPrintException;

}