package com.lbisoft.core.app.components.thread.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.lbisoft.core.app.components.thread.model.ThreadMessage;
import com.lbisoft.core.app.components.thread.provider.ThreadMessageProvider;
import com.lbisoft.core.app.db.mapper.generated.ThreadMessageDbMapper;
import com.lbisoft.core.app.db.model.generated.ThreadMessageDb;
import com.lbisoft.core.app.db.model.generated.ThreadMessageDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ThreadMessageGetService {

  private final Dao<ThreadMessageDb, Integer> dao;
  private final ThreadMessageProvider provider = new ThreadMessageProvider();
  private final ThreadMessageTagService tagService;
  private final ThreadMessageAttachmentService attachmentService;

  public ThreadMessageGetService(ThreadMessageDbMapper mapper, ThreadMessageTagService tagService,
      ThreadMessageAttachmentService attachmentService) {
    this.tagService = tagService;
    this.attachmentService = attachmentService;
    this.dao = Dao.getDao(mapper, ThreadMessageDb::getThreadMessageId);
  }

  public List<ThreadMessage> getByThreadIds(List<Integer> threadIds) {
    if (threadIds == null || threadIds.isEmpty()) {
      return Collections.emptyList();
    }
    ThreadMessageDbExample byThreadIds = new ThreadMessageDbExample();
    byThreadIds.createCriteria().andThreadIdIn(threadIds);

    var dbs = dao.select(byThreadIds);

    return populateDbs(dbs);
  }

  public List<ThreadMessage> getByThreadId(Integer threadId) {
    if (threadId == null) {
      return Collections.emptyList();
    }
    return getByThreadIds(Collections.singletonList(threadId));
  }

  public Optional<ThreadMessage> get(Integer threadMessageId) {
    return dao.select(threadMessageId).map(db -> populateDbs(Collections.singletonList(db)).get(0));
  }

  private List<ThreadMessage> populateDbs(List<ThreadMessageDb> dbs) {
    List<Integer> messageIds = dbs.stream().map(ThreadMessageDb::getThreadMessageId).distinct().toList();

    var tags = tagService.getTags(messageIds);
    var attachments = attachmentService.getAttachments(messageIds);

    return provider.mapAll(dbs, tags, attachments);
  }
}
