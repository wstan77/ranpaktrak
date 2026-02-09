package com.lbisoft.core.app.components.thread.provider;

import com.lbisoft.core.app.components.thread.model.ThreadMessage;
import com.lbisoft.core.app.components.thread.model.ThreadMessageAttachment;
import com.lbisoft.core.app.components.thread.model.ThreadMessageTag;
import com.lbisoft.core.app.db.model.generated.ThreadMessageDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class ThreadMessageProvider extends AbstractProvider<ThreadMessageDb, ThreadMessage> {

  @Override
  protected void defineClassMap(ClassMapBuilder<ThreadMessageDb, ThreadMessage> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<ThreadMessage> childrenMap) {
    childrenMap
        .defineList(ThreadMessageTag.class, ThreadMessage::setTags,
            (message, tag) -> message.getThreadMessageId().equals(tag.getThreadMessageId()));
    childrenMap
        .defineList(ThreadMessageAttachment.class, ThreadMessage::setAttachments,
            (message, attachment) -> message.getThreadMessageId().equals(attachment.getThreadMessageId()));
  }

}
