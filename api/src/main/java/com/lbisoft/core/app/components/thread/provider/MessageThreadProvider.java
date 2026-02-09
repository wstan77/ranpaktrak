package com.lbisoft.core.app.components.thread.provider;

import com.lbisoft.core.app.components.thread.model.MessageThread;
import com.lbisoft.core.app.components.thread.model.ThreadMessage;
import com.lbisoft.core.app.db.model.generated.ThreadDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class MessageThreadProvider extends AbstractProvider<ThreadDb, MessageThread> {

  @Override
  protected void defineClassMap(ClassMapBuilder<ThreadDb, MessageThread> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<MessageThread> childrenMap) {
    childrenMap
        .defineList(ThreadMessage.class, MessageThread::setMessages,
            (thread, message) -> thread.getThreadId().equals(message.getThreadId()));
  }

}
