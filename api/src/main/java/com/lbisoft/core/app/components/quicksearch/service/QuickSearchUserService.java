package com.lbisoft.core.app.components.quicksearch.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.lbisoft.core.app.components.quicksearch.model.MyQuickSearchForm;
import com.lbisoft.core.app.components.user.event.UserBeforeDeleteEvent;
import com.lbisoft.core.app.components.user.event.UserBeforeDisableEvent;
import com.lbisoft.core.app.components.user.event.UserSavedEvent;
import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.app.components.user.service.UserService;
import com.lbisoft.core.framework.search.factor.impl.AlphabeticalFactor;
import com.lbisoft.core.framework.search.factor.impl.EditDistanceFactor;
import com.lbisoft.core.framework.search.factor.impl.ExactMatchFactor;
import com.lbisoft.core.framework.search.filter.ClassSpecificSearchFilter;
import com.lbisoft.core.framework.search.index.FuzzyMatchSearchIndex;
import com.lbisoft.core.framework.search.index.PrefixSearchIndex;
import com.lbisoft.core.framework.search.service.ChildQuickSearchService;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
@Transactional(readOnly = true)
public class QuickSearchUserService extends ChildQuickSearchService<Integer> {
  private static final Logger LOGGER = LoggerFactory.getLogger(QuickSearchUserService.class);
  private final UserService userService;

  public QuickSearchUserService(UserService userService) {
    super();
    this.userService = userService;

    addFactor(new ExactMatchFactor<>());
    addFactor(new EditDistanceFactor<>());
    addFactor(new AlphabeticalFactor<>());

    addFilter(ClassSpecificSearchFilter.builder(MyQuickSearchForm.class, UserInfo.class)
        .withFilter((form, user) -> !user.getUserId().equals(form.getExcludeId()))
        .build());
  }

  @Override
  public void populate() {
    long startTime = System.currentTimeMillis();
    List<UserInfo> users = userService.getActiveUserInfos();
    LOGGER.info("{} users loaded in {} ms", users.size(), System.currentTimeMillis() - startTime);
    startTime = System.currentTimeMillis();
    FuzzyMatchSearchIndex<UserInfo, Integer> fuzzyIndex = new FuzzyMatchSearchIndex<>(users);
    PrefixSearchIndex<UserInfo, Integer> prefixIndex = new PrefixSearchIndex<>(users);
    this.indexes = List.of(fuzzyIndex, prefixIndex);
    LOGGER.info("{} users indexed in {} ms", users.size(), System.currentTimeMillis() - startTime);

  }

  @Async
  @TransactionalEventListener(fallbackExecution = true)
  public void onUserSaved(UserSavedEvent event) {
    updateUser(event.getUserId());
  }

  @Async
  @EventListener
  public void onUserDeleted(UserBeforeDeleteEvent event) {
    removeUser(event.getUserId());
  }

  @Async
  @EventListener
  public void onUserDisabled(UserBeforeDisableEvent event) {
    removeUser(event.getUserId());
  }

  @Override
  public UserInfo getItemById(Integer id) {
    return userService.getUserInfoById(id);
  }

  @Override
  public boolean searchTermIsID(String searchTerm) {
    return searchTerm.matches("\\d+");
  }

  @Override
  public Integer parseSearchTermAsID(String searchTerm) {
    return Integer.parseInt(searchTerm);
  }

  private void updateUser(Integer userId) {
    UserInfo user = getItemById(userId);
    removeItem(user);
    addItem(user);
  }

  private void removeUser(Integer userId) {
    UserInfo user = getItemById(userId);
    removeItem(user);
  }
}
