package com.lbisoft.core.app.components.notes.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lbisoft.core.app.components.quicksearch.model.QuickSearchTag;
import com.lbisoft.core.app.components.quicksearch.model.Taggable;

public class NoteReference {
  private Integer noteReferenceId;
  private Integer noteId;
  @JsonDeserialize(as = QuickSearchTag.class)
  private Taggable tag = new QuickSearchTag();

  // audit info
  private LocalDateTime createDate;
  private Integer createUserId;
  private LocalDateTime lastChangeDate;
  private Integer lastChangeUserId;
  private LocalDateTime systemLastChangeDate;

  public Integer getNoteReferenceId() {
    return noteReferenceId;
  }

  public void setNoteReferenceId(Integer noteReferenceId) {
    this.noteReferenceId = noteReferenceId;
  }

  public Integer getNoteId() {
    return noteId;
  }

  public void setNoteId(Integer noteId) {
    this.noteId = noteId;
  }

  public LocalDateTime getCreateDate() {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
  }

  public Integer getCreateUserId() {
    return createUserId;
  }

  public void setCreateUserId(Integer createUserId) {
    this.createUserId = createUserId;
  }

  public LocalDateTime getLastChangeDate() {
    return lastChangeDate;
  }

  public void setLastChangeDate(LocalDateTime lastChangeDate) {
    this.lastChangeDate = lastChangeDate;
  }

  public Integer getLastChangeUserId() {
    return lastChangeUserId;
  }

  public void setLastChangeUserId(Integer lastChangeUserId) {
    this.lastChangeUserId = lastChangeUserId;
  }

  public LocalDateTime getSystemLastChangeDate() {
    return systemLastChangeDate;
  }

  public void setSystemLastChangeDate(LocalDateTime systemLastChangeDate) {
    this.systemLastChangeDate = systemLastChangeDate;
  }

  public Taggable getTag() {
    return tag;
  }

  public void setTag(Taggable tag) {
    this.tag = tag;
  }
}
