package com.geaviation.techpubs.services.excel.exception;

public class ExcelException extends Exception {

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String s, Exception e) {
        super(s, e);
    }

    public ExcelException(Exception e) {
        super(e);
    }
}
