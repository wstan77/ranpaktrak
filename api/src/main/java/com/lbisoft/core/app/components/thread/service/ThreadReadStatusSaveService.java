package com.lbisoft.core.app.components.thread.service;

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.ThreadReadStatusDbMapper;
import com.lbisoft.core.app.db.model.generated.ThreadReadStatusDb;
import com.lbisoft.core.app.db.model.generated.ThreadReadStatusDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.dao.DaoWithoutPrimaryKey;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ThreadReadStatusSaveService {

  private final DaoWithoutPrimaryKey<ThreadReadStatusDb> dao;

  public ThreadReadStatusSaveService(ThreadReadStatusDbMapper mapper) {
    this.dao = Dao.getDao(mapper);
  }

  public void setLastReadMessageId(Integer threadId, Integer lastReadMessageId, AppUser user) {
    ThreadReadStatusDb db = new ThreadReadStatusDb();
    db.setThreadId(threadId);
    db.setUserId(user.getUserId());
    db.setLastReadMessageId(lastReadMessageId);

    ThreadReadStatusDbExample byThreadAndUser = new ThreadReadStatusDbExample();
    byThreadAndUser.createCriteria().andThreadIdEqualTo(threadId).andUserIdEqualTo(user.getUserId());

    var existing = dao.selectOne(byThreadAndUser);
    if (existing.isPresent()) {
      dao.update(db, byThreadAndUser, user);
    } else {
      dao.insert(db, user);
    }

  }
}
