package com.lbisoft.core.app.components.notes.provider;

import com.lbisoft.core.app.components.notes.model.Note;
import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.db.model.generated.NoteDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class NoteProvider extends AbstractProvider<NoteDb, Note> {

  @Override
  protected void defineClassMap(ClassMapBuilder<NoteDb, Note> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<Note> childrenMap) {
    childrenMap
        .defineList(NoteReference.class, Note::setReferences,
            (note, reference) -> note.getNoteId().equals(reference.getNoteId()));
  }

  @Override
  protected NoteDb completeReverseMap(Note model, NoteDb db) {
    db.setAttachmentModified(true);
    db.setFileExtModified(true);
    db.setFileNameModified(true);
    return db;
  }
}
