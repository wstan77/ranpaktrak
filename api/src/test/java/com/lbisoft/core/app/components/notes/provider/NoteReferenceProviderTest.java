package com.lbisoft.core.app.components.notes.provider;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.components.quicksearch.model.QuickSearchTag;
import com.lbisoft.core.app.components.quicksearch.model.Taggable;
import com.lbisoft.core.app.db.model.generated.NoteReferenceDb;

public class NoteReferenceProviderTest {
  private NoteReferenceDb db;
  private NoteReference model;
  private NoteReferenceProvider provider = new NoteReferenceProvider();
  private List<Taggable> tags;

  @BeforeEach
  public void before() {
    LocalDateTime date = LocalDateTime.now();

    // will map because tag id = reference id and tag type = reference type
    QuickSearchTag tag = new QuickSearchTag();
    tag.setId(3);
    tag.setType("referenceType");
    // will not map because tag id != reference id
    QuickSearchTag badTag1 = new QuickSearchTag();
    badTag1.setId(6);
    badTag1.setType(tag.getType());
    // will not map because tag type != reference type
    QuickSearchTag badTag2 = new QuickSearchTag();
    badTag2.setId(tag.getId());
    badTag2.setType("differentReferenceType");
    tags = List.of(tag, badTag1, badTag2);

    db = new NoteReferenceDb().withNoteId(1).withNoteReferenceId(2).withReferenceId(tag.getId())
        .withReferenceTypeLk(tag.getType()).withCreateUserId(123).withLastChangeUserId(456).withCreateDate(date)
        .withLastChangeDate(date).withSystemLastChangeDate(date);

    model = new NoteReference();
    model.setNoteId(db.getNoteId());
    model.setNoteReferenceId(db.getNoteReferenceId());
    model.setCreateUserId(db.getCreateUserId());
    model.setLastChangeUserId(db.getLastChangeUserId());
    model.setCreateDate(db.getCreateDate());
    model.setLastChangeDate(db.getLastChangeDate());
    model.setSystemLastChangeDate(db.getSystemLastChangeDate());

    model.setTag(tag);
  }

  @Test
  public void prepareForDb() {
    NoteReferenceDb prepared = provider.reverseMap(model);
    assertThat(prepared).usingRecursiveComparison().isEqualTo(db);
  }

  @Test
  public void populateFromDb() {
    NoteReference populated = provider.map(db, tags);
    assertThat(populated).usingRecursiveComparison().isEqualTo(model);
  }
}
