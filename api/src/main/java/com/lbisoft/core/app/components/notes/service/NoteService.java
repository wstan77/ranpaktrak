package com.lbisoft.core.app.components.notes.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.notes.model.Note;
import com.lbisoft.core.app.components.notes.model.NoteReference;
import com.lbisoft.core.app.components.notes.model.NoteSearchForm;
import com.lbisoft.core.app.components.notes.provider.NoteProvider;
import com.lbisoft.core.app.components.notes.validate.NoteValidator;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.app.configuration.UnsavedCleanupJob;
import com.lbisoft.core.app.db.mapper.custom.NoteExtendedMapper;
import com.lbisoft.core.app.db.mapper.generated.NoteDbMapper;
import com.lbisoft.core.app.db.model.generated.NoteDb;
import com.lbisoft.core.app.db.model.generated.NoteDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.InsufficientPermissionException;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.permissions.PermissionsHelper;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.response.ListResponse;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.FileDownloadHelper;
import com.lbisoft.core.framework.utils.RequestHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CoreService
public class NoteService extends UnsavedCleanupJob {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  protected final NoteProvider provider;
  protected final Dao<NoteDb, Integer> dao;
  protected final NoteExtendedMapper extendedMapper;
  protected final NoteReferenceService noteReferenceService;
  private final UserActivitiesService activitiesService;

  @Autowired
  public NoteService(NoteDbMapper mapper, NoteExtendedMapper extendedMapper,
      NoteReferenceService noteReferenceService,
      UserActivitiesService activitiesService) {
    this.activitiesService = activitiesService;
    this.dao = Dao.getDao(mapper, NoteDb::getNoteId);
    this.provider = new NoteProvider();
    this.extendedMapper = extendedMapper;
    this.noteReferenceService = noteReferenceService;
  }

  @Transactional(readOnly = true)
  public Note getNote(Integer noteId, AppUser user) {
    NoteDbExample example = new NoteDbExample();
    example.createCriteria().andNoteIdEqualTo(noteId).andRecordStatusLkNotEqualTo(RecordStatusLks.DELETED);

    NoteDb noteDb = dao.selectOne(example).orElseThrow(ResourceNotFoundException::new);
    List<NoteReference> references = noteReferenceService.getNoteReferencesForNoteId(noteId, user);

    Note note = provider.map(noteDb, references);
    if (!PermissionsHelper.hasPermission(user, PermissionLevel.READ, note)) {
      throw new InsufficientPermissionException("You don't have permission to view note");
    }
    return note;
  }

  private Optional<Note> getNoteWithAttachment(Integer noteId, AppUser user) {
    return dao.select(noteId).filter(db -> !RecordStatusLks.DELETED.equals(db.getRecordStatusLk()))
        .map(provider::map)
        .filter(n -> n.getAttachment() != null
            && PermissionsHelper.hasPermission(user, PermissionLevel.READ, n));

  }

  @Transactional(readOnly = true)
  public void sendNoteAttachment(Integer noteId, HttpServletResponse response, HttpServletRequest request,
      AppUser user) {
    Note note = getNoteWithAttachment(noteId, user).orElseThrow(ResourceNotFoundException::new);
    try (InputStream fileStream = new ByteArrayInputStream(note.getAttachment())) {
      FileDownloadHelper.sendFile(fileStream, note.getFileName() + '.' + note.getFileExt(),
          note.getAttachment().length,
          false, response, request);
    } catch (IOException e) {
      LOGGER.error("Failed to fetch note attachment", e);
    }
  }

  public void deleteRelatedNotes(List<Integer> noteIds, AppUser user) {
    NoteDbExample example = new NoteDbExample();
    example.createCriteria().andNoteIdIn(noteIds.stream().filter(id -> id != null).toList());

    List<NoteDb> dbs = dao.select(example);
    dbs.forEach(note -> note.setRecordStatusLk(RecordStatusLks.DELETED));
    dao.update(dbs, user);
  }

  public Note saveNote(Note note, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);

    if (note.getNoteId() == null && note.getCreateUserId() == null) {
      note.setCreateUserId(user.getUserId());
    }
    if (RecordStatusLks.DELETED.equals(note.getRecordStatusLk())) {
      return deleteNote(note, request);
    }
    Note permissionNote = note.getNoteId() != null
        ? provider.map(dao.select(note.getNoteId()).orElseThrow(ResourceNotFoundException::new))
        : note;
    if (!PermissionsHelper.hasPermission(user, PermissionLevel.EDIT, permissionNote)) {
      throw new InsufficientPermissionException("You don't have permission to save note");
    }
    if (note.getRecordStatusLk() == null) {
      note.setRecordStatusLk(RecordStatusLks.APPROVED);
    }
    NoteDb db = provider.reverseMap(note);

    NoteValidator validator = new NoteValidator();
    validator.validate(note).validate();
    if (note.getNoteId() == null) {
      dao.insert(db, user);
      note.setNoteId(db.getNoteId());
    } else {
      setFieldsModified(note, db);
      dao.update(db, user);
    }
    noteReferenceService.saveNoteReferences(note.getReferences(), note.getNoteId(), user);
    note = getNote(note.getNoteId(), user);
    note.addResponseInfo("Note Saved Successfully");
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.SAVE_NOTE).relatedObject(note).build(),
        request);
    return note;
  }

  private void setFieldsModified(Note note, NoteDb db) {
    db.setCommentsModified(true);
    // Attachment data not necessarily included when updating
    db.setAttachmentModified(note.getFileExt() == null || note.getAttachment() != null);
  }

  public Note deleteNote(Note note, HttpServletRequest request) {
    AppUser user = RequestHelper.getUser(request);
    Note permissionNote = Optional.ofNullable(note.getNoteId()).flatMap(noteId -> dao.select(noteId))
        .map(provider::map)
        .orElseThrow(ResourceNotFoundException::new);
    if (!PermissionsHelper.hasPermission(user, PermissionLevel.DELETE, permissionNote)) {
      throw new InsufficientPermissionException("You don't have permission to delete note");
    }

    NoteDb deleteOperation = new NoteDb();
    deleteOperation.setNoteId(note.getNoteId());
    deleteOperation.setRecordStatusLk(RecordStatusLks.DELETED);

    dao.update(deleteOperation, user);

    note = provider.map(deleteOperation);
    note.addResponseInfo("Note Deleted Successfully");
    activitiesService.logActivityById(ActivityTypes.DELETE_NOTE, note.getNoteId(), request);
    return note;
  }

  @Transactional(readOnly = true)
  public ListResponse<Note> searchNotes(NoteSearchForm form, AppUser user) {
    form.setLimit(Constants.DEFAULT_QUERY_LIMIT + 1);
    List<Note> notes = extendedMapper.search(form, user.getUserId());
    List<Integer> noteIds = notes.stream().map(n -> n.getNoteId()).distinct().toList();
    if (noteIds.isEmpty()) {
      return new ListResponse<>(Collections.emptyList(), Constants.DEFAULT_QUERY_LIMIT);
    }
    List<NoteReference> noteReferences = extendedMapper.searchReferences(noteIds);
    provider.childrenMap.mapChildrenAll(notes, noteReferences);
    return new ListResponse<>(notes, Constants.DEFAULT_QUERY_LIMIT);
  }

  @Override
  public void cleanup(List<Integer> noteIds) {
    LOGGER.info("CLEANUP JOB: Cleaning up unsaved notes");
    extendedMapper.cleanupUnsavedNoteReferences(noteIds);
    extendedMapper.cleanupUnsavedNotes(noteIds);
    LOGGER.info("CLEANUP JOB: Successfully deleted unsaved notes");
  }

  @Override
  @Transactional(readOnly = true)
  public List<Integer> getAssetIdsToDelete() {
    return extendedMapper.getNoteIdsToDelete();
  }

  @Transactional(readOnly = true)
  public List<Note> getNotes(List<Integer> noteIds, AppUser user) {
    if (noteIds.isEmpty()) {
      return Collections.emptyList();
    }
    NoteDbExample example = new NoteDbExample();
    example.createCriteria().andNoteIdIn(noteIds).andRecordStatusLkEqualTo(RecordStatusLks.APPROVED);

    List<NoteReference> references = noteReferenceService.getNoteReferencesForNoteIds(noteIds, user);

    List<Note> notes = provider.mapAll(dao.select(example), references);

    return notes;

  }
}
