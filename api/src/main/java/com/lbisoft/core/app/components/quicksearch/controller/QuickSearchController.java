package com.lbisoft.core.app.components.quicksearch.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.person.model.QuickSearchPerson;
import com.lbisoft.core.app.components.quicksearch.model.MyQuickSearchForm;
import com.lbisoft.core.app.components.quicksearch.model.QuickSearchTag;
import com.lbisoft.core.app.components.quicksearch.model.UserSearch;
import com.lbisoft.core.app.components.quicksearch.service.QuickSearchPersonService;
import com.lbisoft.core.app.components.quicksearch.service.QuickSearchUserGroupService;
import com.lbisoft.core.app.components.quicksearch.service.QuickSearchUserService;
import com.lbisoft.core.app.components.quicksearch.service.TopSearchService;
import com.lbisoft.core.app.components.quicksearch.service.UserSearchService;
import com.lbisoft.core.app.components.quicksearch.util.QuickSearchUtil;
import com.lbisoft.core.app.components.user.model.UserInfo;
import com.lbisoft.core.framework.search.model.SearchResult;
import com.lbisoft.core.framework.search.service.BaseQuickSearchService;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/quick-search")
@Tag(name = QuickSearchController.QUICK_SEARCH)
public class QuickSearchController {
  static final String QUICK_SEARCH = "Quick-Search";

  @Autowired
  private QuickSearchUserService userService;
  @Autowired
  private QuickSearchPersonService personService;
  @Autowired
  private UserSearchService userSearchService;
  @Autowired
  private QuickSearchUserGroupService userGroupService;
  @Autowired
  private TopSearchService topSearchService;

  @PostMapping("/user-search")
  @Operation(summary = "Log a user search", tags = { QUICK_SEARCH })
  public void logUserSearch(@RequestBody UserSearch search, HttpServletRequest request) {
    userSearchService.saveUserSearch(search, RequestHelper.getUser(request));
  }

  @PostMapping("/user")
  @Operation(summary = "Quick Search for a user by search term", tags = { QUICK_SEARCH })
  public List<UserInfo> searchForUser(@RequestBody MyQuickSearchForm form, HttpServletRequest request) {
    return getResults(userService, form);
  }

  @PostMapping("/user-group")
  @Operation(summary = "Quick Search for a user group by search term", tags = { QUICK_SEARCH })
  public List<UserInfo> searchForUserGroup(@RequestBody MyQuickSearchForm form, HttpServletRequest request) {
    return getResults(userGroupService, form);
  }

  @PostMapping("/person")
  @Operation(summary = "Quick Search for a person by search term", tags = { QUICK_SEARCH })
  public List<QuickSearchPerson> searchForPersons(@RequestBody MyQuickSearchForm form, HttpServletRequest request) {
    return getResults(personService, form);
  }

  @PostMapping("/tag")
  @Operation(summary = "Quick Search for a tag by search term", tags = { QUICK_SEARCH })
  public List<QuickSearchTag> searchForTag(@RequestBody MyQuickSearchForm form, HttpServletRequest request) {
    return getResults(topSearchService, form);
  }

  @GetMapping("/{type}/{id}")
  @Operation(summary = "Get a quick search item by its type and ID", tags = { QUICK_SEARCH })
  public Object getById(@PathVariable String type, @PathVariable String id, HttpServletRequest request) {
    switch (type) {
      case "user":
        return userService.getItemById(Integer.parseInt(id));
      case "person":
        return personService.getItemById(Integer.parseInt(id));
      case "tag":
        return topSearchService.getItemById(QuickSearchUtil.parseTag(id));
      default:
        return null;
    }
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> getResults(BaseQuickSearchService<?, ?> service, MyQuickSearchForm form) {
    return service.search(form).getResults().stream().map(SearchResult::getItem).map(item -> (T) item)
        .collect(Collectors.toList());
  }
}
