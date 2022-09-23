package com.geaviation.techpubs.data.api;

import com.amazonaws.AmazonServiceException;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.ResourceMetaDataModel;
import java.util.List;
import java.util.Map;

import com.geaviation.techpubs.models.techlib.dto.CortonaTargetDto;
import com.geaviation.techpubs.models.techlib.dto.PageblkLookupDto;
import org.apache.lucene.document.Document;

/**
 * The Interface IResourceData.
 */
public interface IResourceData {

    /**
     * Prepare wrapped resource.
     *
     * @param embedDocument the embed document
     * @param contentType the content type
     * @param documentInfo the document info
     * @return the string
     */
    String prepareWrappedResource(String embedDocument, String contentType,
        DocumentInfoModel documentInfo);

    /**
     * Gets the CMM parts.
     *
     * @param publication the publication
     * @param fileId the file id
     * @param partsList the parts list
     * @return String the CMM parts
     */
    String getCMMParts(String publication, String fileId, List<String[]> partsList);

    /**
     * Return a file (artifact) from MongoDB along with metadata.
     *
     * @param fileId MongoDB artifact _id value
     * @return Map MongoDB artifact with metadata
     */
    Map<String, Object> getArtifact(String fileId);

    /**
     * Gets the logo.
     *
     * @param portalId the portal id
     * @return byte the logo
     */
    byte[] getLogo(String portalId);

    /**
     * Gets the HTML resource TD.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param filename the filename
     * @param bandwidth the bandwidth
     * @param multiBrowserDocumentRequired the multi browser document required
     * @param userIsExplicitlyEnabledToViewFile
     * @return the HTML resource TD
     */
    byte[] getHTMLResourceTD(ProgramItemModel programItem, String manual,
        String filename,
        String bandwidth, boolean multiBrowserDocumentRequired,  boolean userIsExplicitlyEnabledToViewFile)
        throws TechpubsException;

    byte[] getHTMLResourceTD(ProgramItemModel programItem, String manual, String target,
        String filename,
        String bandwidth, boolean multiBrowserDocumentRequired) throws TechpubsException;

    List<Document> getDocumentsByTargetIndex(ProgramItemModel programItem, String manual, String target);


    String getFilenameByBandwidthValue(String filename, ProgramItemModel programItem, String bookKey, String bandwidth, boolean multiBrowserDocumentRequired);

    byte[] getDocumentNotFoundFile();

    String displaySelectDocument(ProgramItemModel programItem, List<Document> documentList,
        String bandwidth, boolean multiBrowserDocumentRequired, String target);

    String displaySelectDocument(ProgramItemModel programItem, List<PageblkLookupDto> entities, String bandwidth, boolean multiBrowserDocumentRequired);

    @LogExecutionTime
    ResourceMetaDataModel getResourceNameTD(ProgramItemModel programItem, String manual,
        String target, String filename,
        String bandwidth, boolean multiBrowserDocumentRequired) throws TechpubsException;

    /**
     * Gets the binary resource TD.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param res the res
     * @return the binary resource TD
     */
    byte[] getCortonaResourceTD(ProgramItemModel programItem, String manual, String res)
        throws TechpubsException;


    /**
     * Gets the binary resource TD.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param res the res
     * @return the binary resource TD
     */
    byte[] getBinaryResourceTD(ProgramItemModel programItem, String manual, String res)
        throws TechpubsException;

    /**
     * Return TD CSS File
     *
     * @param res the TD CSS Resource
     * @return String - TD CSS File
     */
    String getCSSResource(String res);

    /**
     * Gets the HTML resource summary TD.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param filename the filename
     * @return the HTML resource summary TD
     */
    String getHTMLResourceSummaryTD(ProgramItemModel programItem, String manual, String filename);

    /**
     * Gets the prints the not available.
     *
     * @return the prints the not available
     */
    byte[] getPrintNotAvailable();

    /**
     * Gets the specified css stylesheet
     *
     * @return the string created from a css file
     */
    String getStylesheet(String filename);

    /**
     * Gets the binary resource TD from s3.
     *
     * @param programItem the program item
     * @param manual the manual
     * @param res the res
     * @return the binary resource TD
     */
    byte[] getBinaryResourceTDs3(ProgramItemModel programItem, String manual, String res) throws TechpubsException, AmazonServiceException;


    /**
     * As the name would imply, this checks to see if the resource is a cortona file.
     * @param programItem
     * @param manual
     * @param resourceName
     * @return
     */
    boolean cortonaCheck(ProgramItemModel programItem, String manual, String resourceName)
        throws TechpubsException;

    /**
     * Get a list of unique books that contain Cortona files
     * @return List of books containing cortona files
     * @throws TechpubsException
     */
    List<String> cortonaBooks() throws TechpubsException;

    /**
     * Look up Cortona Files by target
     * @param programItem The program item
     * @param manual The book containing pageblk's
     * @param target The target to search search pageblk's with
     * @return List of matches
     */
    List<CortonaTargetDto> cortonaTargetLookup(ProgramItemModel programItem, String manual, String target) throws TechpubsException;

    /**
     * Lookup Pageblk's by target
     * @param programItem The program item
     * @param manual The book containing pageblk's
     * @param target The target to search search pageblk's with
     * @return List of matches
     */
    List<PageblkLookupDto> pageblkTargetLookup(ProgramItemModel programItem, String bookKey, String target);

    /**
     * This method connects to techlib DB to identify if the passed file is Cortona file and returns
     * true else returns false.
     * @param fileName filename to check if cortona or not
     * @param version bookcase version
     * @param bookcase bookcase
     * @param book
     * @return true if cortona file else returns false
     */

    boolean cortonaFileCheck(String fileName, String version, String bookcase, String book);
}
