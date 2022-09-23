package com.geaviation.techpubs.data.util;

import static com.geaviation.techpubs.data.util.DataConstants.IC_PAGEBLK_TYPE;
import static com.geaviation.techpubs.data.util.DataConstants.MANUAL_PAGEBLK_TYPE;
import static com.geaviation.techpubs.data.util.DataConstants.SB;
import static com.geaviation.techpubs.data.util.DataConstants.SBALERT_PAGEBLK_TYPE;
import static com.geaviation.techpubs.data.util.DataConstants.TR_PAGEBLK_TYPE;

import com.amazonaws.util.DateUtils;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

public class DataUtil {

    private static final Logger log = LogManager.getLogger(DateUtil.class);

    private static final long HOUR = (long) 3600 * 1000; // in milli-seconds.

    private DataUtil() {
    }

    public static boolean isNotNullandEmpty(final String strData) {
        boolean isValid = false;
        if (strData != null && !"".equals(strData.trim())) {
            isValid = true;
        }
        return isValid;
    }

    public static String getAsString(Object obj) {
        if (null != obj) {
            return obj.toString().trim();
        }
        return DataConstants.EMPTY_STRING;
    }

    public static Double getAsDouble(String obj) {
        if (null != obj && !"".equalsIgnoreCase(obj.trim())) {
            return Double.parseDouble(obj);
        }
        return Double.parseDouble(DataConstants.UNDEFINED_NUMBER_VALUE);
    }

    public static String formatDateTime(Date date) {
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateTimeFormat.format(date);
    }

    public static Date parseDateTime(String date) throws ParseException {
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateTimeFormat.parse(date);
    }

    public static Document getYearMonthDocument() {
        return new Document(DataConstants.ADD, Arrays.asList(
            new Document(DataConstants.MULTIPLY, Arrays.asList(DataConstants.UPLOADYEAR_VAL, 100)),
            new Document(DataConstants.COND, Arrays.asList(
                new Document(
                    DataConstants.EQ,
                    Arrays.asList(DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                        DataConstants.JANUARY)),
                1,
                new Document(DataConstants.COND, Arrays.asList(
                    new Document(DataConstants.EQ,
                        Arrays.asList(DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                            DataConstants.FEBRUARY)),
                    2,
                    new Document(DataConstants.COND, Arrays.asList(
                        new Document(DataConstants.EQ, Arrays
                            .asList(DataConstants.DOCUMENTS_UPLOADMONTH_VAL, DataConstants.MARCH)),
                        3,
                        new Document(DataConstants.COND, Arrays.asList(
                            new Document(DataConstants.EQ, Arrays.asList(
                                DataConstants.DOCUMENTS_UPLOADMONTH_VAL, DataConstants.APRIL)),
                            4,
                            new Document(DataConstants.COND, Arrays.asList(new Document(
                                    DataConstants.EQ,
                                    Arrays.asList(DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                        DataConstants.MAY)),
                                5,
                                new Document(DataConstants.COND, Arrays.asList(
                                    new Document(DataConstants.EQ,
                                        Arrays.asList(
                                            DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                            DataConstants.JUNE)),
                                    6,
                                    new Document(DataConstants.COND, Arrays.asList(
                                        new Document(DataConstants.EQ, Arrays.asList(
                                            DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                            DataConstants.JULY)),
                                        7,
                                        new Document(DataConstants.COND, Arrays.asList(
                                            new Document(DataConstants.EQ, Arrays
                                                .asList(DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                                    DataConstants.AUGUST)),
                                            8,
                                            new Document(DataConstants.COND,
                                                Arrays.asList(
                                                    new Document(
                                                        DataConstants.EQ,
                                                        Arrays.asList(
                                                            DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                                            DataConstants.SEPTEMBER)),
                                                    9,
                                                    new Document(
                                                        DataConstants.COND,
                                                        Arrays.asList(
                                                            new Document(
                                                                DataConstants.EQ,
                                                                Arrays.asList(
                                                                    DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                                                    DataConstants.OCTOBER)),
                                                            10,
                                                            new Document(
                                                                DataConstants.COND,
                                                                Arrays.asList(
                                                                    new Document(
                                                                        DataConstants.EQ,
                                                                        Arrays.asList(
                                                                            DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                                                            DataConstants.NOVEMBER)),
                                                                    11,
                                                                    new Document(
                                                                        DataConstants.COND,
                                                                        Arrays.asList(
                                                                            new Document(
                                                                                DataConstants.EQ,
                                                                                Arrays.asList(
                                                                                    DataConstants.DOCUMENTS_UPLOADMONTH_VAL,
                                                                                    DataConstants.DECEMBER)),
                                                                            12,
                                                                            0))))))))))))))))))))))))));
    }

    public static int monthAsInt(String strMonth) {
        if (strMonth == null) {
            return 0;
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("MMM").parse(strMonth));
            return cal.get(Calendar.MONTH) + 1;
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }

    /**
     * Return a File Type based on filename extension
     *
     * @param filename - Name of file
     * @return String - File Type (e.g. HTML, PDF)
     */
    public static String getFileType(String filename) {
        String fileType = "";

        if (filename != null) {
            String fileExtension = FilenameUtils.getExtension(filename);
            if ("html".equalsIgnoreCase(fileExtension) || "htm".equalsIgnoreCase(fileExtension)) {
                fileType = "HTML";
            } else {
                fileType = (fileExtension == null ? "" : fileExtension.toUpperCase());
            }
        }

        return fileType;
    }

    /**
     * Method determines which portal domain the user is coming from.
     *
     * @param portalId Portal Id indicating which portal user is using.
     * @return String String value of domain name (i.e. geaviation.com)
     * @throws TechpubsException Error is portal id is invalid
     */
    public static String getPortalDomain(String portalId, String environment)
        throws TechpubsException {
        String hostDomain;
        switch (portalId) {
            case "CWC":
                hostDomain = ".my.geaviation.com";
                break;
            case "GEHonda":
                hostDomain = ".my.gehonda.com";
                break;
            default:
                log.error("generateCloudFrontCookies -- invalid portal Id " + portalId);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        if (!("prod".equals(environment)) && !("local".equals(environment))) {
            hostDomain = environment + hostDomain;
        }

        return hostDomain;
    }

    /**
     * Method determines the distribution domain resource path based off the environment, portal id
     * and engine program. If the environment is local the dev distribution is used.
     *
     * @param portalId portal id used to identify which portal the user is coming from.
     * @param environment environment used to determine which environment to create distribution
     * domain for.
     * @param program engine program
     * @return String string value of distribution domain
     * @throws TechpubsException error if invalid portal id.
     */
    public static String getPortalDistributionResourcePath(String portalId, String environment,
        String program) throws TechpubsException {
        String distributionDomain;
        switch (portalId) {
            case "CWC":
                distributionDomain = ".my.geaviation.com";
                break;
            case "GEHonda":
                distributionDomain = ".my.gehonda.com";
                break;
            default:
                log.error("generateCloudFrontCookies -- invalid portal Id " + portalId);
                throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        if ("prod".equals(environment)) {
            distributionDomain = "docs" + distributionDomain;
        } else if ("local".equals(environment)) {
            distributionDomain = "docs.dev" + distributionDomain;
        } else {
            distributionDomain = "docs." + environment + distributionDomain;
        }

        return "https://" + distributionDomain + "/" + program + "/dvd/*";
    }

    /**
     * Method takes in a PKCS8 formatted PEM key string and parses it to generate a privateKey
     * object.
     *
     * @param pkcs8Pem PKCS8 formatted PEM key string
     * @return PrivateKey Private Key object used to create CloudFront signed cookies.
     * @throws NoSuchAlgorithmException error seeting key factory instance
     * @throws InvalidKeySpecException error generating private key
     */
    public static PrivateKey readPrivateKeyFromString(String pkcs8Pem)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Remove the "BEGIN" and "END" lines, as well as any whitespace
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

        byte[] decoded = Base64.decodeBase64(pkcs8Pem.getBytes());
        // extract the private key
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    /**
     * Method returns the current date in GMT timezone.
     *
     * @return Date returns date object in GMT timezone.
     */
    public static Date getCurrentDateInGmt() {
        String timeNowInGmt = DateUtils.formatISO8601Date(new Date());
        return DateUtils.parseISO8601Date(timeNowInGmt);
    }

    /**
     * Method returns the current date plus 30 minutes in GMT timezone.
     *
     * @return Date returns date object in GMT timezone plus 30 minutes.
     */
    public static Date getCurrentDatePlusThirtyMinutesInGmt() {
        String timeNowPlusSixInGmt = DateUtils
            .formatISO8601Date(new Date(new Date().getTime() + HOUR / 2));
        return DateUtils.parseISO8601Date(timeNowPlusSixInGmt);
    }

    /**
     * Method takes in a ByteBuffer and converts it into a string.
     *
     * @param byteBuffer byteBuffer object
     * @return String converted byteBuffer into String
     */
    public static String getStringFromByteBuffer(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new String(bytes);
    }

  public static boolean isPageBlkType(String type){
      if (type == null)
          return false;
      String lowerCaseType = type.toLowerCase();
      return lowerCaseType.equals(IC_PAGEBLK_TYPE)
              || lowerCaseType.equals(TR_PAGEBLK_TYPE)
              || lowerCaseType.equals(MANUAL_PAGEBLK_TYPE)
              || lowerCaseType.equals(SBALERT_PAGEBLK_TYPE)
              || lowerCaseType.equals(SB);
  }

  public static String createFileResourceUri(String bookcaseKey, String bookKey, String filename, String tocVersion){
      String resourceUri = null;
      if (TechpubsAppUtil.isNotNullandEmpty(bookcaseKey) && TechpubsAppUtil.isNotNullandEmpty(bookKey) && TechpubsAppUtil.isNotNullandEmpty(filename)){
          resourceUri =  DataConstants.TECHPUBS_FILE_URI.replace(DataConstants.BOOKCASE_KEY_URI_PARAMETER, bookcaseKey)
              .replace(DataConstants.TOC_VERSION_URI_PARAMETER, tocVersion)
                  .replace(DataConstants.BOOK_KEY_URI_PARAMETER, bookKey)
                  .replace(DataConstants.FILENAME_URI_PARAMETER, filename);
      }

      return resourceUri;
  }

    public static boolean isSBAlert(String type){
        return DataConstants.ALERT.equalsIgnoreCase(type) || DataConstants.ALERT_COVER.equalsIgnoreCase(type);

    }
}
