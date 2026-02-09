package com.lbisoft.core.app.components.locking.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.lbisoft.core.framework.response.ResponseObject;

public class Lock extends ResponseObject {
  private Integer userId;
  private Integer secondsToTimeout;
  private LocalDateTime expirationTime;

  public Lock(Integer userId, Integer secondsToTimeout) {
    this.userId = userId;
    this.secondsToTimeout = secondsToTimeout;
    renew();
  }

  public Lock() {
  }

  public final LocalDateTime renew() {
    return renew(null);
  }

  public final LocalDateTime renew(Integer secondsToTimeout) {
    if (secondsToTimeout != null) {
      this.secondsToTimeout = secondsToTimeout;
    }
    expirationTime = LocalDateTime.now().plusSeconds(this.secondsToTimeout);

    return expirationTime;
  }

  public boolean isActive() {
    return expirationTime != null && expirationTime.isAfter(LocalDateTime.now());
  }

  public Integer getUserId() {
    return userId;
  }

  public Long getRemainingTime() {
    if (expirationTime == null) {
      return null;
    }

    return ChronoUnit.SECONDS.between(LocalDateTime.now(), expirationTime);
  }
}
