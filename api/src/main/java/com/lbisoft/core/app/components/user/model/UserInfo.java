package com.lbisoft.core.app.components.user.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.Constants.ReferenceTypeLks;
import com.lbisoft.core.app.components.quicksearch.model.Taggable;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.utils.NormalizeUtils;

public class UserInfo extends ResponseObject implements Taggable {
  private Integer userId;
  private String firstName;
  private String lastName;
  private String recordStatusLk;

  public String getFullName() {
    if (firstName == null || lastName == null) {
      return null;
    }
    return firstName + " " + lastName;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof UserInfo
        && Optional.ofNullable(userId).equals(Optional.ofNullable(((UserInfo) other).getUserId()));
  }

  @Override
  public int hashCode() {
    if (userId == null) {
      return 0;
    }
    return userId.hashCode();
  }

  @Override
  public Integer getId() {
    return userId;
  }

  @Override
  public String getDisplayName() {
    return getFullName();
  }

  @Override
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public List<String> getDetails() {
    return List.of("User");
  }

  public Integer getUserId() {
    return this.userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getRecordStatusLk() {
    return recordStatusLk;
  }

  public void setRecordStatusLk(String recordStatusLk) {
    this.recordStatusLk = recordStatusLk;
  }

  @Override
  public String getIcon() {
    return "user";
  }

  @Override
  public List<String> getSearchTerms() {
    return Stream.of(getFirstName(), getLastName())
        .filter(s -> s != null)
        .map(s -> NormalizeUtils.normalize(s))
        .toList();
  }

  @Override
  public String getType() {
    return ReferenceTypeLks.USER;
  }

  @Override
  public boolean getActiveFlg() {
    return !Constants.RecordStatusLks.DELETED.equals(getRecordStatusLk());
  }
}
