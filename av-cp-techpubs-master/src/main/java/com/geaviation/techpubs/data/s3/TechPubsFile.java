package com.geaviation.techpubs.data.s3;

import java.io.File;

public class TechPubsFile extends File{
  public TechPubsFile(String filePath){
    super(filePath);
  }

  private String name;

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name){
    this.name = name;
  }
}
