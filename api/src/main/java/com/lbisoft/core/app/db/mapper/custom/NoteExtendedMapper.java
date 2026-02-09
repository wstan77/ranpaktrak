package com.lbisoft.core.app.db.mapper.custom;

import java.util.List;

import com.lbisoft.core.app.components.notes.model.Note;
import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.components.notes.model.NoteSearchForm;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoteExtendedMapper {

  List<Note> search(@Param("form") NoteSearchForm form, @Param("userId") Integer userId);

  List<NoteReference> searchReferences(@Param("noteIds") List<Integer> noteIds);

  List<Integer> getNoteIdsToDelete();

  int cleanupUnsavedNotes(@Param("noteIds") List<Integer> noteIds);

  int cleanupUnsavedNoteReferences(@Param("noteIds") List<Integer> noteIds);
}
