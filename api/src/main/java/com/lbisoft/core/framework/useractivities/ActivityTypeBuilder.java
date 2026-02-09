package com.lbisoft.core.framework.useractivities;

public class ActivityTypeBuilder {
  private final ActivityType type;

  public ActivityTypeBuilder(String activityLk) {
    type = new ActivityType();
    type.setActivityLk(activityLk);
  }

  public ActivityTypeBuilder description(String description) {
    type.setDescription(description);
    return this;
  }

  public ActivityTypeBuilder resourceName(String resourceName) {
    type.setResourceName(resourceName);
    return this;
  }

  public ActivityTypeBuilder relatedIdName(String relatedIdName) {
    type.setRelatedIdName(relatedIdName);
    return this;
  }

  public ActivityTypeBuilder relatedNumberName(String relatedNumberName) {
    type.setRelatedNumberName(relatedNumberName);
    return this;
  }

  public ActivityTypeBuilder relatedDateName(String relatedDateName) {
    type.setRelatedDateName(relatedDateName);
    return this;
  }

  public ActivityTypeBuilder relatedBoolName(String relatedBoolName) {
    type.setRelatedBoolName(relatedBoolName);
    return this;
  }

  public ActivityTypeBuilder relatedStringName(String relatedStringName) {
    type.setRelatedStringName(relatedStringName);
    return this;
  }

  public ActivityTypeBuilder relatedObjectName(String relatedObjectName) {
    type.setRelatedObjectName(relatedObjectName);
    return this;
  }


  public ActivityType build() {
    return type;
  }
}
