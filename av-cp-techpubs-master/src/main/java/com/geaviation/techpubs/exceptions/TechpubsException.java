package com.geaviation.techpubs.exceptions;

import com.geaviation.dss.service.common.exception.BaseException;

public class TechpubsException extends BaseException {

    private static final long serialVersionUID = -99363059404244834L;

    private final TechpubsAppError techpubsAppError;

    public TechpubsException(TechpubsAppError techpubsAppError) {
        super(techpubsAppError.getErrorCode(), techpubsAppError.getErrorMsg());
        this.techpubsAppError = techpubsAppError;
    }

    public TechpubsException(TechpubsAppError techpubsAppError, Throwable cause) {
        super(techpubsAppError.getErrorCode(), techpubsAppError.getErrorMsg(), cause);
        this.techpubsAppError = techpubsAppError;
    }

    public TechpubsException(TechpubsAppError techpubsAppError, String message) {
        super(techpubsAppError.getErrorCode(), techpubsAppError.getErrorMsg() + "; " + message);
        this.techpubsAppError = techpubsAppError;
    }

    public TechpubsAppError getTechpubsAppError() {
        return techpubsAppError;
    }

    public enum TechpubsAppError {
            OK(0, "OK"),
            INTERNAL_ERROR(4001, "Internal Error"),
            INVALID_PARAMETER(4002, "Invalid Parameter"),
            NO_PROGRAMS_AVAILABLE(4003, "No Programs Available"),
            NOT_AUTHORIZED(4004, "Not Authorized"),
            RESOURCE_NOT_FOUND(4005, "Resource Not Found"),
            PING_SERVICE_FAILED(4006, "Ping service failed"),
            REST_SERVICE_FAILED(4007, "Error while calling external service"),
            TEST_CASE_FAILED(4008, "Error in techpub test case"),
            DOWNLOAD_MAX_LIMIT_REACHED_ERROR(4009, "Max limit to download files is 20"),
            UPLOADING_FILE_TO_S3_ERROR(4010, "Error while uploading file to s3"),
            CONVERT_MULTIPART_TO_FILE_ERROR(4011, "Error while converting the multi-part file to file"),
            DATA_NOT_FOUND(4012, "Data not found in the Database"),
            DOWNLOADING_FILE_FROM_S3_ERROR(4013, "Error downloading file from S3"),
            INVALID_DOCUMENT_IDENTIFIERS(4014, "Combination of site, type, and document number already exists"),
        ;

        private final int errorCode;
        private final String errorMsg;

        private TechpubsAppError(int errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }

        public static TechpubsAppError fromErrorCode(int errorCode) {
            for (TechpubsAppError e : values()) {
                if (e.getErrorCode() == errorCode) {
                    return e;
                }
            }
            return null;
        }

        public int getErrorCode() {
            return errorCode;
        }

        @Override
        public String toString() {
            return errorMsg;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }
}
