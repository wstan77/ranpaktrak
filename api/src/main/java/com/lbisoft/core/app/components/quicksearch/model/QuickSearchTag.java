package com.lbisoft.core.app.components.quicksearch.model;

import java.util.List;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.framework.response.ResponseObject;

public class QuickSearchTag extends ResponseObject implements Taggable {
  private Integer id;
  private String displayName;
  private String icon;
  private List<String> details;
  private String type;
  private boolean activeFlg;

  public String getTagKey() {
    return "[" + type + "_" + id + "]";
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getDetails() {
    return details;
  }

  public void setDetails(List<String> details) {
    this.details = details;
  }

  public boolean getActiveFlg() {
    return activeFlg;
  }

  public void setActiveFlg(boolean activeFlg) {
    this.activeFlg = activeFlg;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public String getRecordStatusLk() {
    return RecordStatusLks.APPROVED;
  }

  @Override
  public List<String> getSearchTerms() {
    return getDetails();
  }
}
