package com.lbisoft.core.app.components.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.locking.service.LockService;
import com.lbisoft.core.app.components.lookup.service.Lookups;
import com.lbisoft.core.app.components.quicksearch.service.QuickSearchPersonService;
import com.lbisoft.core.app.components.quicksearch.service.QuickSearchUserGroupService;
import com.lbisoft.core.app.components.quicksearch.service.QuickSearchUserService;
import com.lbisoft.core.app.settings.CacheSettings;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
@Transactional(readOnly = true)
public class CacheService {
  private static final double NANOSECONDS_PER_SECOND = 1000000000.0;
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  private final Lookups lookups;

  private final LockService lockService;
  private final QuickSearchPersonService quickSearchPersonService;
  private final QuickSearchUserService quickSearchUserService;
  private final QuickSearchUserGroupService quickSearchUserGroupService;
  private static final String CACHE_REFRESH_LOCK = "PUBLIC_CACHE_REFRESH";
  private static final int MINUTES_BETWEEN_CACHE_REFRESH = 15;

  public CacheService(Lookups lookups, LockService lockService, QuickSearchPersonService quickSearchPersonService,
      QuickSearchUserService quickSearchUserService,
      QuickSearchUserGroupService quickSearchUserGroupService) {
    this.lookups = lookups;
    this.lockService = lockService;
    this.quickSearchPersonService = quickSearchPersonService;
    this.quickSearchUserService = quickSearchUserService;
    this.quickSearchUserGroupService = quickSearchUserGroupService;
  }

  public void populateCaches(boolean waitToComplete, CacheSettings settings) {
    Thread t = new Thread(() -> {
      long startTime = System.nanoTime();

      if (settings.getStandardLookups()) {
        lookups.loadLookups();
        quickSearchPersonService.populate();
        quickSearchUserService.populate();
        quickSearchUserGroupService.populate();
      }

      LOGGER.info(String.format("Finished populating caches in %.3f seconds",
          (System.nanoTime() - startTime) / NANOSECONDS_PER_SECOND));
    });

    t.start();
    if (waitToComplete) {
      try {
        t.join();
      } catch (InterruptedException e) {
        LOGGER.error("Issue with cache population thread", e);
      }
    }
  }

  public boolean populateCachesThrottled(boolean waitToComplete, CacheSettings settings) {
    boolean gotLock = lockService.getLock(null, CACHE_REFRESH_LOCK, false, MINUTES_BETWEEN_CACHE_REFRESH * 60);
    if (gotLock) {
      // Cache hasn't been refreshed in the past MINUTES_BETWEEN_CACHE_REFRESH minutes
      populateCaches(waitToComplete, settings);
      return true;
    } else {
      LOGGER.warn(
          String.format("Attempted to refresh cache but cache may not be refreshed for another %d seconds",
              lockService.getSecondsRemainingOnLock(CACHE_REFRESH_LOCK)));
    }
    // Cache has been refreshed too recently
    return false;
  }
}
