package com.lbisoft.core.app.components.thread.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.thread.model.ThreadMessageAttachment;
import com.lbisoft.core.app.components.thread.provider.ThreadMessageAttachmentProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.ThreadMessageAttachmentDbMapper;
import com.lbisoft.core.app.db.model.generated.ThreadMessageAttachmentDb;
import com.lbisoft.core.app.db.model.generated.ThreadMessageAttachmentDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.utils.FileDownloadHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CoreService
public class ThreadMessageAttachmentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadMessageAttachmentService.class);

  private final Dao<ThreadMessageAttachmentDb, Integer> dao;
  private final ThreadMessageAttachmentProvider provider = new ThreadMessageAttachmentProvider();

  public ThreadMessageAttachmentService(ThreadMessageAttachmentDbMapper mapper) {
    this.dao = Dao.getDao(mapper, ThreadMessageAttachmentDb::getThreadMessageAttachmentId);
  }

  public Integer save(ThreadMessageAttachment attachment, AppUser user) {
    ThreadMessageAttachmentDb db = provider.reverseMap(attachment);
    db.setRecordStatusLk(RecordStatusLks.APPROVED);
    dao.insert(db, user);
    return db.getThreadMessageAttachmentId();
  }

  public List<ThreadMessageAttachment> getAttachments(List<Integer> threadMessageIds) {
    if (threadMessageIds == null || threadMessageIds.isEmpty()) {
      return Collections.emptyList();
    }
    ThreadMessageAttachmentDbExample byMessageIds = new ThreadMessageAttachmentDbExample();
    byMessageIds.createCriteria().andThreadMessageIdIn(threadMessageIds);

    return provider.mapAll(dao.select(byMessageIds));
  }

  public List<ThreadMessageAttachment> getAttachments(Integer threadMessageId) {
    if (threadMessageId == null) {
      return Collections.emptyList();
    }
    return getAttachments(Collections.singletonList(threadMessageId));
  }

  @Transactional(readOnly = true)
  public void sendAttachment(Integer threadMessageAttachmentId, HttpServletResponse response,
      HttpServletRequest request,
      AppUser user) {
    var attachmentDb = dao.select(threadMessageAttachmentId).orElseThrow(ResourceNotFoundException::new);
    try (InputStream fileStream = new ByteArrayInputStream(attachmentDb.getAttachment())) {
      FileDownloadHelper.sendFile(fileStream, attachmentDb.getFileName() + '.' + attachmentDb.getFileExt(),
          attachmentDb.getAttachment().length,
          false, response, request);
    } catch (IOException e) {
      LOGGER.error("Failed to fetch note attachment", e);
    }
  }

}
