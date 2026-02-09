package com.lbisoft.core.app.components.systemvalues.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.systemvalues.model.SystemValue;
import com.lbisoft.core.app.db.mapper.generated.SystemValueDbMapper;
import com.lbisoft.core.app.db.model.generated.SystemValueDb;
import com.lbisoft.core.app.db.model.generated.SystemValueDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class SystemValueGetService {
  private final Dao<SystemValueDb, String> dao;
  private final AbstractProvider<SystemValueDb, SystemValue> systemValueProvider = ProviderBuilder
      .simple(SystemValueDb.class, SystemValue.class);

  public SystemValueGetService(SystemValueDbMapper mapper) {
    dao = Dao.getDao(mapper, sv -> sv.getSystemValueKey());
  }

  @Transactional(readOnly = true)
  public List<SystemValue> getSystemValues() {
    SystemValueDbExample example = new SystemValueDbExample();
    example.createCriteria().andActiveFlgEqualTo(true);
    List<SystemValueDb> systemValues = dao.select(example);
    return systemValueProvider.mapAll(systemValues);
  }

  @Transactional(readOnly = true)
  public SystemValue getSystemValue(String systemValueKey) {
    if (systemValueKey == null) {
      return null;
    }
    SystemValueDb db = dao.select(systemValueKey).orElse(null);
    return systemValueProvider.map(db);
  }
}
