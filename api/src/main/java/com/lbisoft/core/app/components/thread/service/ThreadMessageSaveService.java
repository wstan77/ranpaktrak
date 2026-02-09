package com.lbisoft.core.app.components.thread.service;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.thread.model.ThreadMessage;
import com.lbisoft.core.app.components.thread.provider.ThreadMessageProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.ThreadMessageDbMapper;
import com.lbisoft.core.app.db.model.generated.ThreadMessageDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ThreadMessageSaveService {

  private final Dao<ThreadMessageDb, Integer> dao;
  private final ThreadMessageProvider provider = new ThreadMessageProvider();
  private final ThreadMessageTagService tagService;
  private final ThreadMessageAttachmentService attachmentService;
  private final ThreadMessageGetService getService;
  private final ThreadReadStatusSaveService threadReadStatusSaveService;

  public ThreadMessageSaveService(ThreadMessageDbMapper mapper, ThreadMessageTagService tagService,
      ThreadMessageAttachmentService attachmentService, ThreadMessageGetService getService,
      ThreadReadStatusSaveService threadReadStatusSaveService) {
    this.tagService = tagService;
    this.attachmentService = attachmentService;
    this.getService = getService;
    this.threadReadStatusSaveService = threadReadStatusSaveService;
    this.dao = Dao.getDao(mapper, ThreadMessageDb::getThreadMessageId);
  }

  public ThreadMessage post(ThreadMessage threadMessage, Integer threadId, AppUser user) {
    threadMessage.setThreadId(threadId);
    ThreadMessageDb db = provider.reverseMap(threadMessage);
    db.setRecordStatusLk(RecordStatusLks.APPROVED);
    dao.insert(db, user);

    if (threadMessage.getTags() != null) {
      for (var tag : threadMessage.getTags()) {
        tag.setThreadMessageId(db.getThreadMessageId());
        tagService.save(tag, user);
      }
    }

    if (threadMessage.getAttachments() != null) {
      for (var attachment : threadMessage.getAttachments()) {
        attachment.setThreadMessageId(db.getThreadMessageId());
        attachmentService.save(attachment, user);
      }
    }

    threadReadStatusSaveService.setLastReadMessageId(threadId, db.getThreadMessageId(), user);

    return getService.get(db.getThreadMessageId()).orElseThrow(() -> new ResourceNotFoundException());
  }
}
