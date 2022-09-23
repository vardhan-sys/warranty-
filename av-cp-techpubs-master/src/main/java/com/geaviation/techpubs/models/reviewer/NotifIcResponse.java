package com.geaviation.techpubs.models.reviewer;

import java.util.ArrayList;
import java.util.HashMap;

public class NotifIcResponse {

    private HashMap<String, ArrayList<NotifIc>> gekToIcNotifMapping = new HashMap<>();

    public HashMap<String, ArrayList<NotifIc>> getGekToIcNotifMapping() {
        return gekToIcNotifMapping;
    }

    public void setGekToIcNotifMapping(HashMap<String, ArrayList<NotifIc>> gekToIcNotifMapping) {
        this.gekToIcNotifMapping = gekToIcNotifMapping;
    }

    public void processNotifMapping(ArrayList<String> bookcaseTitles, ArrayList<NotifIc> icDocs) {
        for (String bookcaseTitle : bookcaseTitles ) {
            gekToIcNotifMapping.put(bookcaseTitle, new ArrayList<>());
        }
        for (NotifIc ic: icDocs) {
            gekToIcNotifMapping.get(ic.getBookcaseTitle()).add(ic);
        }
    }
}
