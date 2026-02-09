package com.lbisoft.core.app.components.systemdates.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.lookup.service.Lookups;
import com.lbisoft.core.app.components.systemdates.model.SystemDate;
import com.lbisoft.core.app.db.model.generated.LkSystemDateTypeDb;
import com.lbisoft.core.app.db.model.generated.SystemDateDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;
import com.lbisoft.core.framework.provider.ProviderBuilder;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class SystemDateGetService {
  private final Lookups lookups;
  private final AbstractProvider<SystemDateDb, SystemDate> systemDateProvider = new ProviderBuilder<>(
      SystemDateDb.class, SystemDate.class)
      .field("systemDateTypeLk", "systemDateType.systemDateTypeLk")
      .defineOneToOne(LkSystemDateTypeDb.class, SystemDate::setSystemDateType,
          ChildrenMap.matchNested(SystemDate::getSystemDateType, LkSystemDateTypeDb::getSystemDateTypeLk))
      .build();

  public SystemDateGetService(Lookups lookups) {
    this.lookups = lookups;
  }

  @Transactional(readOnly = true)
  public List<SystemDate> getCurrentSystemDates() {
    List<LkSystemDateTypeDb> lks = lookups.getListByClass(LkSystemDateTypeDb.class);
    return systemDateProvider.mapAll(lookups.getList("systemDates"), lks);
  }

  @Transactional(readOnly = true)
  public SystemDate getCurrentSystemDate(String systemDateTypeLk) {
    if (systemDateTypeLk == null) {
      return null;
    }
    LkSystemDateTypeDb lk = lookups.getItem(LkSystemDateTypeDb.class, systemDateTypeLk);
    SystemDateDb db = (SystemDateDb) lookups.getItem("systemDates", systemDateTypeLk);
    return systemDateProvider.map(db, lk);
  }
}
