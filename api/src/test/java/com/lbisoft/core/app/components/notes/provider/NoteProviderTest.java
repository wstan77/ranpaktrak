package com.lbisoft.core.app.components.notes.provider;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lbisoft.core.app.components.notes.model.Note;
import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.db.model.generated.NoteDb;

public class NoteProviderTest {

  private NoteDb db;
  private Note model;
  private final NoteProvider provider = new NoteProvider();
  private List<NoteReference> references = Collections.emptyList();

  @BeforeEach
  public void before() {
    LocalDateTime date = LocalDateTime.now();
    NoteReference ref = new NoteReference();
    ref.setNoteId(1);
    NoteReference ref2 = new NoteReference();
    ref2.setNoteId(2);
    references = List.of(ref, ref2);
    db = new NoteDb().withNoteId(1).withNoteTypeLk("noteType").withNoteDate(date.toLocalDate()).withRecordStatusLk("AP")
        .withSubject("subject").withComments("comments").withAttachment(new byte[10]).withFileName("fileName")
        .withFileExt("fileExt").withCreateUserId(123).withLastChangeUserId(456).withCreateDate(date)
        .withLastChangeDate(date).withSystemLastChangeDate(date);

    model = new Note();
    model.setNoteId(db.getNoteId());
    model.setNoteTypeLk(db.getNoteTypeLk());
    model.setNoteDate(db.getNoteDate());
    model.setRecordStatusLk(db.getRecordStatusLk());
    model.setSubject(db.getSubject());
    model.setComments(db.getComments());
    model.setAttachment(db.getAttachment());
    model.setFileName(db.getFileName());
    model.setFileExt(db.getFileExt());
    model.setCreateUserId(db.getCreateUserId());
    model.setLastChangeUserId(db.getLastChangeUserId());
    model.setCreateDate(date);
    model.setLastChangeDate(date);
    model.setSystemLastChangeDate(date);

    model.setReferences(references.stream().filter(r -> r.getNoteId().equals(model.getNoteId())).toList());

  }

  @Test
  public void prepareForDb() {
    NoteDb prepared = provider.reverseMap(model);
    assertThat(prepared).usingRecursiveComparison().isEqualTo(db);
  }

  @Test
  public void populateFromDb() {
    Note populated = provider.map(db, references);
    assertThat(populated).usingRecursiveComparison().isEqualTo(model);
  }
}
