package com.lbisoft.core.app.components.quicksearch.model;

import java.util.List;

import com.lbisoft.core.framework.search.model.QuickSearchForm;

public class MyQuickSearchForm extends QuickSearchForm {
  private String type;
  private Integer year;
  private Integer excludeId;
  private List<String> personTypeLks = List.of("EMP", "MGR");
  private String levelOfPlayLk;
  private boolean isTopSearch;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public Integer getExcludeId() {
    return excludeId;
  }

  public void setExcludeId(Integer excludeId) {
    this.excludeId = excludeId;
  }

  public String getLevelOfPlayLk() {
    return levelOfPlayLk;
  }

  public void setLevelOfPlayLk(String levelOfPlayLk) {
    this.levelOfPlayLk = levelOfPlayLk;
  }

  public List<String> getPersonTypeLks() {
    return personTypeLks;
  }

  public void setPersonTypeLks(List<String> personTypeLks) {
    this.personTypeLks = personTypeLks;
  }

  public boolean getIsTopSearch() {
    return isTopSearch;
  }

  public void setIsTopSearch(boolean isTopSearch) {
    this.isTopSearch = isTopSearch;
  }
}
