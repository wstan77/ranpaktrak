package com.lbisoft.core.app.components.thread.provider;

import com.lbisoft.core.app.components.thread.model.ThreadMessageAttachment;
import com.lbisoft.core.app.db.model.generated.ThreadMessageAttachmentDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class ThreadMessageAttachmentProvider
    extends AbstractProvider<ThreadMessageAttachmentDb, ThreadMessageAttachment> {

  @Override
  protected void defineClassMap(ClassMapBuilder<ThreadMessageAttachmentDb, ThreadMessageAttachment> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<ThreadMessageAttachment> childrenMap) {
  }

}
