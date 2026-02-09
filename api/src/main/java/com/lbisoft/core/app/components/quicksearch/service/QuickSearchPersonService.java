package com.lbisoft.core.app.components.quicksearch.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.lbisoft.core.app.components.person.event.PersonBeforeDeleteEvent;
import com.lbisoft.core.app.components.person.event.PersonSavedEvent;
import com.lbisoft.core.app.components.person.model.QuickSearchPerson;
import com.lbisoft.core.app.components.quicksearch.model.MyQuickSearchForm;
import com.lbisoft.core.app.db.mapper.custom.QuickSearchExtendedMapper;
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
public class QuickSearchPersonService extends ChildQuickSearchService<Integer> {
  private static final Logger LOGGER = LoggerFactory.getLogger(QuickSearchPersonService.class);

  private final QuickSearchExtendedMapper mapper;

  public QuickSearchPersonService(QuickSearchExtendedMapper mapper) {
    super();
    this.mapper = mapper;

    addFactor(new ExactMatchFactor<>());
    addFactor(new EditDistanceFactor<>());
    addFactor(new AlphabeticalFactor<>());

    addFilter(ClassSpecificSearchFilter.builder(MyQuickSearchForm.class, QuickSearchPerson.class)
        .withFilter(this::filterByForm)
        .build());
  }

  @Override
  public void populate() {
    long startTime = System.currentTimeMillis();
    List<QuickSearchPerson> persons = mapper.getQuickSearchPersons();
    LOGGER.info("{} persons loaded in {} ms", persons.size(), System.currentTimeMillis() - startTime);
    startTime = System.currentTimeMillis();
    FuzzyMatchSearchIndex<QuickSearchPerson, Integer> fuzzyIndex = new FuzzyMatchSearchIndex<>(persons);
    PrefixSearchIndex<QuickSearchPerson, Integer> prefixIndex = new PrefixSearchIndex<>(persons);
    this.indexes = List.of(fuzzyIndex, prefixIndex);
    LOGGER.info("{} persons indexed in {} ms", persons.size(), System.currentTimeMillis() - startTime);

  }

  @Async
  @TransactionalEventListener(fallbackExecution = true)
  public void onPersonSaved(PersonSavedEvent event) {
    updatePerson(event.getPersonId());
  }

  @Async
  @EventListener
  public void onPersonDeleted(PersonBeforeDeleteEvent event) {
    removePerson(event.getPersonId());
  }

  private void updatePerson(Integer personId) {
    QuickSearchPerson person = getItemById(personId);
    removeItem(person);
    addItem(person);
  }

  private void removePerson(Integer personId) {
    QuickSearchPerson person = getItemById(personId);
    removeItem(person);
  }

  @Override
  public QuickSearchPerson getItemById(Integer id) {
    return mapper.getQuickSearchPersonById(id);
  }

  @Override
  public boolean searchTermIsID(String searchTerm) {
    return searchTerm.matches("\\d+");
  }

  @Override
  public Integer parseSearchTermAsID(String searchTerm) {
    return Integer.parseInt(searchTerm);
  }

  boolean filterByForm(MyQuickSearchForm form, QuickSearchPerson person) {
    if (form.getPersonTypeLks() != null && !form.getPersonTypeLks().contains(person.getPersonTypeLk())) {
      return false;
    }
    return !person.getPersonId().equals(form.getExcludeId());
  }

}
