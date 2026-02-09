package com.lbisoft.core.app.components.quicksearch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbisoft.core.app.components.quicksearch.model.UserSearch;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class UserSearchService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserSearchService() {
    }

    public void saveUserSearch(UserSearch userSearch, AppUser user) {
        LOGGER.info(
                "Performed search for " + userSearch.getSelectedTypeLk() + " with term: " + userSearch.getSearchTerm());
    }
}