package com.geaviation.techpubs.exceptions.fault;

public class TechpubsServiceFault {

    private final Integer errorCode;
    private final String uimsg;
    private final String logmsg;
    private final String type;
    private final String desc;

    public TechpubsServiceFault(Integer errorCode, String uimsg, String logmsg, String type,
        String desc) {
        this.errorCode = errorCode;
        this.uimsg = uimsg;
        this.logmsg = logmsg;
        this.type = type;
        this.desc = desc;

    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getUimsg() {
        return uimsg;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public String getLogmsg() {
        return logmsg;
    }

}
