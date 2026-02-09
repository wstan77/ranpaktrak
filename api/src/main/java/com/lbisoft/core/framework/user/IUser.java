package com.lbisoft.core.framework.user;

import java.util.List;

public interface IUser extends HasPermissions {
  String getUsername();

  Boolean getRegisteredFlg();

  List<? extends IRole> getRoles();

  Boolean getExtendedTimeoutFlg();
}
