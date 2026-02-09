package com.lbisoft.core.app.components.systemdates.model;

import java.util.List;

import com.lbisoft.core.app.db.model.generated.SystemDateDb;

public class SystemDateForm {
  private List<SystemDateDb> systemDates;

  public List<SystemDateDb> getSystemDates() {
    return systemDates;
  }

  public void setSystemDates(List<SystemDateDb> systemDates) {
    this.systemDates = systemDates;
  }
}
