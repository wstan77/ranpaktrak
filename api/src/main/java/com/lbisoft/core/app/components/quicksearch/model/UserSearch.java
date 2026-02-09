package com.lbisoft.core.app.components.quicksearch.model;

import java.time.LocalDateTime;

public class UserSearch {
  private String searchTerm;
  private Integer resultCount;
  private Integer exactMatchesCount;
  private Integer resultNumSelected;
  private String selectedTypeLk;
  private Integer selectedId;
  private boolean fullSearchFlg = false;
  private LocalDateTime createDate;

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  public Integer getResultCount() {
    return resultCount;
  }

  public void setResultCount(Integer resultCount) {
    this.resultCount = resultCount;
  }

  public Integer getExactMatchesCount() {
    return exactMatchesCount;
  }

  public void setExactMatchesCount(Integer exactMatchesCount) {
    this.exactMatchesCount = exactMatchesCount;
  }

  public Integer getResultNumSelected() {
    return resultNumSelected;
  }

  public void setResultNumSelected(Integer resultNumSelected) {
    this.resultNumSelected = resultNumSelected;
  }

  public String getSelectedTypeLk() {
    return selectedTypeLk;
  }

  public void setSelectedTypeLk(String selectedTypeLk) {
    this.selectedTypeLk = selectedTypeLk;
  }

  public Integer getSelectedId() {
    return selectedId;
  }

  public void setSelectedId(Integer selectedId) {
    this.selectedId = selectedId;
  }

  public boolean getFullSearchFlg() {
    return fullSearchFlg;
  }

  public void setFullSearchFlg(boolean fullSearchFlg) {
    this.fullSearchFlg = fullSearchFlg;
  }

  public LocalDateTime getCreateDate() {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
  }
}
