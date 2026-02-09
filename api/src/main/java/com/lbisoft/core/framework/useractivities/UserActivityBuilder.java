package com.lbisoft.core.framework.useractivities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserActivityBuilder {
    private final UserActivity activity;

    public UserActivityBuilder(ActivityType type) {
        activity = new UserActivity();
        activity.setType(type);
    }

    public UserActivityBuilder resourceId(Integer resourceId) {
        activity.setResourceId(resourceId);
        return this;
    }

    public UserActivityBuilder relatedId(Integer relatedId) {
        activity.setRelatedId(relatedId);
        return this;
    }

    public UserActivityBuilder relatedNumber(BigDecimal relatedNumber) {
        activity.setRelatedNumber(relatedNumber);
        return this;
    }

    public UserActivityBuilder relatedString(String relatedString) {
        activity.setRelatedString(relatedString);
        return this;
    }

    public UserActivityBuilder relatedDate(LocalDateTime relatedDate) {
        activity.setRelatedDate(relatedDate);
        return this;
    }

    public UserActivityBuilder relatedBool(Boolean relatedBool) {
        activity.setRelatedBool(relatedBool);
        return this;
    }

    public UserActivityBuilder relatedObject(Object relatedObject) {
        activity.setRelatedObject(relatedObject);
        return this;
    }

    public UserActivity build() {
        return activity;
    }
}
