package com.lbisoft.core.app.components.thread.service;

import java.util.Collections;
import java.util.List;

import com.lbisoft.core.app.components.thread.model.ThreadMessageTag;
import com.lbisoft.core.app.components.thread.provider.ThreadMessageTagProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.ThreadMessageTagDbMapper;
import com.lbisoft.core.app.db.model.generated.ThreadMessageTagDb;
import com.lbisoft.core.app.db.model.generated.ThreadMessageTagDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class ThreadMessageTagService {

  private final Dao<ThreadMessageTagDb, Integer> dao;
  private final ThreadMessageTagProvider provider = new ThreadMessageTagProvider();

  public ThreadMessageTagService(ThreadMessageTagDbMapper mapper) {
    this.dao = Dao.getDao(mapper, ThreadMessageTagDb::getThreadMessageTagId);
  }

  public List<ThreadMessageTag> getTags(List<Integer> threadMessageIds) {
    if (threadMessageIds == null || threadMessageIds.isEmpty()) {
      return Collections.emptyList();
    }
    ThreadMessageTagDbExample byMessageIds = new ThreadMessageTagDbExample();
    byMessageIds.createCriteria().andThreadMessageIdIn(threadMessageIds);

    return provider.mapAll(dao.select(byMessageIds));
  }

  public List<ThreadMessageTag> getTags(Integer threadMessageId) {
    if (threadMessageId == null) {
      return Collections.emptyList();
    }
    return getTags(Collections.singletonList(threadMessageId));
  }

  public void save(ThreadMessageTag tag, AppUser user) {
    dao.insert(provider.reverseMap(tag), user);
  }
}
