package com.lbisoft.core.app.components.quicksearch.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.lbisoft.core.app.components.quicksearch.model.MyQuickSearchForm;
import com.lbisoft.core.app.components.user.event.UserGroupsChangedEvent;
import com.lbisoft.core.app.components.user.model.UserGroup;
import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.app.components.user.service.UserGroupService;
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
public class QuickSearchUserGroupService extends ChildQuickSearchService<Integer> {
  private static final Logger LOGGER = LoggerFactory.getLogger(QuickSearchUserGroupService.class);
  private final UserGroupService userGroupService;
  private Map<Integer, UserGroup> userGroupsById;

  public QuickSearchUserGroupService(UserGroupService userGroupService) {
    super();
    this.userGroupService = userGroupService;

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
    var userGroups = userGroupService.getActiveUserGroups();
    LOGGER.info("{} user groups loaded in {} ms", userGroups.size(), System.currentTimeMillis() - startTime);
    startTime = System.currentTimeMillis();
    userGroupsById = userGroups.stream().collect(Collectors.toMap(UserGroup::getUserGroupId, Function.identity()));
    FuzzyMatchSearchIndex<UserGroup, Integer> fuzzyIndex = new FuzzyMatchSearchIndex<>(userGroups);
    PrefixSearchIndex<UserGroup, Integer> prefixIndex = new PrefixSearchIndex<>(userGroups);
    this.indexes = List.of(fuzzyIndex, prefixIndex);
    LOGGER.info("{} user groups indexed in {} ms", userGroups.size(), System.currentTimeMillis() - startTime);

  }

  @Async
  @TransactionalEventListener(fallbackExecution = true)
  public void onUserGroupsChanged(UserGroupsChangedEvent event) {
    populate();
  }

  @Override
  public UserGroup getItemById(Integer id) {
    if (userGroupsById == null) {
      return null;
    }
    return userGroupsById.get(id);
  }

  @Override
  public boolean searchTermIsID(String searchTerm) {
    return searchTerm.matches("\\d+");
  }

  @Override
  public Integer parseSearchTermAsID(String searchTerm) {
    return Integer.parseInt(searchTerm);
  }
}
