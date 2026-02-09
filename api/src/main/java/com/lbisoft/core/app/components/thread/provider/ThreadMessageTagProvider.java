package com.lbisoft.core.app.components.thread.provider;

import com.lbisoft.core.app.components.thread.model.ThreadMessageTag;
import com.lbisoft.core.app.db.model.generated.ThreadMessageTagDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class ThreadMessageTagProvider extends AbstractProvider<ThreadMessageTagDb, ThreadMessageTag> {

  @Override
  protected void defineClassMap(ClassMapBuilder<ThreadMessageTagDb, ThreadMessageTag> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<ThreadMessageTag> childrenMap) {
  }

}
