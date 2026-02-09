package com.lbisoft.core.app.components.thread.service;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.thread.model.MessageThread;
import com.lbisoft.core.app.components.thread.model.ThreadCreateForm;
import com.lbisoft.core.app.components.thread.provider.MessageThreadProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.ThreadDbMapper;
import com.lbisoft.core.app.db.model.generated.ThreadDb;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.permissions.PermissionsHelper;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ThreadSaveService {

  private final Dao<ThreadDb, Integer> dao;
  private final MessageThreadProvider provider = new MessageThreadProvider();
  private final ThreadGetService getService;

  public ThreadSaveService(ThreadDbMapper mapper, ThreadGetService getService) {
    this.dao = Dao.getDao(mapper, ThreadDb::getThreadId);
    this.getService = getService;
  }

  public MessageThread create(ThreadCreateForm form, AppUser user) {
    ThreadDb thread = new ThreadDb();
    thread.setReferenceTypeLk(form.getReferenceTypeLk());
    thread.setReferenceId(form.getReferenceId());
    thread.setSubject(form.getSubject());
    thread.setRecordStatusLk(RecordStatusLks.APPROVED);

    dao.insert(thread, user);

    return getService.getThreadById(thread.getThreadId(), user).orElse(null);
  }

  public boolean delete(Integer threadId, AppUser user) {
    return dao.select(threadId)
        .filter(toDelete -> PermissionsHelper.hasPermission(user, PermissionLevel.DELETE, provider.map(toDelete)))
        .map(toDelete -> {
          toDelete.setThreadId(threadId);
          toDelete.setRecordStatusLk(RecordStatusLks.DELETED);

          dao.update(toDelete, user);
          return true;
        }).orElse(false);
  }
}
