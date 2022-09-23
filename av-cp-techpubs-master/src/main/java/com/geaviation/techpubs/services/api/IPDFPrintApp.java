package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.exceptions.PdfPrintException;
import java.io.ByteArrayOutputStream;

/**
 * The Interface IPDFPrintApp.
 */
@FunctionalInterface
public interface IPDFPrintApp {

    /**
     * Convert HTML to PDF.
     *
     * @param htmlURL the html URL
     * @param directHtmlURL the direct html URL
     * @param inputHTML the input HTML
     * @return the byte array output stream
     * @throws PdfPrintException the pdf print exceptions
     */
    ByteArrayOutputStream convertHTMLToPDF(String htmlURL, String directHtmlURL, String inputHTML)
        throws PdfPrintException;

}