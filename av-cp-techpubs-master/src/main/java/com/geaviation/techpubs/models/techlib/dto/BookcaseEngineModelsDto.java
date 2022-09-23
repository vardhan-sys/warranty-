package com.geaviation.techpubs.models.techlib.dto;

import java.util.List;

public class BookcaseEngineModelsDto {

  private List<String> engineModels;

  public BookcaseEngineModelsDto(){}

  public BookcaseEngineModelsDto(List<String> engineModels){
    this.engineModels = engineModels;
  }

  public List<String> getEngineModels() {
    return engineModels;
  }

  public void setEngineModels(List<String> engineModels) {
    this.engineModels = engineModels;
  }
}
