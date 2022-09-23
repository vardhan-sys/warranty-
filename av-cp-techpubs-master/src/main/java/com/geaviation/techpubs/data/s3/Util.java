package com.geaviation.techpubs.data.s3;

import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.models.ProgramItemModel;
import org.springframework.stereotype.Component;

@Component
public class Util {

  private Util(){

  }

  public static String createVersionFolderS3ObjKey(ProgramItemModel programItem, String manual, String res){
    return new StringBuilder()
        .append(programItem.getProgramDocnbr() + "/")
        .append(programItem.getProgramOnlineVersion()+ "/")
        .append(DataConstants.DOC + "/")
        .append(manual + "/").append(res).toString();
  }

  public static String createProgramFolderS3ObjKey(ProgramItemModel programItem, String manual, String res){
    return new StringBuilder()
        .append(programItem.getProgramDocnbr() + "/")
        .append("program/")
        .append(DataConstants.DOC + "/")
        .append(manual + "/")
        .append(res).toString();
  }

  public static String createXmlFolderObjKey(ProgramItemModel programItem) {
    return new StringBuilder()
        .append(programItem.getProgramDocnbr() + "/")
        .append(programItem.getProgramOnlineVersion() + "/")
        .append("xml/")
        .append("toc.xml").toString();
  }

  public static String createProgramTargets(ProgramItemModel programItem) { //targetshadow/gek112865_lr/targetlive/5.1/targetindex
    return new StringBuilder()
        .append("targetshadow/")
        .append(programItem.getProgramDocnbr() + "/")
        .append("targetlive/")
        .append(programItem.getProgramOnlineVersion() + "/")
        .append("targetindex")
        .append("/_6xv.cfs").toString();
  }
}
