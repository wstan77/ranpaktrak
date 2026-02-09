package com.lbisoft.core.app.components.systemdates.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.components.lookup.service.Lookups;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.SystemDateDbMapper;
import com.lbisoft.core.app.db.model.generated.SystemDateDb;
import com.lbisoft.core.app.db.model.generated.SystemDateDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class SystemDateSaveService {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  private final Dao<SystemDateDb, SystemDateDbKey> dao;
  private final Lookups lookups;

  @Autowired
  public SystemDateSaveService(SystemDateDbMapper mapper, Lookups lookups) {
    dao = Dao.getDao(mapper, sd -> sd);
    this.lookups = lookups;
  }

  public ResponseObject saveSystemDates(List<SystemDateDb> systemDates, AppUser user) {
    LOGGER.info("Saving system dates with user id: " + user.getUserId());
    dao.insertOrUpdate(systemDates, user);
    lookups.reloadAllLookups(user);
    return new ResponseObject().addResponseInfo("Saved System Dates");
  }
}
