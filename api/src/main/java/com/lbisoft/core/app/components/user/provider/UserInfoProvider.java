package com.lbisoft.core.app.components.user.provider;

import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.app.db.model.generated.UserDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;
import org.springframework.stereotype.Component;
import ma.glasnost.orika.metadata.ClassMapBuilder;

@Component
public class UserInfoProvider extends AbstractProvider<UserDb, UserInfo> {

  @Override
  protected void defineClassMap(ClassMapBuilder<UserDb, UserInfo> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<UserInfo> childrenMap) {
  }

}
