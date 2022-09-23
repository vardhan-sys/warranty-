package com.geaviation.techpubs.services.impl.download;

import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.download.PdfDocumentDownloadRequest;
import com.geaviation.techpubs.models.download.PdfDownloadFuture;
import com.geaviation.techpubs.services.api.IManualApp;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfDownloadImpl {

  private static final Logger logger = LogManager.getLogger(PdfDownloadImpl.class);

  @Autowired
  private IManualApp iManualApp;

  @Autowired
  private IResourceData iResourceData;

  /**
   * <p>Implements an output stream filter for writing files in the ZIP file format after
   * converting HTML to PDF file content.</p>
   *
   * @param baseURI                        contains PDF URL to generate files.
   * @param ssoId                          contains logged in user SSO information.
   * @param portalId                       contains logged in user portal information such as
   *                                       CWC/GEHonda.
   * @param pdfDocumentDownloadRequestList contains the list of file information to download.
   * @return StreamingOutput streamed response object.
   */

  public StreamingResponseBody downloadAndZipMultiplePdfFiles(String baseURI, String ssoId, String portalId, List<PdfDocumentDownloadRequest> pdfDocumentDownloadRequestList) {
    return outputStream -> {
      try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream))) {

        List<CompletableFuture<PdfDownloadFuture>> futures = getPdfConvertedFile(
            pdfDocumentDownloadRequestList, baseURI, ssoId, portalId);

        for (CompletableFuture<PdfDownloadFuture> byteObject : futures) {
          String sourceFile = byteObject.get().getFileName() + ".pdf";

          logger.info("Start zipping downloaded PDF files. ");

          zipMultipleFiles(sourceFile, byteObject.get().getPdfByte(), zipOut);
        }

        outputStream.flush();
      } catch (IOException | InterruptedException | ExecutionException exception) {
        logger.error(String
            .format("Error occurred during zipping the directory: %s ", exception.getMessage()));

      } finally {
        outputStream.close();
      }

    };

  }

  private void zipMultipleFiles(String sourceFile, byte[] pdfByte, ZipOutputStream zipOut)
      throws IOException {
    ZipEntry zipEntry = new ZipEntry(sourceFile);
    zipOut.putNextEntry(zipEntry);
    zipOut.write(pdfByte);

  }

  private List<CompletableFuture<PdfDownloadFuture>> getPdfConvertedFile(
      List<PdfDocumentDownloadRequest> pdfDocumentDownloadRequestList,
      String baseURI, String ssoId,
      String portalId) {

    List<CompletableFuture<PdfDownloadFuture>> futures = new ArrayList<>();
    for (PdfDocumentDownloadRequest pdfDocumentDownload : pdfDocumentDownloadRequestList) {

      CompletableFuture<PdfDownloadFuture> future = CompletableFuture
          .supplyAsync(() -> {
            PdfDownloadFuture response = new PdfDownloadFuture();

            logger
                .info("Calling  iManualApp.getPrintHTMLResourceTD method to download PDF files. ");

            //Check for Cortona files for IPC/IPD doc types to download directly without converting to PDF
            boolean isCortonaFile = iResourceData
                .cortonaFileCheck(pdfDocumentDownload.getFilename(),
                    pdfDocumentDownload.getVersion(), pdfDocumentDownload.getBookcase(),
                    pdfDocumentDownload.getBook());

            byte[] pdfByte;
            try {
              if (isCortonaFile) {
                String fileName =
                    FilenameUtils.removeExtension(pdfDocumentDownload.getFilename()) + ".pdf";

                pdfByte = iManualApp
                    .getBinaryResourceTD(ssoId, portalId,
                        pdfDocumentDownload.getBookcase(),
                        pdfDocumentDownload.getVersion(),
                        pdfDocumentDownload.getBook(), fileName);

              } else {
                pdfByte = iManualApp
                    .getPrintHTMLResourceTD(baseURI, ssoId, portalId,
                        pdfDocumentDownload.getBookcase(),
                        pdfDocumentDownload.getVersion(),
                        pdfDocumentDownload.getBook(), pdfDocumentDownload.getFilename());
              }
                response.setPdfByte(pdfByte);
                response.setFileName(
                    pdfDocumentDownload.getBookcase() + "-" +pdfDocumentDownload.getBook() + "-"
                        + FilenameUtils
                        .removeExtension(pdfDocumentDownload.getFilename()));
            } catch (TechpubsException e) {
              logger.error(String
                  .format("Error occurred during asynchronous download of PDF files : %s ",
                      e.getMessage()));

            }

            return response;
          });

      futures.add(future);

    }

    return futures;
  }

}