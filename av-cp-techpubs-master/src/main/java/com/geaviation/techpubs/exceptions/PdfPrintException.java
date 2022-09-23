package com.geaviation.techpubs.exceptions;

import com.geaviation.dss.service.common.exception.BaseException;

public class PdfPrintException extends BaseException {

    private static final long serialVersionUID = -7711067618524360646L;

    public PdfPrintException(int errorCode) {
        super(errorCode);
    }

    public PdfPrintException(int errorCode, String message) {
        super(errorCode, message);
    }

    public PdfPrintException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}