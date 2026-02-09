package com.lbisoft.core.app.components.systemdates.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.db.mapper.generated.SystemDateDbMapper;
import com.lbisoft.core.app.db.model.generated.SystemDateDb;
import com.lbisoft.core.app.db.model.generated.SystemDateDbExample;
import com.lbisoft.core.app.db.model.generated.SystemDateDbKey;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class SystemDateLookupService {
  private final Dao<SystemDateDb, SystemDateDbKey> dao;

  @Autowired
  public SystemDateLookupService(SystemDateDbMapper mapper) {
    dao = Dao.getDao(mapper, sd -> sd);
  }

  @Transactional(readOnly = true)
  public List<SystemDateDb> getAllSystemDates() {
    return getSystemDatesByYear(null);
  }

  @Transactional(readOnly = true)
  public List<SystemDateDb> getCurrentSystemDates() {
    return getSystemDatesByYear(LocalDate.now().getYear());
  }

  @Transactional(readOnly = true)
  public List<SystemDateDb> getSystemDatesByYear(Integer systemDateYear) {
    SystemDateDbExample example = new SystemDateDbExample();
    if (systemDateYear != null) {
      example.createCriteria().andSystemDateYearEqualTo(systemDateYear);
    }
    return dao.select(example);
  }
}
