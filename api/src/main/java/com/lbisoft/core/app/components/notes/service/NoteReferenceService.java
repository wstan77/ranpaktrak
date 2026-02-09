package com.lbisoft.core.app.components.notes.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.components.notes.provider.NoteReferenceProvider;
import com.lbisoft.core.app.components.quicksearch.model.ReferenceTag;
import com.lbisoft.core.app.components.quicksearch.model.Taggable;
import com.lbisoft.core.app.components.quicksearch.service.TopSearchService;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.NoteReferenceDbMapper;
import com.lbisoft.core.app.db.model.generated.NoteReferenceDb;
import com.lbisoft.core.app.db.model.generated.NoteReferenceDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class NoteReferenceService {
  private final Dao<NoteReferenceDb, Integer> dao;
  private final NoteReferenceProvider provider;
  private final TopSearchService tagService;

  @Autowired
  public NoteReferenceService(NoteReferenceDbMapper mapper, TopSearchService tagService) {
    this.dao = Dao.getDao(mapper, NoteReferenceDb::getNoteReferenceId);
    this.provider = new NoteReferenceProvider();
    this.tagService = tagService;
  }

  @Transactional(readOnly = true)
  public List<NoteReference> getNoteReferencesForNoteId(Integer noteId, AppUser user) {
    return getNoteReferencesForNoteIds(List.of(noteId), user);
  }

  @Transactional(readOnly = true)
  public List<NoteReference> getNoteReferencesForNoteIds(List<Integer> noteIds,
      AppUser user) {
    if (noteIds.isEmpty()) {
      return Collections.emptyList();
    }
    NoteReferenceDbExample example = new NoteReferenceDbExample();
    example.createCriteria().andNoteIdIn(noteIds);
    List<NoteReferenceDb> dbs = dao.select(example);
    return dbs.stream()
        .map(db -> {
          NoteReference reference = provider.map(db);
          reference.setTag(
              (Taggable) tagService.getItemById(new ReferenceTag(db.getReferenceTypeLk(), db.getReferenceId())));
          return reference;
        }).toList();
  }

  public int saveNoteReferences(List<NoteReference> noteReferences, Integer noteId,
      AppUser updateUser) {
    NoteReferenceDbExample example = new NoteReferenceDbExample();
    example.createCriteria().andNoteIdEqualTo(noteId);
    dao.delete(example);
    if (noteReferences == null) {
      return 0;
    }
    for (NoteReference ref : noteReferences) {
      ref.setNoteId(noteId);
    }
    return dao.insertOrUpdate(provider.reverseMapAll(noteReferences), updateUser);
  }

  public int insertNoteReferences(List<NoteReferenceDb> dbs, AppUser user) {
    return dao.insert(dbs, user);
  }
}
