package com.lbisoft.core.app.components.user.model;

import java.util.Collections;
import java.util.List;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.Constants.ReferenceTypeLks;
import com.lbisoft.core.app.components.quicksearch.model.Taggable;
import com.lbisoft.core.framework.utils.NormalizeUtils;

import lombok.Data;

@Data
public class UserGroup implements Taggable {
  private Integer userGroupId;
  private String description;
  private boolean activeFlg;

  @Override
  public String getDisplayName() {
    return description;
  }

  @Override
  public Integer getId() {
    return userGroupId;
  }

  @Override
  public List<String> getSearchTerms() {
    return description != null ? NormalizeUtils.splitNameParts(description) : Collections.emptyList();
  }

  @Override
  public String getType() {
    return ReferenceTypeLks.USER_GROUP;
  }

  @Override
  public String getRecordStatusLk() {
    return activeFlg ? RecordStatusLks.APPROVED : RecordStatusLks.DELETED;
  }

  @Override
  public String getIcon() {
    return "";
  }

  @Override
  public List<String> getDetails() {
    return List.of("User Group");
  }

  @Override
  public boolean getActiveFlg() {
    return activeFlg;
  }
}
