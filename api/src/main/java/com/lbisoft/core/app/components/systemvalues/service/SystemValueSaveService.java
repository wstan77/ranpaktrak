package com.lbisoft.core.app.components.systemvalues.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lbisoft.core.app.components.lookup.service.Lookups;
import com.lbisoft.core.app.components.systemvalues.model.SystemValue;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.SystemValueDbMapper;
import com.lbisoft.core.app.db.model.generated.SystemValueDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class SystemValueSaveService {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  private final Dao<SystemValueDb, String> dao;
  private final AbstractProvider<SystemValueDb, SystemValue> systemValueProvider = ProviderBuilder
      .simple(SystemValueDb.class, SystemValue.class);
  private final Lookups lookups;

  public SystemValueSaveService(SystemValueDbMapper mapper, Lookups lookups) {
    dao = Dao.getDao(mapper, sv -> sv.getSystemValueKey());
    this.lookups = lookups;
  }

  public ResponseObject saveSystemValues(List<SystemValue> systemValues, AppUser user) {
    LOGGER.info("Saving system values with user id: " + user.getUserId());
    dao.insertOrUpdate(systemValueProvider.reverseMapAll(systemValues), user);
    lookups.reloadAllLookups(user);
    return new ResponseObject().addResponseInfo("Saved System Values");
  }
}
