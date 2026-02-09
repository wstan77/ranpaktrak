package com.lbisoft.core.app.components.notes.controller;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.notes.model.Note;
import com.lbisoft.core.app.components.notes.model.NoteSearchForm;
import com.lbisoft.core.app.components.notes.service.NoteService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.response.ListResponse;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/notes/")
@Tag(name = "Notes")
@PreAuthorize("hasAuthority('CAN_READ_NOTES')")
public class NoteController {
  static final String NOTES = "Notes";

  @Autowired
  private NoteService noteService;
  @Autowired
  private UserActivitiesService activitiesService;

  @GetMapping("/{noteId}")
  @Operation(summary = "Get a note for a given noteId", tags = { NOTES })
  public Note getNote(@PathVariable("noteId") Integer noteId, HttpServletRequest request) {
    activitiesService.logActivityById(ActivityTypes.LOAD_NOTE, noteId, request);
    return noteService.getNote(noteId, RequestHelper.getUser(request));
  }

  @GetMapping("/{noteId}/attachment")
  @Operation(summary = "Get a note's attachment for a given noteId", tags = { NOTES })
  public void getNoteAttachment(@PathVariable("noteId") Integer noteId, HttpServletRequest request,
      HttpServletResponse response) throws FileNotFoundException {
    noteService.sendNoteAttachment(noteId, response, request, RequestHelper.getUser(request));
  }

  @PreAuthorize("hasAuthority('CAN_EDIT_NOTES')")
  @PostMapping("/")
  @Operation(summary = "Save a note", tags = { NOTES })
  public ResponseObject saveNote(@RequestBody Note note, HttpServletRequest request) {
    return noteService.saveNote(note, request);
  }

  @PostMapping("/search")
  @Operation(summary = "Search for notes", tags = { NOTES })
  public ListResponse<Note> searchNotes(@RequestBody NoteSearchForm form, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.SEARCH_NOTES).relatedObject(form).build(),
        request);
    return noteService.searchNotes(form, RequestHelper.getUser(request));
  }
}
