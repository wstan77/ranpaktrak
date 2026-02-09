package com.lbisoft.core.app.components.thread.service;

import java.util.List;
import java.util.Optional;

import com.lbisoft.core.app.components.thread.model.MessageThread;
import com.lbisoft.core.app.components.thread.provider.MessageThreadProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.custom.ThreadExtendedMapper;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ThreadGetService {

  private final ThreadMessageGetService messageService;
  private final MessageThreadProvider provider = new MessageThreadProvider();
  private final ThreadExtendedMapper mapper;

  public ThreadGetService(ThreadMessageGetService messageService, ThreadExtendedMapper mapper) {
    this.messageService = messageService;
    this.mapper = mapper;
  }

  public List<MessageThread> getThreadsForReference(String referenceTypeLk, Integer referenceId, AppUser user) {
    var threads = mapper.getThreadsByReference(referenceTypeLk, referenceId, user.getUserId());
    return populate(threads);
  }

  public List<MessageThread> getThreadsForParticipant(AppUser user) {
    var threads = mapper.getThreadsByParticipant(user.getUserId());
    return populate(threads);
  }

  public List<MessageThread> getThreadsByIds(List<Integer> threadIds, AppUser user) {
    if (threadIds == null || threadIds.isEmpty()) {
      return null;
    }
    var threads = mapper.getThreadsByIds(threadIds, user.getUserId());
    return populate(threads);
  }

  public Optional<MessageThread> getThreadById(Integer threadId, AppUser user) {
    return getThreadsByIds(List.of(threadId), user).stream().findFirst();
  }

  private List<MessageThread> populate(List<MessageThread> threads) {
    List<Integer> threadIds = threads.stream().map(MessageThread::getThreadId).toList();
    var messages = messageService.getByThreadIds(threadIds);

    provider.childrenMap.mapChildrenAll(threads, messages);
    return threads;
  }
}
