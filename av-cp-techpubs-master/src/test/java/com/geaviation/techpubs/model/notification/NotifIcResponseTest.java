package com.geaviation.techpubs.model.notification;

import com.geaviation.techpubs.models.reviewer.NotifIc;
import com.geaviation.techpubs.models.reviewer.NotifIcResponse;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class NotifIcResponseTest {

    @Test
    public void processNotifMappingTest() throws ParseException {
        ArrayList<String> bookTitles = new ArrayList<>();
        bookTitles.add("CF6-80C2");
        bookTitles.add("GE9X");

        ArrayList<NotifIc> icDocs = new ArrayList<>();
        NotifIc ic1 = new NotifIc("{\"ic\": \"\", \"type\": \"\", \"mfile\": \"\", \"sbnbr\": \"\", \"update\":" +
                " \"true\", \"ictitle\": \"\", \"licensed\": \"\", \"atanbr\": \"ataTestNbr\"}"
                ,"testFileName1.htm","ic",
                "gek92451","File Name 1", "gek108746", "CF6-80C2",
                new SimpleDateFormat("yyyyMMdd").parse("20201022"));
        NotifIc ic2 = new NotifIc("{\"ic\": \"\", \"type\": \"\", \"mfile\": \"\", \"sbnbr\": \"\", \"update\":" +
                " \"true\", \"ictitle\": \"\", \"licensed\": \"\", \"atanbr\": \"ataTestNbr\"}","testFileName2.htm","ic",
                "gek92451","File Name 2", "gek108746", "CF6-80C2",
                new SimpleDateFormat("yyyyMMdd").parse("20201122"));

        NotifIc ic3 = new NotifIc("{\"ic\": \"\", \"type\": \"\", \"mfile\": \"\", \"sbnbr\": \"\", \"update\":" +
                " \"true\", \"ictitle\": \"\", \"licensed\": \"\", \"atanbr\": \"ataTestNbr\"}","testFileName3.htm","ic",
                "gek131810","File Name 3", "gek131810", "GE9X",
                new SimpleDateFormat("yyyyMMdd").parse("20201022"));

        icDocs.add(ic1);
        icDocs.add(ic2);
        icDocs.add(ic3);

        NotifIcResponse notifIcResponse = new NotifIcResponse();

        notifIcResponse.processNotifMapping(bookTitles, icDocs);

        HashMap<String, ArrayList<NotifIc>> expectedResponse = new HashMap<>();
        ArrayList<NotifIc> CF6expected = new ArrayList<>();
        ArrayList<NotifIc> GE9Xexpected = new ArrayList<>();
        CF6expected.add(ic1);
        CF6expected.add(ic2);
        GE9Xexpected.add(ic3);

        expectedResponse.put("CF6-80C2",CF6expected);
        expectedResponse.put("GE9X", GE9Xexpected);

        Assert.assertEquals(expectedResponse, notifIcResponse.getGekToIcNotifMapping());

    }

}
