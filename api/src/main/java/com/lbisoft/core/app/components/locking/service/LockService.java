package com.lbisoft.core.app.components.locking.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.components.locking.model.Lock;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class LockService {
  private static final int DEFAULT_SECONDS_TO_TIMEOUT = 120;
  private Map<String, Lock> locks;

  public LockService() {
    locks = new ConcurrentHashMap<String, Lock>();
  }

  /**
   * Attempts to get a lock for a user on a specified resource. If the resource
   * already has a lock and allowRenew is false or it's owned different user, null
   * is returned. If the resource already has a lock for the current user and
   * allowRenew is true, the lock is renewed and the lock id is returned.
   * Otherwise, a new lock is created and it's lock id is returned.
   *
   * @param user             AppUser accessing the resource
   * @param resource         Unique identifier for the resource being accessed
   * @param allowRenew       Will return the existing lock if it's owned by the
   *                         accessing user
   * @param secondsToTimeout The number of seconds before the lock times out. -1
   *                         for default.
   * @return True if lock created, false if lock is unavailable
   */
  public boolean getLock(AppUser user, String resource, boolean allowRenew, int secondsToTimeout) {
    if (user == null) {
      user = getSystemAppUser();
    }
    if (secondsToTimeout < 0) {
      secondsToTimeout = DEFAULT_SECONDS_TO_TIMEOUT;
    }
    // Check for an existing lock
    if (locks.containsKey(resource)) {
      Lock existingLock = locks.get(resource);
      // If existing lock is expired, ignore it
      if (existingLock.isActive()) {
        if (allowRenew && existingLock.getUserId().equals(user.getUserId())) {
          // Current user already has a lock, renew it and send back the existing lock id
          existingLock.renew(secondsToTimeout);
          return true;
        } else {
          // Another user has an active lock on the resource, return a fail code
          return false;
        }
      }
    }

    // No user has an active lock on the resource, create a new lock
    Lock newLock = new Lock(user.getUserId(), secondsToTimeout);
    // Store new lock and return the unique lock id
    locks.put(resource, newLock);
    return true;
  }

  /**
   * Attempts to get a lock for a user on a specified resource. If the resource
   * already has a lock for a different user, null is returned. If the resource
   * already has a lock for the current user the lock is renewed and the lock id
   * is returned. Otherwise, a new lock is created and it's lock id is returned
   *
   * @param user     AppUser accessing the resource
   * @param resource Unique identifier for the resource being accessed
   * @return True if lock created, false if lock is unavailable
   */
  public boolean getLock(AppUser user, String resource) {
    return getLock(user, resource, true, -1);
  }

  public Integer getLockOwner(String resource) {
    if (locks.containsKey(resource)) {
      Lock lock = locks.get(resource);
      if (lock.isActive()) {
        return lock.getUserId();
      }
    }
    return null;
  }

  /**
   * Renews the specified lock to prevent it from expiring and returns the time
   * the lock will expire. If the lock is already expired or isn't found, null is
   * returned.
   *
   * @param resource Resource with the lock
   * @param owner    Expected owner of lock
   * @return The time the lock will expire
   */
  public LocalDateTime renewLock(AppUser owner, String resource) {
    if (locks.containsKey(resource)) {
      Lock existingLock = locks.get(resource);
      // If existing lock is expired, it's too late to renew
      if (existingLock.isActive() && owner.getUserId().equals(existingLock.getUserId())) {
        return existingLock.renew();
      }
    }
    return null;
  }

  /**
   * Removes the specified lock from the given resource.
   *
   * @param resource The locked resource
   * @param owner    The expected owner of the lock
   * @return true if the lock was found and removed, false otherwise
   */
  public Lock removeLock(AppUser owner, String resource) {
    if (locks.containsKey(resource)) {
      Lock existingLock = locks.get(resource);
      if (existingLock.isActive() && owner.getUserId().equals(existingLock.getUserId())) {
        locks.remove(resource);
        return existingLock;
      }
    }
    return null;
  }

  /**
   * Gets the amount of time remaining on the lock for the specified resource
   *
   * @param resource
   * @return Remaining seconds on lock if there's a current lock, null otherwise
   */
  public Long getSecondsRemainingOnLock(String resource) {
    // Check for an existing lock
    if (locks.containsKey(resource)) {
      Lock existingLock = locks.get(resource);
      // If existing lock is expired, ignore it
      if (existingLock.isActive()) {
        return existingLock.getRemainingTime();
      }
    }
    return null;
  }

  private AppUser getSystemAppUser() {
    AppUser system = new AppUser();
    system.setUserId(Constants.SYSTEM_USER_ID);
    system.setLastName("System");
    return system;
  }

}
