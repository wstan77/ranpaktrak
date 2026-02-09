package com.lbisoft.core.app.components.quicksearch.service;

import java.util.Collections;
import java.util.List;

import com.lbisoft.core.app.Constants.ReferenceTypeLks;
import com.lbisoft.core.app.components.quicksearch.model.ReferenceTag;
import com.lbisoft.core.app.components.quicksearch.util.QuickSearchUtil;
import com.lbisoft.core.framework.search.factor.impl.AlphabeticalFactor;
import com.lbisoft.core.framework.search.factor.impl.EditDistanceFactor;
import com.lbisoft.core.framework.search.factor.impl.ExactMatchFactor;
import com.lbisoft.core.framework.search.service.ChildQuickSearchService;
import com.lbisoft.core.framework.search.service.CombinedQuickSearchService;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class TopSearchService extends CombinedQuickSearchService<Integer, ReferenceTag> {

  public TopSearchService(QuickSearchPersonService personService, QuickSearchUserService userService,
      QuickSearchUserGroupService userGroupService) {
    super();

    addFactor(new ExactMatchFactor<>());
    addFactor(new EditDistanceFactor<>());
    addFactor(new AlphabeticalFactor<>());

    register(ReferenceTypeLks.PERSON, personService);
    register(ReferenceTypeLks.USER, userService);
    register(ReferenceTypeLks.USER_GROUP, userGroupService);
  }

  @Override
  public boolean searchTermIsID(String searchTerm) {
    return searchTerm.matches("\\[\\w+?_\\d+\\]");
  }

  @Override
  public ReferenceTag parseSearchTermAsID(String searchTerm) {
    return QuickSearchUtil.parseTag(searchTerm);
  }

  @Override
  public Integer getId(ReferenceTag id) {
    return id != null ? id.getReferenceId() : null;
  }

  @Override
  public List<ChildQuickSearchService<Integer>> getServices(ReferenceTag id) {
    if (id == null) {
      return Collections.emptyList();
    }
    var service = getServices().get(id.getReferenceTypeLk());
    return service != null ? List.of(service) : Collections.emptyList();
  }
}
