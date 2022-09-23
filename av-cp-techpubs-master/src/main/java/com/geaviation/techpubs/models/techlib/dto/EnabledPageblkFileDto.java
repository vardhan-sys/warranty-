package com.geaviation.techpubs.models.techlib.dto;

public class EnabledPageblkFileDto {
  private boolean isAnEnabledSMMPageblk;
  private String ded_filename;
  private  String offline_filename;
  private String online_filename;

  public EnabledPageblkFileDto(String online_filename,
      String offline_filename,
      String ded_filename,
      boolean isAnEnabledSMMPageblk) {
    this.isAnEnabledSMMPageblk = isAnEnabledSMMPageblk;
    this.ded_filename = ded_filename;
    this.offline_filename = offline_filename;
    this.online_filename = online_filename;
  }

  public boolean isAnEnabledSMMPageblk() {
    return isAnEnabledSMMPageblk;
  }

  public void setAnEnabledSMMPageblk(boolean anEnabledSMMPageblk) {
    isAnEnabledSMMPageblk = anEnabledSMMPageblk;
  }

  public String getDed_filename() {
    return ded_filename;
  }

  public void setDed_filename(String ded_filename) {
    this.ded_filename = ded_filename;
  }

  public String getOffline_filename() {
    return offline_filename;
  }

  public void setOffline_filename(String offline_filename) {
    this.offline_filename = offline_filename;
  }

  public String getOnline_filename() {
    return online_filename;
  }

  public void setOnline_filename(String online_filename) {
    this.online_filename = online_filename;
  }

  @Override
  public String toString() {
    return "EnabledPageblk{" +
        "isAnEnabledSMMPageblk=" + isAnEnabledSMMPageblk +
        ", ded_filename="
        + ded_filename +
        ", offline_filename="
        + offline_filename +
        ", online_filename=" + online_filename +
        '}';
  }
}
